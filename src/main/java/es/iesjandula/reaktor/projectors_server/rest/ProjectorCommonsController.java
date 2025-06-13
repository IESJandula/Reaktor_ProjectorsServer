package es.iesjandula.reaktor.projectors_server.rest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.aspectj.apache.bcel.classfile.ConstantString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.base.security.models.DtoUsuarioExtended;
import es.iesjandula.reaktor.base.utils.BaseConstants;
import es.iesjandula.reaktor.projectors_server.dtos.ActionDto;
import es.iesjandula.reaktor.projectors_server.dtos.ClassroomDto;
import es.iesjandula.reaktor.projectors_server.dtos.EventFilterObject;
import es.iesjandula.reaktor.projectors_server.dtos.FloorDto;
import es.iesjandula.reaktor.projectors_server.dtos.ProjectorDto;
import es.iesjandula.reaktor.projectors_server.dtos.ProjectorModelDto;
import es.iesjandula.reaktor.projectors_server.dtos.ResponseDto;
import es.iesjandula.reaktor.projectors_server.dtos.ServerEventBatchDto;
import es.iesjandula.reaktor.projectors_server.dtos.TableServerEventDto;
import es.iesjandula.reaktor.projectors_server.entities.Command;
import es.iesjandula.reaktor.projectors_server.entities.Projector;
import es.iesjandula.reaktor.projectors_server.entities.ServerEvent;
import es.iesjandula.reaktor.projectors_server.entities.ServerEventHistory;
import es.iesjandula.reaktor.projectors_server.entities.ids.CommandId;
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
public class ProjectorCommonsController {

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

	/**
	 * Creates a server event for a projector with the specified model, classroom,
	 * and action.
	 * 
	 * This method retrieves the necessary entities (projector model, classroom,
	 * projector, and action) from the database, validates the inputs, and creates a
	 * new server event with the appropriate details.
	 * 
	 * @param projectorModelName The model name of the projector.
	 * @param projectorClassroom The classroom where the projector is located.
	 * @param commandActionName  The action to be performed on the projector.
	 * @return A ServerEvent entity populated with the correct details.
	 * @throws ProjectorServerException If any of the entities cannot be found or if
	 *                                  input parameters are invalid.cla
	 */
	private ServerEvent createServerEventEntity(ProjectorDto projectorDto, String commandActionName, String userEmail)
			throws ProjectorServerException {

		String model = projectorDto.getModel();
		String classroom = projectorDto.getClassroom();

		// Check if any of the parameters are null or empty/blank string.
		if (model == null || model.isBlank() || classroom == null || classroom.isBlank() || commandActionName == null
				|| commandActionName.isBlank()) {
			// if blank or null throw exception.
			String exceptionMessage = "Null parameter received during server event creation.";
			log.error(exceptionMessage);
			throw new ProjectorServerException(505, exceptionMessage);
		}

		// Log the action for traceability.
		log.info("Creating '{}' event for projector '{}' in classroom '{}'.", commandActionName, model, classroom);

		/* -------------- FORMING PROJECTOR ENTITY -------------- */

		// Retrieve the projector entity using the primary key.
		Projector projectorEntity = this.projectorRepository.findById(classroom).orElseThrow(() -> {
			return new ProjectorServerException(494,
					String.format("The projector model '{}' in classroom '{}' does not exist.", model, classroom));
		});

		log.debug("PROJECTOR UNIT RETRIEVED: {}", projectorEntity);
						
		/* -------------------- END FORMING PROJECTOR ENTITY -------------------- */

		/* -------------------- RETRIEVE COMMAND ENTITY -------------------- */

		// Retrieve the command entity for the given projector model and action.
		CommandId commandId = new CommandId();
		commandId.setAction(commandActionName);
		commandId.setModelName(model);

		Optional<Command> commandOpt = this.commandRepository.findById(commandId);

		Command commandEntity = commandOpt.orElseThrow(() -> {
			String exceptionMessage = String.format(
					"No command found in the database for model '%s' to perform action '%s'.", model,
					commandActionName);

			log.error(exceptionMessage);
			return new ProjectorServerException(494, exceptionMessage);
		});

		log.debug("COMMAND RETRIEVED: {}", commandEntity);

		/* -------------------- END RETRIEVE COMMAND ENTITY -------------------- */

		/* -------------- SET OTHER PARAMETERS FOR THE EVENT -------------- */

		// Get the current date and time for the event timestamp.
		LocalDateTime dateTime = LocalDateTime.now();

		String user = userEmail;

		// Default value of event status.
		String eventStatus = Constants.EVENT_STATUS_PENDING;
		
		// Si el proyector está encendido y la orden es de encendido, ponerlo como ejecutado.
		if ( projectorEntity.getStatus().equalsIgnoreCase(Constants.PROJECTOR_ON) || projectorEntity.getStatus().equalsIgnoreCase(Constants.PROJECTOR_TURNING_ON) )
		{
			if ( commandActionName.equalsIgnoreCase(Constants.TURN_ON_ACTION_NAME ) ) {
				eventStatus = Constants.EVENT_STATUS_EXECUTED;
			}
			if ( commandActionName.equalsIgnoreCase(Constants.TURN_OFF_ACTION_NAME) ) {
				projectorEntity.setStatus( Constants.PROJECTOR_TURNING_OFF );
			}
		}
		else if (  projectorEntity.getStatus().equalsIgnoreCase(Constants.PROJECTOR_OFF) || projectorEntity.getStatus().equalsIgnoreCase(Constants.PROJECTOR_TURNING_OFF) )  
		{
			if ( commandActionName.equalsIgnoreCase(Constants.TURN_ON_ACTION_NAME) ) {
				projectorEntity.setStatus( Constants.PROJECTOR_TURNING_ON );
			}
			if ( commandActionName.equalsIgnoreCase(Constants.TURN_OFF_ACTION_NAME) ) {
				eventStatus = Constants.EVENT_STATUS_EXECUTED;
			}
		}
				
		// Create and populate the server event entity.
		ServerEvent serverEventEntity = new ServerEvent();
		serverEventEntity.setCommand(commandEntity);
		serverEventEntity.setProjector(projectorEntity);
		serverEventEntity.setActionStatus(eventStatus);
		serverEventEntity.setDateTime(dateTime);
		serverEventEntity.setUser(user);

		// Log the event creation process.
		log.info("Server event successfully created for projector '{}', action '{}', in classroom '{}' for user '{}'.",
				model, commandActionName, classroom, user);

		// Return the populated server event entity.
		return serverEventEntity;
	}

