package es.iesjandula.reaktor.projectors_server.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.projectors_server.dtos.ResponseDto;
import es.iesjandula.reaktor.projectors_server.dtos.SimplifiedServerEventDto;
import es.iesjandula.reaktor.projectors_server.entities.Command;
import es.iesjandula.reaktor.projectors_server.entities.Projector;
import es.iesjandula.reaktor.projectors_server.entities.ServerEventHistory;
import es.iesjandula.reaktor.projectors_server.parsers.interfaces.ICommandParser;
import es.iesjandula.reaktor.projectors_server.parsers.interfaces.IProjectorParser;
import es.iesjandula.reaktor.projectors_server.repositories.ICommandRepository;
import es.iesjandula.reaktor.projectors_server.repositories.IProjectorRepository;
import es.iesjandula.reaktor.projectors_server.repositories.IServerEventHistoryRepository;
import es.iesjandula.reaktor.projectors_server.repositories.IServerEventRepository;
import es.iesjandula.reaktor.projectors_server.utils.Constants;
import es.iesjandula.reaktor.projectors_server.utils.ProjectorServerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/projectors")
public class ProjectorRemoteAgentController {

	@Autowired
	ICommandParser commandsParser;

	@Autowired
	IProjectorParser projectorParser;

	@Autowired
	IServerEventRepository serverEventRepository;

	@Autowired
	IProjectorRepository projectorRepository;

	@Autowired
	ICommandRepository commandRepository;

	@Autowired
	IServerEventHistoryRepository serverEventHistoryRepository;

	// -------------------------- SERVER EVENT METHODS -----------------------------

	/**
	 * Updates the status of a server event based on the provided response code
	 * (RARC) and classroom information.
	 *
	 * <p>
	 * This endpoint is secured and accessible only to users with the
	 * CLIENTE_PROYECTOR role.
	 * </p>
	 *
	 * @param eventId   the unique identifier of the server event to update
	 * @param rarc      the response code received from the projector
	 * @param classroom the classroom identifier where the projector is located
	 * @return a ResponseEntity containing a success message or an error response if
	 *         the operation fails
	 *
	 * @throws ProjectorServerException if the event ID, command, or status is
	 *                                  invalid
	 */
	@Transactional
	@PreAuthorize("hasRole('" + BaseConstants.ROLE_CLIENTE_PROYECTOR + "')")
	@PutMapping(value = "/server-events")
	public ResponseEntity<?> updateServerEventStatus(@RequestParam(name = "eventId") String eventId,
			@RequestParam(name = "rarc") String rarc, @RequestParam(name = "classroom") String classroom) {
		try {
			// Log the incoming request parameters for the event status update.
			log.info(
					"PUT request for '/server-events' received with parameters 'Event ID: {}, Response code: {}', Classroom: {}",
					eventId, rarc, classroom);

			// Prepare the response object to send back the status and message.
			ResponseDto response = new ResponseDto();
			String message;
			String eventNewStatus;

			// Fetch the name of the model from the
			String modelName = this.projectorRepository.findProjectorModelNameByClassroom(classroom);

			// Build the requesting model's command entity for comparison.
			Command command = this.commandRepository.findByModelNameAndCommand(modelName, rarc)
					.orElseThrow(() -> new ProjectorServerException(404,
							"El codigo de respuesta y modelo recibidos en la petición no corresponden a ningun comando registrado."));

			// Configure the event new status based on code received.
			eventNewStatus = command.getAction().equalsIgnoreCase(Constants.ACKNWOLEDGE_ACTION_NAME)
					? Constants.EVENT_STATUS_EXECUTED
					: Constants.EVENT_STATUS_ERROR;

			log.info("Status to be assigned: {}", eventNewStatus);

			// Validate that the new event status is part of the acceptable list.
			if (!Constants.POSSIBLE_EVENT_STATUS.contains(eventNewStatus)) {
				message = "Error updating event status: The selected status for the event does not exist.";
				log.error(message);
				throw new ProjectorServerException(499, message); // Error code for invalid event status.
			}

			// Convert eventId to Long
			Long eventIdLong = Long.valueOf(eventId);

			// Fetch the corresponding event from the database history.
			ServerEventHistory serverEventEntity = this.serverEventHistoryRepository.findById(eventIdLong)
					.orElseThrow(() -> {
						String messagex = "The server event with ID '" + eventIdLong + "' does not exist.";
						log.error(messagex);
						return new ProjectorServerException(494, messagex); // Error code for event not found.
					});

			// Capture the current status of the event before updating it.
			String oldStatus = serverEventEntity.getActionStatus();

			// Update the event's status to the new status.
			serverEventEntity.setActionStatus(eventNewStatus);

			// Prepare a success message with the old and new status.
			message = "Event with ID " + eventId + " successfully updated from " + oldStatus + " to " + eventNewStatus;
			log.info(message); // Log successful event status update.

			// Set the response status and message.
			response.setStatus(Constants.RESPONSE_STATUS_SUCCESS);
			response.setMessage(message);

			this.serverEventHistoryRepository.saveAndFlush(serverEventEntity);

			// Return the response with success status.
			return ResponseEntity.ok().body(response);

		} catch (ProjectorServerException e) {
			// Log the exception details and return a response with the error.
			log.error("Error updating event status", e);
			return ResponseEntity.internalServerError().body(e.getMapError());
		} catch (Exception e) {
			// Log the exception details and return a response with the error.
			log.error("Unexpected error updating event status", e);
			return ResponseEntity.internalServerError().body(e.getLocalizedMessage());
		}
	}

	// -----------------------------------------------------------------------------

	/**
	 * Handles GET requests to the "/server-events" endpoint.
	 * <p>
	 * This method processes a projector's reported status and returns the most recent
	 * pending server event assigned to that projector's classroom. It also updates the 
	 * projector's power status (ON/OFF) based on the received status code, and updates 
	 * the state of all pending server events accordingly.
	 * </p>
	 *
	 * @param projectorClassroom The identifier of the classroom where the projector is located.
	 * @param projectorStatus    The current status code reported by the projector (e.g., power state).
	 * @return A {@link ResponseEntity} containing the most recent pending server event for the projector,
	 *         or an appropriate error response if the projector or command is not found,
	 *         if there are no pending events, or if an unexpected error occurs.
	 *
	 * @throws ProjectorServerException If the projector is not found, if the command is invalid,
	 *                                  or if the projector status could not be updated.
	 */
	// @PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" +
	// BaseConstants.ROLE_CLIENTE_PROYECTOR + "')")
	@GetMapping(value = "/server-events")
	public ResponseEntity<?> serveCommandToController(@RequestParam(required = true) String projectorClassroom,
			@RequestParam(required = true) String projectorStatus) 
	{

		// Log the incoming request for processing models.
		log.info("GET request for '/server-events' received with classroom '{}' and satus {}.", projectorClassroom, projectorStatus);

		try {
			
			// Recupera el proyector o lanza error si no existe.
			Projector projectorEntity = this.projectorRepository.findById(projectorClassroom).orElseThrow(() 
					-> new ProjectorServerException(494, "ERROR: There are no projectors assigned to this classroom.")
			);
			
			log.info(projectorEntity.toString());

			// Recupera listado eventos servidor para este proyector que estan en pendiente.
			List<ServerEventHistory> serverEventsList = this.serverEventHistoryRepository
					.findRecentPendingServerEventsByClassroom(projectorClassroom);

			// Actualiza estado encendido/apagado del proyector.
			String modelName = this.projectorRepository.findProjectorModelNameByClassroom(projectorClassroom);
			
			log.info(modelName);

			// Build the requesting model's command entity for comparison.
			Command statusCommand = this.commandRepository.findByModelNameAndCommand(modelName, projectorStatus)
					.orElseThrow(() 
							-> new ProjectorServerException(404,"El codigo de estado de la petición no corresponden a ningun comando registrado.")
							);
			
			log.info(statusCommand.toString());
			
			if (statusCommand.getAction().equalsIgnoreCase(Constants.LAMP_ON)) {
				
				projectorEntity.setStatus(Constants.PROJECTOR_ON);
				
			} else if (statusCommand.getAction().equalsIgnoreCase(Constants.LAMP_OFF)) {
				
				projectorEntity.setStatus(Constants.PROJECTOR_OFF);
				
			} else {
				
				throw new ProjectorServerException(499, "Error al registrar el estado del proyector. Estado desconocido.");
				
			}
			
			log.info(projectorEntity.getStatus());
			
			// Guarda el estado del proyector antes de continuar..
			this.projectorRepository.saveAndFlush(projectorEntity);

			if (serverEventsList.isEmpty()) {
				return ResponseEntity.noContent().build();
			}

			// Evento servidor mas reciente de todos.
			ServerEventHistory mostRecentEvent = serverEventsList.get(0);

			// Configura evento simplificado.
			SimplifiedServerEventDto simpleEvent = new SimplifiedServerEventDto();
			simpleEvent.setActionStatus(mostRecentEvent.getActionStatus());
			simpleEvent.setCommandInstruction(mostRecentEvent.getCommand());
			simpleEvent.setEventId(mostRecentEvent.getEventId());

			// Bloque que actualiza los estados de los otros eventos en pendiente que
			// ya no deberan ser servidos en la proxima peticion de recientes.
			for (ServerEventHistory serverEvent : serverEventsList) {
				if (serverEvent.equals(serverEventsList.get(0))) {
					serverEvent.setActionStatus(Constants.EVENT_STATUS_SERVED);
				} else {
					serverEvent.setActionStatus(Constants.EVENT_STATUS_CANCELED);
				}
			}

			this.serverEventHistoryRepository.saveAllAndFlush(serverEventsList);

			return ResponseEntity.ok().body(simpleEvent);

		} catch (ProjectorServerException e) {
			return ResponseEntity.internalServerError().body(e.getMapError());
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body( e.getLocalizedMessage());
		}
	}

	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_CLIENTE_PROYECTOR + "')")
	@GetMapping(value = "/config-params")
	public ResponseEntity<?> serveConfigParamsToRemoteAgents(@RequestParam(required = true) String projectorClassroom) {
		try {

			// Recupera el proyector o lanza error si no existe.
			Projector projectorEntity = this.projectorRepository.findById(projectorClassroom).orElseThrow(() ->
				new ProjectorServerException(494, "ERROR: There are no projectors assigned to this classroom.")
			);

			// Busca el comando relativo a la interrogación de estado del proyector. 
			// Es necesario servir este comando a los proyectores cuando se inicializan para que sepan "como preguntar" el estado de la lampara.
			Command statusInquiryCommand = this.commandRepository
					.findByModelNameAndAction(projectorEntity.getModel(), Constants.STATUS_INQUIRY_COMMAND)
					.orElseThrow(() -> new ProjectorServerException(494,
							"ERROR: There are no inquiry commands assigned to this projector model."));

			// Devuelve el comando al cliente.
			return ResponseEntity.ok().body(statusInquiryCommand);

		} catch (ProjectorServerException e) {
			return ResponseEntity.internalServerError().body(e.getMapError());
		}
	}

}