	private ServerEventHistory createServerEventHistoryFromServerEntity(ServerEvent serverEvent) {
		
		ServerEventHistory serverEventHistory = new ServerEventHistory();

		serverEventHistory.setModelName(serverEvent.getCommand().getModelName());
		serverEventHistory.setAction(serverEvent.getCommand().getAction());
		serverEventHistory.setCommand(serverEvent.getCommand().getCommand());
		serverEventHistory.setClassroom(serverEvent.getProjector().getClassroom());
		serverEventHistory.setFloor(serverEvent.getProjector().getFloor());
		serverEventHistory.setUser(serverEvent.getUser());
		serverEventHistory.setDateTime(serverEvent.getDateTime());
		serverEventHistory.setActionStatus(serverEvent.getActionStatus());
		
		log.info("Status of the new historic record: {} ", serverEventHistory.getAction() );

		return serverEventHistory;
	}

	/**
	 * Retrieves a list of all floors recorded in the database.
	 * 
	 * This endpoint fetches all available floor details from the repository and
	 * returns them as a list of FloorDto objects.
	 * 
	 * Note: This method is only for front-end QOL improvements.
	 * 
	 * @return A ResponseEntity containing the list of floors, or a no-content
	 *         response if no floors are found.
	 */
	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_PROFESOR + "')")
	@GetMapping(value = "/floors")
	public ResponseEntity<?> getFloorList() {
		log.info("GET request for '/floors' received.");

		try {
			// Retrieve all floor data from the repository
			List<FloorDto> floors = this.projectorRepository.findAllFloorAsDtos();

			// Log the number of retrieved floors (avoiding large data dump in the logs).
			log.info("Retrieved {} floor(s).", floors.size());

			// Return the list of floors, or no content if the list is empty.
			if (floors.isEmpty()) {
				log.warn("No floors found in the database.");
				return ResponseEntity.noContent().build(); // Returning HTTP 204 No Content.
			}

			return ResponseEntity.ok().body(floors);

		} catch (Exception e) {
			// Log any error or exception that might occur.
			log.error("Error occurred while retrieving floor list: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError()
					.body("An error occurred while fetching floors from the database.");
		}
	}

	/**
	 * Retrieves a list of classrooms on the specified floor.
	 * 
	 * This endpoint accepts a floor name and returns a list of classrooms located
	 * on that floor. If no classrooms are found, it returns a 204 No Content
	 * status. If an error occurs, a 500 Internal Server Error response is returned.
	 * 
	 * @param floor The floor name to filter the classrooms.
	 * @return A list of classrooms for the specified floor or an appropriate error
	 *         message.
	 */
	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_PROFESOR + "')")
	@GetMapping(value = "/classrooms")
	public ResponseEntity<?> getClassroomList(@RequestParam(required = true) String floor) {
		log.info("GET request for '/classrooms' received with floor parameter '{}'.", floor);

		try {
			// Fetch the list of classrooms for the given floor
			List<ClassroomDto> classrooms = this.projectorRepository.findClassroomsByFloorNameAsDto(floor);

			// If no classrooms were found, return a 204 No Content status
			if (classrooms.isEmpty()) {
				log.warn("No classrooms found for floor '{}'.", floor);
				return ResponseEntity.noContent().build(); // HTTP 204 No Content
			}

			// Log the number of classrooms retrieved without logging large data dumps
			log.info("Successfully retrieved {} classroom(s) for floor '{}'.", classrooms.size(), floor);

			// Return the list of classrooms with an HTTP 200 OK status
			return ResponseEntity.ok().body(classrooms);

		} catch (Exception e) {
			// Log any errors that occur during the process
			log.error("Error occurred while retrieving classrooms for floor '{}': {}", floor, e.getMessage(), e);
			return ResponseEntity.internalServerError().body("An error occurred while fetching classroom data.");
		}
	}

	/**
	 * Retrieves a list of all actions.
	 * 
	 * This endpoint fetches and returns a list of all available actions.
	 * 
	 * @return A list of actions or a 204 No Content status if no actions are
	 *         available.
	 */
	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_PROFESOR + "')")
	@GetMapping(value = "/actions")
	public ResponseEntity<?> getActionsList() {
		log.info("GET request for '/actions' received.");

		try {
			// Fetch the list of actions from the repository
			List<ActionDto> actions = this.commandRepository.findActionsAsDto();

			// Remove ACK and ERROR and LAMP Status response codes.
			actions.remove(new ActionDto(Constants.ACKNWOLEDGE_ACTION_NAME));
			actions.remove(new ActionDto(Constants.ERROR_ACTION_NAME));
			actions.remove(new ActionDto(Constants.LAMP_OFF));
			actions.remove(new ActionDto(Constants.LAMP_ON));
			actions.remove(new ActionDto(Constants.STATUS_INQUIRY_COMMAND));

			// Return the list of actions with an HTTP 200 OK status
			log.info("Successfully retrieved {} action(s).", actions.size());
			return ResponseEntity.ok().body(actions);

		} catch (Exception e) {
			// Log any unexpected errors that occur
			log.error("Error occurred while retrieving actions list: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body("An error occurred while fetching actions data.");
		}
	}

	/**
	 * Retrieves a list of all projector models from the database.
	 * <p>
	 * This endpoint queries the database to fetch all the available projector
	 * models in the form of a list of {@link ProjectorModelDto}. If no models are
	 * found, it returns a 404 Not Found response with a relevant message. In case
	 * of a server error during the retrieval process, a 500 Internal Server Error
	 * response is returned with a message indicating an unexpected error.
	 * </p>
	 * 
	 * @return ResponseEntity<?> The response entity containing the status code and
	 *         either the list of projector models (HTTP 200 OK) or an error message
	 *         (HTTP 404 Not Found or HTTP 500 Internal Server Error).
	 * 
	 * @throws Exception If an error occurs while retrieving the projector models
	 *                   from the database.
	 */
	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_PROFESOR + "')")
	@GetMapping("/projector-models")
	public ResponseEntity<?> getModelsList() {
		try {
			// Log the start of the retrieval process
			log.info("GET request for '/projector-models' received.");

			// Retrieve the list of projector models from the database
			List<ProjectorModelDto> projectorModelList = this.commandRepository.findAllProjectorModelsDto();

			// Log the successful retrieval of the models
			log.info("Successfully retrieved {} projector models from the database.", projectorModelList.size());

			// Return a 200 OK response with the list of projector models
			return ResponseEntity.status(HttpStatus.OK).body(projectorModelList);

		} catch (Exception e) {
			// Define an error message
			String message = "An unexpected error occurred while retrieving the projector model list.";

			// Log the error in case of failure with stack trace
			log.error(message, e);

			// Return a 500 Internal Server Error response with an error message
			return ResponseEntity.internalServerError().body(message);
		}
	}

	/**
	 * Endpoint to create a batch of server events for multiple projectors.
	 * 
	 * This method receives a batch of projector events, iterates over them, and
	 * creates corresponding server events. The events are then persisted in the
	 * database in a single transaction.
	 * 
	 * @param serverEventBatchDto DTO containing the details of the server events to
	 *                            be created.
	 * @return A ResponseEntity containing the status and message of the operation.
	 */
	@Transactional
	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_PROFESOR + "')")
	@PostMapping(value = "/server-events-batch")
	public ResponseEntity<?> createServerEventBatch(@AuthenticationPrincipal DtoUsuarioExtended usuario,
			@RequestBody(required = true) ServerEventBatchDto serverEventBatchDto) {
		try {
			log.debug("POST request for /server-events-batch received");

			// Prepare the response object with success status and message
			ResponseDto response = new ResponseDto();

			// Extract action name from the request DTO
			String commandActionName = serverEventBatchDto.getAction();

			// Extract user email from user dto.
			String userEmail = usuario.getEmail();
			// Extract list of projectors from the request DTO
			List<ProjectorDto> projectorList = serverEventBatchDto.getProjectorList();

			if (projectorList.isEmpty()) {
				response.setMessage("NULL PROJECTOR LIST.");
				response.setStatus("ERROR");
				return ResponseEntity.badRequest().body(response);
			}

			// Initialize list to hold the server events to be saved
			List<ServerEvent> serverEventList = new ArrayList<>();
			// Initialize list to hold the server events to be saved
			List<ServerEventHistory> serverEventHistoryList = new ArrayList<>();

			// Loop through each projector and create a corresponding server event
			for (ProjectorDto projectorDto : projectorList) {
				// Create server event for each projector and add it to the list
				ServerEvent serverEventEntity = this.createServerEventEntity(projectorDto, commandActionName,
						userEmail);
				serverEventList.add(serverEventEntity);
				serverEventHistoryList.add(createServerEventHistoryFromServerEntity(serverEventEntity));
			}

			// Log the number of events being saved for traceability
			log.info("Creating and saving {} server events to the database, including history table.",
					serverEventList.size());

			// Persist all server events in a single transaction for efficiency
			this.serverEventRepository.saveAllAndFlush(serverEventList);
			this.serverEventHistoryRepository.saveAllAndFlush(serverEventHistoryList);

			// Log successful operation
			log.info("{} server events successfully created and saved to the database.", serverEventList.size());

			response.setMessage(serverEventList.size() + " events successfully created.");
			response.setStatus(Constants.RESPONSE_STATUS_SUCCESS);

			// Return a success response with the created events count
			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (ProjectorServerException ex) {
			// Log the exception error for debugging purposes
			log.error("Error occurred while processing server event batch: {}", ex.getMessage(), ex);

			// Return a bad request response with error details
			return ResponseEntity.badRequest().body(ex.getMapError());
		} catch (Exception e) {
			// Log the exception details and return a response with the error.
			log.error("Unexpected error creating batch of server events", e);
			return ResponseEntity.internalServerError().body(e.getLocalizedMessage());
		}
	}

	@GetMapping("/event-states")
	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_PROFESOR + "')")
	public ResponseEntity<?> getEventStatusList() {
		return ResponseEntity.ok().body(Constants.POSSIBLE_EVENT_STATUS);
	}

	@PreAuthorize("hasAnyRole('" + BaseConstants.ROLE_ADMINISTRADOR + "', '" + BaseConstants.ROLE_PROFESOR + "')")
	@PostMapping("/server-events")
	public ResponseEntity<?> getEventsPage(@RequestBody(required = false) EventFilterObject eventFilterObject,
			@PageableDefault(page = 0, size = 10) Pageable pageable) {
		log.info("POST request for '/server-events' received.");

		log.debug("Classroom: {}", eventFilterObject.getClassroomName());
		log.debug("Floor: {}", eventFilterObject.getFloorName());
		log.debug("Model: {}", eventFilterObject.getModelName());
		log.debug("getActionStatus: {}", eventFilterObject.getActionStatus());
		log.debug("getActionName: {}", eventFilterObject.getActionName());
		log.debug("getEventId: {}", eventFilterObject.getEventId());
		log.debug("getUser: {}", eventFilterObject.getUser());
		log.debug("getDateTime: {}", eventFilterObject.getDateTime());

		Page<TableServerEventDto> pagina = this.serverEventHistoryRepository.getFilteredServerEventDtosPage(pageable,
				eventFilterObject.getClassroomName(), eventFilterObject.getFloorName(),
				eventFilterObject.getModelName(), eventFilterObject.getActionStatus());

		log.debug("Recuperados:" + pagina.toList().size());

		return ResponseEntity.ok().body(pagina);
	}

}
