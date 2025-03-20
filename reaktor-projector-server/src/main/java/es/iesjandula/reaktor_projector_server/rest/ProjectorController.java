package es.iesjandula.reaktor_projector_server.rest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.iesjandula.reaktor_projector_server.dtos.ActionDto;
import es.iesjandula.reaktor_projector_server.dtos.ClassroomDto;
import es.iesjandula.reaktor_projector_server.dtos.CommandDto;
import es.iesjandula.reaktor_projector_server.dtos.EventFilterObject;
import es.iesjandula.reaktor_projector_server.dtos.FloorDto;
import es.iesjandula.reaktor_projector_server.dtos.GeneralCountOverviewDto;
import es.iesjandula.reaktor_projector_server.dtos.ProjectorDto;
import es.iesjandula.reaktor_projector_server.dtos.ProjectorInfoDto;
import es.iesjandula.reaktor_projector_server.dtos.ProjectorModelDto;
import es.iesjandula.reaktor_projector_server.dtos.ResponseDto;
import es.iesjandula.reaktor_projector_server.dtos.RichResponseDto;
import es.iesjandula.reaktor_projector_server.dtos.ServerEventBatchDto;
import es.iesjandula.reaktor_projector_server.dtos.ServerEventOverviewDto;
import es.iesjandula.reaktor_projector_server.dtos.SimplifiedServerEventDto;
import es.iesjandula.reaktor_projector_server.dtos.TableServerEventDto;
import es.iesjandula.reaktor_projector_server.entities.Action;
import es.iesjandula.reaktor_projector_server.entities.Command;
import es.iesjandula.reaktor_projector_server.entities.Projector;
import es.iesjandula.reaktor_projector_server.entities.ProjectorModel;
import es.iesjandula.reaktor_projector_server.entities.ServerEvent;
import es.iesjandula.reaktor_projector_server.entities.ids.CommandId;
import es.iesjandula.reaktor_projector_server.parsers.interfaces.ICommandParser;
import es.iesjandula.reaktor_projector_server.parsers.interfaces.IProjectorModelParser;
import es.iesjandula.reaktor_projector_server.parsers.interfaces.IProjectorParser;
import es.iesjandula.reaktor_projector_server.repositories.IActionRepository;
import es.iesjandula.reaktor_projector_server.repositories.ICommandRepository;
import es.iesjandula.reaktor_projector_server.repositories.IProjectorModelRepository;
import es.iesjandula.reaktor_projector_server.repositories.IProjectorRepository;
import es.iesjandula.reaktor_projector_server.repositories.IServerEventRepository;
import es.iesjandula.reaktor_projector_server.utils.Constants;
import es.iesjandula.reaktor_projector_server.utils.ProjectorServerException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProjectorController
{

	@Autowired
	ICommandParser commandsParser;

	@Autowired
	IProjectorParser projectorParser;

	@Autowired
	IProjectorModelParser projectorModelsParser;

	@Autowired
	IServerEventRepository serverEventRepository;

	@Autowired
	IProjectorRepository projectorRepository;

	@Autowired
	IProjectorModelRepository projectorModelRepository;

	@Autowired
	ICommandRepository commandRepository;

	@Autowired
	IActionRepository actionRepositories;

	// ----------------------------- UTILITY METHODS -------------------------------

	/**
	 * Validates the uploaded CSV file.
	 * <p>
	 * Ensures the uploaded file is not empty and has a valid content type (CSV). If
	 * validation fails, a {@link ProjectorServerException} is thrown with an
	 * appropriate error code and message.
	 * </p>
	 *
	 * @param file The uploaded {@link MultipartFile} to be validated.
	 * @throws ProjectorServerException if the file is empty or has an invalid
	 *                                  content type.
	 */
	private void validateFile(MultipartFile file) throws ProjectorServerException
	{
		log.info("Validation file method called for file {}", file.getName());

		String message;
		// Check if the file is empty
		if (file.isEmpty())
		{
			message = "File validation failed: Received an empty CSV file.";
			log.error(message);
			throw new ProjectorServerException(490, message);
		}

		String contentType = file.getContentType();

		// Check if the file content type is valid for CSV files
		if (contentType == null || !contentType.equals("text/csv"))
		{
			message = "File validation failed: Unsupported file format received. Expected 'text/csv', but got: "
					+ contentType;
			log.error(message);
			throw new ProjectorServerException(498, message);
		}

		log.info("CSV file validation successful.");
	}

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
	 *                                  input parameters are invalid.
	 */
	private ServerEvent createServerEventEntity(String projectorModelName, String projectorClassroom,
			String commandActionName) throws ProjectorServerException
	{
		// Check if any of the parameters is null or empty/blank string.
		if (projectorModelName == null || projectorModelName.isBlank() || projectorClassroom == null
				|| projectorClassroom.isBlank() || commandActionName == null || commandActionName.isBlank())
		{
			// if blank or null throw exception.
			String exceptionMessage = "Null parameter received while during server event creation.";
			log.error(exceptionMessage);
			throw new ProjectorServerException(505, exceptionMessage);
		}

		// Log the action for traceability.
		log.info("Creating '{}' event for projector '{}' in classroom '{}'.", commandActionName, projectorModelName,
				projectorClassroom);

		/* -------------- FORMING PROJECTOR ENTITY -------------- */
		// Retrieve the projector model entity from the database.
		Optional<ProjectorModel> projectorModelOpt = this.projectorModelRepository.findById(projectorModelName);

		// If the model exists, store in entity, otherwise throw an exception.
		ProjectorModel projectorModelEntity = projectorModelOpt.orElseThrow(() ->
		{
			String exceptionMessage = "The projector model '" + projectorModelName + "' does not exist.";
			log.error(exceptionMessage);
			return new ProjectorServerException(494, exceptionMessage);
		});

		log.debug("PROJECTOR MODEL RETRIEVED: {}", projectorModelEntity);

		// Retrieve the projector entity using the composite key.
		Optional<Projector> projectorOpt = this.projectorRepository.findById(projectorClassroom);

		Projector projectorEntity = projectorOpt.orElseThrow(() ->
		{
			String exceptionMessage = "The projector model '" + projectorModelName + "' in classroom '"
					+ projectorClassroom + "' does not exist.";
			log.error(exceptionMessage);
			return new ProjectorServerException(494, exceptionMessage);
		});

		log.debug("PROJECTOR UNIT RETRIEVED: {}", projectorEntity);

		/* -------------------- END FORMING PROJECTOR ENTITY -------------------- */

		/* -------------------- RETRIEVE COMMAND ENTITY -------------------- */

		// Retrieve the action entity from the repository.
		Optional<Action> actionOpt = this.actionRepositories.findById(commandActionName);

		Action actionEntity = actionOpt.orElseThrow(() ->
		{
			String exceptionMessage = "The given action '" + commandActionName + "' does not exist.";
			log.error(exceptionMessage);
			return new ProjectorServerException(494, exceptionMessage);
		});

		log.debug("COMMAND ACTION RETRIEVED: {}", actionEntity);

		// Retrieve the command entity for the given projector model and action.
		Optional<Command> commandOpt = this.commandRepository.findByModelNameAndAction(projectorModelEntity,
				actionEntity);

		Command commandEntity = commandOpt.orElseThrow(() ->
		{
			String exceptionMessage = "No command found in DB for model '" + projectorModelName
					+ "' to perform action '" + commandActionName + "'.";
			log.error(exceptionMessage);
			return new ProjectorServerException(494, exceptionMessage);
		});

		log.debug("COMMAND RETRIEVED: {}", commandEntity);

		/* -------------------- END RETRIEVE COMMAND ENTITY -------------------- */

		/* -------------- SET OTHER PARAMETERS FOR THE EVENT -------------- */

		// Get the current date and time for the event timestamp.
		LocalDateTime dateTime = LocalDateTime.now();

		// Placeholder for user identification (can be updated as per requirement).
		String user = "TO DO"; // TODO: Replace with actual user identification logic

		// Set the default status of the event to pending from constants class.
		String defaultStatus = Constants.EVENT_STATUS_PENDING;

		// Create and populate the server event entity.
		ServerEvent serverEventEntity = new ServerEvent();
		serverEventEntity.setCommand(commandEntity);
		serverEventEntity.setProjector(projectorEntity);
		serverEventEntity.setActionStatus(defaultStatus);
		serverEventEntity.setDateTime(dateTime);
		serverEventEntity.setUser(user);

		// Log the event creation process.
		log.info("Server event successfully created for projector '{}', action '{}', in classroom '{}' for user '{}'.",
				projectorModelName, commandActionName, projectorClassroom, user);

		// Return the populated server event entity.
		return serverEventEntity;
	}

	// ----------------------------- PARSING METHODS -------------------------------

	/**
	 * Handles the parsing of multiple CSV files: projectors, commands, and
	 * classrooms. This endpoint accepts optional files and processes them
	 * accordingly. If no files are provided, it returns an error response.
	 * <p>
	 * The method performs the following steps:
	 * <ul>
	 * <li>Validates the received files.</li>
	 * <li>Parses the projectors file if provided.</li>
	 * <li>Parses the commands file if provided.</li>
	 * <li>Parses the classrooms file if provided.</li>
	 * <li>Returns a structured response with parsing results.</li>
	 * </ul>
	 * </p>
	 *
	 * @param classroomsFile Multipart file containing classroom data (Optional).
	 * @param projectorsFile Multipart file containing projector data (Optional).
	 * @param commandsFile   Multipart file containing command data (Optional).
	 * @return ResponseEntity containing a success message or error response.
	 * @throws ProjectorServerException If an issue occurs related to the server
	 *                                  logic.
	 * @throws IOException              If there is an error reading the provided
	 *                                  files.
	 * @throws Exception                If an unexpected error occurs.
	 */
	@Transactional
	@PostMapping("/parse-multifile")
	public ResponseEntity<?> parseMultifile(
			@RequestParam(value = "projectors.csv", required = false) MultipartFile projectorsFile,
			@RequestParam(value = "commands.csv", required = false) MultipartFile commandsFile)
	{
		try
		{
			// Log the incoming request to parse commands
			log.info("POST request for '/parse-multifile' received.");

			// Initialize message and response DTO for structured results.
			String message = "";
			RichResponseDto richResponseDto = new RichResponseDto();

			// Check if no files were received, and return an error message.
			if ((commandsFile == null || commandsFile.isEmpty())
					&& (projectorsFile == null || projectorsFile.isEmpty()))
			{
				message = "No files received for parse operation.";
				log.error(message);
				throw new ProjectorServerException(498, message); // Custom error for no files received.
			}

			// Process projectors file if provided.
			if (projectorsFile != null && !projectorsFile.isEmpty())
			{
				log.info("Processing 'projectors.csv' file.");
				try (Scanner scanner = new Scanner(projectorsFile.getInputStream()))
				{
					this.validateFile(projectorsFile); // Validate the file before processing.
					richResponseDto.setMessage2(projectorParser.parseProjectors(scanner)); // Parse projectors.
					richResponseDto.setStatus2(Constants.RESPONSE_STATUS_SUCCESS);
				} catch (ProjectorServerException e)
				{
					richResponseDto.setMessage2(e.getMessage()); // Parse commands.
					richResponseDto.setStatus2(Constants.RESPONSE_STATUS_ERROR);
				}
			} else
			{
				log.info("No 'projectors.csv' file received.");
				richResponseDto.setMessage2("The request did not include a file for projectors.");
				richResponseDto.setStatus2(Constants.RESPONSE_STATUS_WARNING);
			}

			// Process commands file if provided.
			if (commandsFile != null && !commandsFile.isEmpty())
			{
				log.info("Processing 'commands.csv' file.");
				try (Scanner scanner = new Scanner(commandsFile.getInputStream()))
				{
					this.validateFile(commandsFile); // Validate the file before processing.
					richResponseDto.setMessage1(this.commandsParser.parseCommands(scanner)); // Parse commands.
					richResponseDto.setStatus1(Constants.RESPONSE_STATUS_SUCCESS);
				} catch (ProjectorServerException e)
				{
					richResponseDto.setMessage1(e.getMessage()); // Parse commands.
					richResponseDto.setStatus1(Constants.RESPONSE_STATUS_ERROR);
				}
			} else
			{
				log.info("No 'commands.csv' file received.");
				richResponseDto.setMessage1("The request did not include a file for commands.");
				richResponseDto.setStatus1(Constants.RESPONSE_STATUS_WARNING);
			}

			// Log the final parsing results.
			log.info("Commands: {}\nProjectors: {}", richResponseDto.getMessage1(), richResponseDto.getMessage2());

			// Return the structured response DTO with success status.
			return ResponseEntity.ok(richResponseDto);

		} catch (ProjectorServerException e)
		{
			// Handle custom exceptions (e.g., invalid input, no files received).
			log.error("Projector server error: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body(e.getMapError());

		} catch (IOException e)
		{
			// Handle IO errors when reading the files.
			log.error("Error reading the file: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body("Error encountered while reading the file.");

		} catch (Exception e)
		{
			// Catch any unexpected errors.
			log.error("Unexpected error: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body("ERROR: Unexpected exception occurred.");
		}
	}

	// ----------------------- FLOORS & CLASSROOMS METHODS -------------------------

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
	@GetMapping(value = "/floors")
	public ResponseEntity<?> getFloorList()
	{
		log.info("GET request for '/floors' received.");

		try
		{
			// Retrieve all floor data from the repository
			List<FloorDto> floors = this.projectorRepository.findAllFloorAsDtos();

			// Log the number of retrieved floors (avoiding large data dump in the logs).
			log.info("Retrieved {} floor(s).", floors.size());

			// Return the list of floors, or no content if the list is empty.
			if (floors.isEmpty())
			{
				log.warn("No floors found in the database.");
				return ResponseEntity.noContent().build(); // Returning HTTP 204 No Content.
			}

			return ResponseEntity.ok().body(floors);

		} catch (Exception e)
		{
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
	@GetMapping(value = "/classrooms")
	public ResponseEntity<?> getClassroomList(@RequestParam(required = true) String floor)
	{
		log.info("GET request for '/classrooms' received with floor parameter '{}'.", floor);

		try
		{
			// Fetch the list of classrooms for the given floor
			List<ClassroomDto> classrooms = this.projectorRepository.findClassroomsByFloorNameAsDto(floor);

			// If no classrooms were found, return a 204 No Content status
			if (classrooms.isEmpty())
			{
				log.warn("No classrooms found for floor '{}'.", floor);
				return ResponseEntity.noContent().build(); // HTTP 204 No Content
			}

			// Log the number of classrooms retrieved without logging large data dumps
			log.info("Successfully retrieved {} classroom(s) for floor '{}'.", classrooms.size(), floor);

			// Return the list of classrooms with an HTTP 200 OK status
			return ResponseEntity.ok().body(classrooms);

		} catch (Exception e)
		{
			// Log any errors that occur during the process
			log.error("Error occurred while retrieving classrooms for floor '{}': {}", floor, e.getMessage(), e);
			return ResponseEntity.internalServerError().body("An error occurred while fetching classroom data.");
		}
	}

	// ---------------------------- ACTIONS METHODS --------------------------------

	/**
	 * Handles HTTP DELETE requests to remove a list of actions from the system.
	 * <p>
	 * This method receives a list of actions (provided as DTOs), validates their
	 * existence, and deletes them from the database. If any action does not exist,
	 * an exception is thrown.
	 * </p>
	 * 
	 * @param actionsList The list of {@link ActionDto} objects representing the
	 *                    actions to be deleted.
	 * @return ResponseEntity containing a success message if actions are deleted
	 *         successfully, or an error message in case of failure.
	 */
	@DeleteMapping(value = "/actions")
	public ResponseEntity<?> deleteActions(@RequestBody List<ActionDto> actionsList)
	{
		try
		{
			// Logging the DELETE request for monitoring and debugging
			log.info("DELETE request for '/actions' received with parameter '{}'.", actionsList);

			// Validate that the received list is not empty
			if (actionsList.isEmpty())
			{
				String message = "Delete actions list is empty.";
				log.error(message);
				throw new ProjectorServerException(499, message);
			}

			// List to store actions that will be deleted
			List<Action> actionsToDelete = new ArrayList<>();

			// Iterate through the received list and validate if each action exists
			for (ActionDto action : actionsList)
			{
				Action actionEntity = this.actionRepositories.findById(action.getActionName()).orElseThrow(() ->
				{
					String message = "Action '" + action + "' does not exist.";
					log.error(message);
					return new ProjectorServerException(499, message);
				});

				actionsToDelete.add(actionEntity);
			}

			// Delete all found actions from the database
			this.actionRepositories.deleteAll(actionsToDelete);

			// Create a response object to indicate successful deletion
			ResponseDto response = new ResponseDto();
			String message = actionsToDelete.size() + " actions deleted successfully.";
			log.info(message);
			response.setMessage(message);
			response.setStatus(Constants.RESPONSE_STATUS_SUCCESS);

			return ResponseEntity.ok().body(response);
		}
		// Handle custom exception when an action is not found
		catch (ProjectorServerException e)
		{
			log.error(e.getMessage());
			return ResponseEntity.badRequest().body(e.getMapError());
		}
		// Handle any unexpected exceptions
		catch (Exception e)
		{
			String message = "Unexpected error while deleting actions. " + e;
			log.error(message);
			return ResponseEntity.internalServerError().body("Unexpected error while deleting actions.");
		}
	}

	/**
	 * Retrieves a paginated list of actions.
	 * 
	 * This endpoint returns a paginated list of actions using the provided
	 * pagination parameters. If no actions are found, a 204 No Content status is
	 * returned.
	 * 
	 * @param pageable The pagination information (page number, size).
	 * @return A paginated response with the list of actions.
	 */
	@PostMapping(value = "/actions-page")
	public ResponseEntity<?> getActionsPage(@PageableDefault(page = 0, size = 15) Pageable pageable)
	{
		log.info("POST request for '/actions-page' received with pagination: page={}, size={}.",
				pageable.getPageNumber(), pageable.getPageSize());

		try
		{
			// Fetch paginated actions list
			Page<ActionDto> actionsPage = this.actionRepositories.findAllActionsAsDtoPaged(pageable);

			// Log the successful retrieval of the actions page with pagination info
			log.info("Successfully retrieved page {} of actions, total pages: {}.", pageable.getPageNumber(),
					actionsPage.getTotalPages());

			// Return paginated actions with additional pagination metadata
			return ResponseEntity.ok().body(actionsPage);

		} catch (Exception e)
		{
			// Log any unexpected errors
			log.error("Error occurred while retrieving actions page: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body("An error occurred while fetching actions data.");
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
	@GetMapping(value = "/actions")
	public ResponseEntity<?> getActionsList()
	{
		log.info("GET request for '/actions' received.");

		try
		{
			// Fetch the list of actions from the repository
			List<ActionDto> actions = this.actionRepositories.findAllActionsAsDto();

			// Return the list of actions with an HTTP 200 OK status
			log.info("Successfully retrieved {} action(s).", actions.size());
			return ResponseEntity.ok().body(actions);

		} catch (Exception e)
		{
			// Log any unexpected errors that occur
			log.error("Error occurred while retrieving actions list: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body("An error occurred while fetching actions data.");
		}
	}

	// ---------------------------- PROJECTOR METHODS ------------------------------

	/**
	 * Retrieves a paginated list of projectors based on optional filters and
	 * sorting criteria.
	 * <p>
	 * This endpoint allows filtering by classroom, floor, or model. If a sorting
	 * criteria is provided, the list can be ordered by model name; otherwise, the
	 * default ordering is by floor and classroom.
	 * </p>
	 * 
	 * @param criteria  (Optional) Sorting criteria. If set to "modelname",
	 *                  projectors are ordered by model name. Any other value or
	 *                  null defaults to ordering by floor and classroom.
	 * @param classroom (Optional) Filter by classroom. If null, no classroom filter
	 *                  is applied.
	 * @param floor     (Optional) Filter by floor. If null, no floor filter is
	 *                  applied.
	 * @param model     (Optional) Filter by projector model. If null, no model
	 *                  filter is applied.
	 * @param pageable  Pagination and sorting parameters, including page number and
	 *                  page size.
	 * @return A paginated list of {@link ProjectorInfoDto} objects wrapped in
	 *         {@link ResponseEntity}. - **200 OK**: Successfully retrieved
	 *         projectors. - **204 No Content**: No projectors found matching the
	 *         filters. - **500 Internal Server Error**: An unexpected error
	 *         occurred.
	 */
	@GetMapping("/projectors")
	public ResponseEntity<Page<ProjectorInfoDto>> getProjectorList(
			@RequestParam(value = "criteria", required = false) String criteria,
			@RequestParam(value = "classroom", required = false) String classroom,
			@RequestParam(value = "floor", required = false) String floor,
			@RequestParam(value = "model", required = false) String model,
			@PageableDefault(page = 0, size = 15) Pageable pageable)
	{
		try
		{

			log.info("GET request received for '/projectors' with criteria: {}", criteria);

			Page<ProjectorInfoDto> projectors;

			boolean orderByModel = criteria != null && !criteria.isBlank()
					&& Constants.PROJECTORS_ORDER_CRITERIA_MODELNAME.equalsIgnoreCase(criteria.trim());

			// Determine ordering strategy based on criteria
			if (orderByModel)
			{
				log.debug("Applying ordering by model name.");
				projectors = projectorRepository.findProjectorsOrderedByModel(pageable, classroom, floor, model);
			} else
			{
				log.debug("Applying default ordering by floor and classroom.");
				projectors = projectorRepository.findProjectorsOrderedByFloorAndClassroom(pageable, classroom, floor,
						model);
			}

			log.info("Successfully retrieved {} projectors.", projectors.getTotalElements());
			return ResponseEntity.ok(projectors);

		} catch (Exception e)
		{
			log.error("Error while retrieving projectors: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body(Page.empty());
		}
	}

	/**
	 * Removes the selected projectors from the database.
	 * <p>
	 * This method handles the removal of one or more projectors from a classroom by
	 * performing the following checks: 1. Verifies that the provided classroom
	 * exists. 2. Verifies that the provided projector model exists. 3. Ensures that
	 * the projector assignment to the classroom exists before proceeding with
	 * removal. 4. If any error occurs during the process (e.g., classroom or
	 * projector model does not exist), appropriate exceptions are thrown.
	 * </p>
	 *
	 * @param projectorDto The Data Transfer Object (DTO) containing the classroom
	 *                     and projector model information to be removed.
	 *
	 * @return ResponseEntity The response entity containing the status of the
	 *         operation: - If the removal is successful, returns HTTP status 200
	 *         (OK) with a success message. - If any errors are encountered, returns
	 *         HTTP status 500 (Internal Server Error) with an error message.
	 *
	 * @throws ProjectorServerException If: - The classroom does not exist. - The
	 *                                  projector model does not exist. - The
	 *                                  projector assignment does not exist.
	 */
	@Transactional
	@DeleteMapping("/projectors")
	public ResponseEntity<?> deleteProjector(@RequestBody List<ProjectorInfoDto> projectorDtoList)
	{
		try
		{
			log.info("DELETE request for '/projectors' received.");

			// Response DTO for returning status
			ResponseDto responseDto = new ResponseDto();
			String message;

			List<Projector> projectorEntitiesiList = new ArrayList();

			for (ProjectorInfoDto projectorDto : projectorDtoList)
			{
				String classroomName = projectorDto.getClassroom();
				String modelName = projectorDto.getModel();

				// Check if the projector assignment exists.
				Projector projectorEntity = projectorRepository.findById(classroomName).orElseThrow(() ->
				{
					String errorMessage = "Projector " + projectorDto + " does not exist.";
					log.error(errorMessage);
					return new ProjectorServerException(499, errorMessage);
				});

				// Log the projector assignment found.
				log.debug("Projector assignment found for model '{}' in classroom '{}'.", modelName, classroomName);

				// Add the current projector for later deletion order.
				projectorEntitiesiList.add(projectorEntity);

				// Log successful removal.
				message = "Projector " + modelName + " from classroom " + classroomName + "added to remove list.";
				log.info(message);
			}

			this.projectorRepository.deleteAll(projectorEntitiesiList);

			// Set the response DTO.
			message = String.format("Successfully removed %d projectors.", projectorEntitiesiList.size());
			log.info(message);
			responseDto.setStatus(Constants.RESPONSE_STATUS_SUCCESS);
			responseDto.setMessage(message);

			// Return the success response.
			return ResponseEntity.status(HttpStatus.OK).body(responseDto);

		} catch (DataIntegrityViolationException e)
		{
			log.error("Projector removal failed due to data integrity issues: {}", e.getMessage());
			return ResponseEntity.internalServerError().body(e.getMessage());
		}

		catch (ProjectorServerException e)
		{
			// Log the error for known exceptions (e.g., classroom or model not found).
			log.error("Projector removal failed: {}", e.getMessage());
			return ResponseEntity.internalServerError().body(e.getMapError());
		} catch (Exception e)
		{
			// Log the error for unexpected exceptions
			log.error("Unexpected error encountered while removing projector: {}", e.getMessage());
			return ResponseEntity.internalServerError().body("Unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * Handles the deletion of all projectors from the database.
	 * 
	 * <p>
	 * This endpoint removes all projector records in a single batch operation. If
	 * no projectors exist, a warning response is returned instead of performing an
	 * unnecessary delete operation.
	 * </p>
	 * 
	 * @return ResponseEntity containing a success or warning message.
	 */
	@Transactional
	@DeleteMapping("/projectors-all")
	public ResponseEntity<?> deleteAllProjectors()
	{
		log.info("Received DELETE request for '/projectors-all'.");

		try
		{
			// Retrieve all projectors from the database
			List<Projector> projectors = projectorRepository.findAll();
			ResponseDto responseDto = new ResponseDto();
			String message;

			// Check if there are any projectors to delete
			if (projectors.isEmpty())
			{
				message = "No projectors found to delete.";
				log.warn("Deletion skipped: {}", message);
				responseDto.setStatus(Constants.RESPONSE_STATUS_WARNING);
				responseDto.setMessage(message);
				return ResponseEntity.ok(responseDto);
			}

			// Delete all projectors in batch (efficient bulk operation)
			projectorRepository.deleteAllInBatch();

			// Log successful deletion
			message = String.format("Successfully removed %d projectors.", projectors.size());
			log.info("Deletion successful: {}", message);

			// Prepare and return success response
			responseDto.setStatus(Constants.RESPONSE_STATUS_SUCCESS);
			responseDto.setMessage(message);
			return ResponseEntity.ok(responseDto);

		} catch (Exception e)
		{
			// Log the exception with full stack trace for debugging
			log.error("Error occurred while deleting projectors: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body("Unexpected error occurred: " + e.getMessage());
		}
	}

	/**
	 * Retrieves a projector assigned to a specific classroom.
	 * <p>
	 * Fetches the projector associated to the classroom sent as parameter. If the
	 * projector is not found, it returns a 404 Not Found response with a relevant
	 * error message. If an unexpected error occurs, a 500 Internal Server Error
	 * response is returned.
	 * </p>
	 *
	 * @param classroom The name of the classroom for which the projector needs to
	 *                  be fetched. This parameter is required and should not be
	 *                  blank.
	 * 
	 * @return ResponseEntity containing the projector (HTTP 200 OK) or an error
	 *         message if the classroom does not exist (HTTP 404 Not Found), or an
	 *         internal server error message (HTTP 500 Internal Server Error).
	 */
	@GetMapping(value = "/classroom-projector")
	public ResponseEntity<?> getProjectorByClassroom(@RequestParam(required = true) String classroom)
	{
		try
		{
			// Log the incoming request to retrieve the projector
			log.info("GET request for '/classroom-projector' received with classroom: '{}'", classroom);

			// Check if the projector for the classroom exists
			Projector projectorEntity = this.projectorRepository.findById(classroom).orElseThrow(() ->
			{
				// Log and throw an exception if the projector is not found
				String message = "Projector for classroom '" + classroom + "' does not exist.";
				log.warn(message); // Log as warning
				return new ProjectorServerException(404, message);
			});

			// Log the successful retrieval of the projector
			log.info("Projector for classroom '{}' successfully retrieved: {}", classroom, projectorEntity);

			// Return the retrieved projector with HTTP status 200 (OK)
			return ResponseEntity.ok().body(projectorEntity);

		} catch (ProjectorServerException e)
		{
			// Log the expected exception (classroom not found) and return a 404 response
			log.error("Projector not found for classroom '{}': {}", classroom, e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMapError());

		} catch (Exception e)
		{
			// Log any unexpected errors and return a 500 (Internal Server Error) response
			String errorMessage = "Unexpected error encountered while retrieving projector for classroom: " + classroom;
			log.error(errorMessage, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage + ": " + e.getMessage());
		}
	}

	// ------------------------------ MODEL METHODS --------------------------------

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
	@GetMapping("/projector-models")
	public ResponseEntity<?> getModelsList()
	{
		try
		{
			// Log the start of the retrieval process
			log.info("GET request for '/projector-models' received.");

			// Retrieve the list of projector models from the database
			List<ProjectorModelDto> projectorModelList = this.projectorModelRepository.findAllProjectorModelsAsDto();

			// Log the successful retrieval of the models
			log.info("Successfully retrieved {} projector models from the database.", projectorModelList.size());

			// Return a 200 OK response with the list of projector models
			return ResponseEntity.status(HttpStatus.OK).body(projectorModelList);

		} catch (Exception e)
		{
			// Define an error message
			String message = "An unexpected error occurred while retrieving the projector model list.";

			// Log the error in case of failure with stack trace
			log.error(message, e);

			// Return a 500 Internal Server Error response with an error message
			return ResponseEntity.internalServerError().body(message);
		}
	}

	// ---------------------------- COMMANDS METHODS -------------------------------

	/**
	 * Retrieves the list of commands associated with a specific projector model.
	 * <p>
	 * This method retrieves commands associated with a given projector model. If
	 * the model exists in the system, the commands are fetched and returned. If the
	 * model does not exist, a 499 error is thrown. If no commands are found for the
	 * model, a 204 No Content status is returned. In case of any other unexpected
	 * errors, a 500 Internal Server Error status with the error message is returned.
	 * </p>
	 *
	 * @param modelname The name of the projector model for which the commands need
	 *                  to be fetched. This parameter is required.
	 * 
	 * @return ResponseEntity containing the list of commands (HTTP 200 OK), no
	 *         content (HTTP 204), or an error message (HTTP 499 or 500).
	 */
	@GetMapping(value = "/commands")
	public ResponseEntity<?> getProjectorModelCommands(@RequestParam(required = true) String modelname)
	{
		try
		{
			// Check if the given projector model exists in the repository
			Optional<ProjectorModel> modelOptional = this.projectorModelRepository.findById(modelname);
			String message;

			if (modelOptional.isEmpty())
			{
				// Log and throw an error if the model does not exist
				message = "The selected projector model does not exist.";
				log.error(message); // Log the error at ERROR level
				throw new ProjectorServerException(499, message);
			}

			log.debug("Projector model '{}' retrieved successfully.", modelname); // Log successful retrieval at DEBUG
																					// level

			// Retrieve commands associated with the model
			List<CommandDto> commands = this.commandRepository.findCommandsByModelNameAsDto(modelname);

			// If no commands are found, return 204 No Content
			if (commands.isEmpty())
			{
				log.info("No commands found for model '{}'. Returning 204 No Content.", modelname); // Log info at INFO
																									// level
				return ResponseEntity.noContent().build(); // HTTP 204 No Content
			}

			// Log the number of commands retrieved successfully
			log.debug("{} commands retrieved successfully for model '{}'.", commands.size(), modelname);

			// Return the list of commands with HTTP 200 OK
			return ResponseEntity.ok().body(commands);
		} catch (ProjectorServerException e)
		{
			// Log and return a custom error response for known errors
			// (ProjectorServerException)
			log.error("An error occurred while retrieving commands for model '{}': {}", modelname, e.getMapError());
			return ResponseEntity.badRequest().body(e.getMapError()); // Return HTTP 400 Bad Request
		} catch (Exception e)
		{
			// Log and return a generic error response for unexpected errors
			log.error("An unexpected error occurred while retrieving commands for model '{}': {}", modelname,
					e.getLocalizedMessage());
			return ResponseEntity.internalServerError().body(e.getLocalizedMessage()); // Return HTTP 400 Bad Request
		}
	}

	/**
	 * Retrieves a paginated list of commands based on the specified model and
	 * action.
	 * 
	 * This endpoint checks if the provided model and action exist, then fetches the
	 * commands for the given parameters with pagination. If no parameters are sent
	 * then it recovers all the commands unfiltered.
	 * 
	 * @param pageable  Pagination details (page number, size, etc.).
	 * @param modelName The model name to filter commands (optional).
	 * @param action    The action name to filter commands (optional).
	 * @return A response containing the paginated list of commands.
	 */
	@PostMapping(value = "/commands-page")
	public ResponseEntity<?> getCommandsPage(@PageableDefault(page = 0, size = 15) Pageable pageable,
			@RequestParam(name = "modelName", required = false) String modelName,
			@RequestParam(name = "action", required = false) String action)
	{
		try
		{

			// Log the request.
			log.debug("POST request for '/commands-page' received with modelName: {}, action: {}", modelName, action);

			// Validate if the model exist.
			if (modelName != null && !this.projectorModelRepository.existsById(modelName))
			{
				String errorMessage = "The selected model '" + modelName + "' does not exist.";
				log.error("Model validation failed: {}", errorMessage);
				throw new ProjectorServerException(404, errorMessage);
			}

			// Validate if the action exist.
			if (action != null && !this.actionRepositories.existsById(action))
			{
				String errorMessage = "The selected action '" + action + "' does not exist.";
				log.error("Action validation failed: {}", errorMessage);
				throw new ProjectorServerException(404, errorMessage);
			}

			// Fetch the commands using pagination
			Page<CommandDto> commands = this.commandRepository.findAllCommandsPage(pageable, modelName, action);

			// Return the paginated response
			log.info("Successfully retrieved commands page.");
			return ResponseEntity.ok().body(commands);

		} catch (ProjectorServerException e)
		{
			// Log error and return a bad request response with the error details
			log.error("Error encountered during commands page retrieval: {}", e.getMapError());
			return ResponseEntity.badRequest().body(e.getMapError());

		} catch (Exception e)
		{
			// Log unexpected error and return a 500 internal server error response
			log.error("Unexpected error during commands page retrieval: {}", e.getMessage(), e);
			return ResponseEntity.internalServerError().body("An unexpected error occurred.");
		}
	}

	@DeleteMapping(value = "/commands")
	public ResponseEntity<?> deleteCommands(@RequestBody List<CommandDto> commandsList)
	{
		try
		{
			log.debug("DELETE request for '/commands' received with parameter {}", commandsList);

			List<Command> commandsToDelete = new ArrayList<>();
			int recordsDeleted = 0;

			for (CommandDto command : commandsList)
			{

				Action actionEntity = this.actionRepositories.findById(command.getAction()).get();
				log.debug("Action retreived");

				ProjectorModel modelEntity = this.projectorModelRepository.findById(command.getModelName()).get();
				log.debug("ProjectorModel retreived");

				CommandId commandId = new CommandId();

				commandId.setAction(actionEntity);
				commandId.setModelName(modelEntity);
				commandId.setCommand(command.getCommand());
				log.debug("CommandId retreived");

				Command commandEntity = this.commandRepository.findById(commandId).get();
				log.debug("Command retreived");

				commandsToDelete.add(commandEntity);
				recordsDeleted++;

			}

			this.commandRepository.deleteAll(commandsToDelete);

			return (ResponseEntity.ok().body("Deleted " + recordsDeleted + " commands from DB."));

		}

		// HACER QUE EL FRONT RECIBA UN AVISO AL INTENTAR BORRAR ESTO Y PERMITA BORRAR
		// EN CASCADA.
		catch (DataIntegrityViolationException e)
		{

			return (ResponseEntity.ok().body("ERROR EN BORRADO POR INTEGRIDAD. \n" + e.getMessage()));
		}
	}

	// -------------------------- SERVER EVENT METHODS -----------------------------

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
	@PostMapping(value = "/server-events-batch")
	public ResponseEntity<?> createServerEventBatch(
			@RequestBody(required = true) ServerEventBatchDto serverEventBatchDto)
	{
		try
		{
			log.debug("POST request for /server-events-batch received");

			// Prepare the response object with success status and message
			ResponseDto response = new ResponseDto();

			// Extract action name from the request DTO
			String commandActionName = serverEventBatchDto.getAction();

			// Extract list of projectors from the request DTO
			List<ProjectorDto> projectorList = serverEventBatchDto.getProjectorList();

			if (projectorList.isEmpty())
			{
				response.setMessage("NULL PROJECTOR LIST.");
				response.setStatus("ERROR");
				return ResponseEntity.status(HttpStatus.CREATED).body(response);
			}

			// Initialize list to hold the server events to be saved
			List<ServerEvent> serverEventList = new ArrayList<>();

			// Loop through each projector and create a corresponding server event
			for (ProjectorDto projectorDto : projectorList)
			{
				// Create server event for each projector and add it to the list
				serverEventList.add(this.createServerEventEntity(projectorDto.getModel(), projectorDto.getClassroom(),
						commandActionName));
			}

			// Log the number of events being saved for traceability
			log.info("Creating and saving {} server events to the database.", serverEventList.size());

			// Persist all server events in a single transaction for efficiency
			this.serverEventRepository.saveAllAndFlush(serverEventList);

			// Log successful operation
			log.info("{} server events successfully created and saved to the database.", serverEventList.size());

			response.setMessage(serverEventList.size() + " events successfully created.");
			response.setStatus(Constants.RESPONSE_STATUS_SUCCESS);

			// Return a success response with the created events count
			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (ProjectorServerException ex)
		{
			// Log the exception error for debugging purposes
			log.error("Error occurred while processing server event batch: {}", ex.getMessage(), ex);

			// Return a bad request response with error details
			return ResponseEntity.badRequest().body(ex.getMapError());
		} catch (Exception e)
		{
			// Log the exception details and return a response with the error.
			log.error("Unexpected error creating batch of server events", e);
			return ResponseEntity.internalServerError().body(e.getLocalizedMessage());
		}
	}

	/**
	 * Updates the status of a server event.
	 * 
	 * This endpoint receives an event ID and a new status, verifies the input,
	 * checks if the event exists, and updates the event status accordingly.
	 * 
	 * @param eventId        The ID of the event to be updated.
	 * @param eventNewStatus The new status to be set for the event.
	 * @return A response entity with a status and message indicating the result of
	 *         the operation.
	 */
	@Transactional
	@PutMapping(value = "/server-events")
	public ResponseEntity<?> updateServerEventStatus(@RequestParam(name = "eventId") String eventId,
			@RequestParam(name = "newStatus") String eventNewStatus)
	{

		try
		{
			// Log the incoming request parameters for the event status update.
			log.info("PUT request for '/server-events' received with parameters 'Event ID: {}, Event new Status: {}'",
					eventId, eventNewStatus);

			// Prepare the response object to send back the status and message.
			ResponseDto response = new ResponseDto();
			String message;

			// Check if eventId or eventNewStatus is null or blank, and handle error.
			if (eventId == null || eventId.isBlank() || eventNewStatus == null || eventNewStatus.isBlank())
			{
				message = "Invalid or incorrect parameters in the request.Event Id or Status is blank or null.";
				log.error(message);
				throw new ProjectorServerException(499, message); // Error code for invalid parameters.
			}

			// Validate that the new event status is part of the acceptable list.
			if (!Constants.POSSIBLE_EVENT_STATUS.contains(eventNewStatus))
			{
				message = "Error updating event status: The selected status for the event does not exist.";
				log.error(message);
				throw new ProjectorServerException(499, message); // Error code for invalid event status.
			}

			// Convert eventId to Long and fetch the corresponding event from the database.
			Long eventIdLong = Long.valueOf(eventId);
			ServerEvent serverEventEntity = this.serverEventRepository.findById(eventIdLong).orElseThrow(() ->
			{
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

			// Persist the updated event entity to the database.
			this.serverEventRepository.saveAndFlush(serverEventEntity);

			// Return the response with success status.
			return ResponseEntity.ok().body(response);

		} catch (ProjectorServerException e)
		{
			// Log the exception details and return a response with the error.
			log.error("Error updating event status", e);
			return ResponseEntity.internalServerError().body(e.getMapError());
		} catch (Exception e)
		{
			// Log the exception details and return a response with the error.
			log.error("Unexpected error updating event status", e);
			return ResponseEntity.internalServerError().body(e.getLocalizedMessage());
		}
	}

	// -----------------------------------------------------------------------------

	/**
	 * Endpoint que recibe una peticion por parte de un microcontrolador y devuelve
	 * una acción a realizar.
	 * 
	 * Este endpoint espera parametros utilizados para identificar el proyector con
	 * el que el micro está asociado. Esta identificacion es necesaria para poder
	 * saber que orden debe de servir el endpoint al microcontrolador para que este
	 * la re-envie al proyector.
	 * 
	 * Enviar al micro: ID accion + Orden
	 * 
	 */
	@GetMapping(value = "/server-events")
	public String serveCommandToController(@RequestParam(required = true) String projectorClassroom)
	{
		// Log the incoming request for processing models.
		log.info("GET request for '/server-events' received with parameter '{}'.", projectorClassroom);

		try
		{

			// Recupera el opcional.
			Optional<Projector> projectorOpt = this.projectorRepository.findById(projectorClassroom);

			// Recupera la entidad o lanza error.
			Projector projectorEntity = projectorOpt.orElseThrow(() ->
			{
				String message = "ERROR: There are no projectors assigned to this classroom.";
				log.error(message);
				return new ProjectorServerException(494, message);
			});

			// Recupera listado eventos servidor para este proyector que estan en pendiente
			// (solo los pendientes).
			List<ServerEvent> serverEventsList = this.serverEventRepository
					.findRecentServerEventsByProjector(projectorEntity, Constants.EVENT_STATUS_PENDING);

			// Evento servidor mas reciente de todos.
			if (serverEventsList.size() > 0)
			{
				ServerEvent mostRecentEvent = serverEventsList.get(0);

				// Configura evento simplificado.
				SimplifiedServerEventDto simpleEvent = new SimplifiedServerEventDto();
				simpleEvent.setActionStatus(mostRecentEvent.getActionStatus());
				simpleEvent.setCommandInstruction(mostRecentEvent.getCommand().getCommand());
				simpleEvent.setEventId(mostRecentEvent.getEventId());

				for (ServerEvent serverEvent : serverEventsList)
				{
					if (serverEvent.equals(serverEventsList.get(0)))
					{
						serverEvent.setActionStatus(Constants.EVENT_STATUS_SERVED);
					} else
					{
						serverEvent.setActionStatus(Constants.EVENT_STATUS_CANCELED);
					}
				}

				this.serverEventRepository.saveAllAndFlush(serverEventsList);

				return simpleEvent.toString();
			} else
			{
				return "No tasks";
			}

//			log.debug(simpleEvent.toString());
//
//			if (simpleEvent.size() > 0)
//			{
//				return simpleEvent.get(0).toString();
//			}
//
//			return null;

		} catch (Exception e)
		{
			return e.getMessage();
		}

	}

	@GetMapping(value = "/micro-greeting")
	public ResponseEntity<?> acknowledgeMicro()
	{
		log.info("GET request for '/micro-greeting' received.");

		// CommandDto cdto = new CommandDto("1","2","3");

		return ResponseEntity.ok().body("turn-on");
	}

	@PostMapping("/server-events")
	public ResponseEntity<?> getEventsPage(@RequestBody(required = false) EventFilterObject eventFilterObject,
			@PageableDefault(page = 0, size = 10) Pageable pageable)
	{
		log.info("POST request for '/server-events' received.");

		log.debug("Classroom: {}", eventFilterObject.getClassroomName());
		log.debug("Floor: {}", eventFilterObject.getFloorName());
		log.debug("Model: {}", eventFilterObject.getModelName());
		log.debug("getActionStatus: {}", eventFilterObject.getActionStatus());
		log.debug("getActionName: {}", eventFilterObject.getActionName());
		log.debug("getEventId: {}", eventFilterObject.getEventId());
		log.debug("getUser: {}", eventFilterObject.getUser());
		log.debug("getDateTime: {}", eventFilterObject.getDateTime());

		Page<TableServerEventDto> pagina = this.serverEventRepository.getFilteredServerEventDtosPage(pageable,
				eventFilterObject.getClassroomName(), eventFilterObject.getFloorName(),
				eventFilterObject.getModelName(), eventFilterObject.getActionStatus());

		log.debug("Recuperados:" + pagina.toList().size());

		return ResponseEntity.ok().body(pagina);
	}

	@GetMapping("/event-states")
	public ResponseEntity<?> getEventStatusList()
	{
		return ResponseEntity.ok().body(Constants.POSSIBLE_EVENT_STATUS);
	}

	// -----------------------------------------------------------------------------

	// ---------------------------- OVERVIEW METHODS -------------------------------

	/**
	 * Handles HTTP GET requests to retrieve an overview of server events.
	 * <p>
	 * This method fetches the count of server events categorized by their status
	 * (Canceled, Completed, Delivered, Error, and Pending) and returns this data
	 * encapsulated in a {@link ServerEventOverviewDto} object.
	 * </p>
	 * 
	 * @return ResponseEntity containing the event overview DTO if successful, or an
	 *         error message in case of failure.
	 */
	@GetMapping("/events-overview")
	public ResponseEntity<?> getEventsOverview()
	{
		try
		{
			// Logging the request for monitoring and debugging
			log.info("GET request for '/events-overview' received.");

			// Creating a DTO object to hold event counts
			ServerEventOverviewDto serverEventOverviewDto = new ServerEventOverviewDto();

			// Populating the DTO with event counts based on their status
			serverEventOverviewDto.setCanceledEvents(
					this.serverEventRepository.countServerEventsByStatus(Constants.EVENT_STATUS_CANCELED));

			serverEventOverviewDto.setCompletedEvents(
					this.serverEventRepository.countServerEventsByStatus(Constants.EVENT_STATUS_EXECUTED));

			serverEventOverviewDto.setDeliveredEvents(
					this.serverEventRepository.countServerEventsByStatus(Constants.EVENT_STATUS_SERVED));

			serverEventOverviewDto
					.setErrorEvents(this.serverEventRepository.countServerEventsByStatus(Constants.EVENT_STATUS_ERROR));

			serverEventOverviewDto.setPendingEvents(
					this.serverEventRepository.countServerEventsByStatus(Constants.EVENT_STATUS_PENDING));

			// Returning the populated DTO with an HTTP 200 OK response
			return ResponseEntity.ok().body(serverEventOverviewDto);

		} catch (Exception e)
		{
			// Logging the error for debugging
			log.error("Error retrieving events overview", e);

			// Returning an HTTP 500 Internal Server Error response
			return ResponseEntity.internalServerError()
					.body("Unexpected error encountered while retrieving events overview.");
		}
	}

	/**
	 * Handles HTTP GET requests to retrieve a general overview of server-related
	 * entities.
	 * <p>
	 * This method gathers and returns a summary of various counts, including the
	 * number of projectors, actions, classrooms, commands, floors, and models. The
	 * data is encapsulated in a {@link GeneralCountOverviewDto} object.
	 * </p>
	 * 
	 * @return ResponseEntity containing the general overview DTO if successful.
	 */
	@GetMapping("/general-overview")
	public ResponseEntity<?> getServerOverview()
	{
		try
		{

			// Logging the request for monitoring and debugging
			log.info("GET request for '/general-overview' received.");

			// Creating a DTO object to hold count information
			GeneralCountOverviewDto projectorOverview = new GeneralCountOverviewDto();

			// Populating the DTO with counts of various entities
			projectorOverview.setNumberOfProjectors(this.projectorRepository.count());
			projectorOverview.setNumberOfActions(this.actionRepositories.count());
			projectorOverview.setNumberOfClassrooms(this.projectorRepository.countClassrooms());
			projectorOverview.setNumberOfCommands(this.commandRepository.count());
			projectorOverview.setNumberOfFloors(this.projectorRepository.countFloors());
			projectorOverview.setNumberOfModels(this.projectorModelRepository.count());

			// Returning the populated DTO with an HTTP 200 OK response
			return ResponseEntity.ok().body(projectorOverview);

		} catch (Exception e)
		{
			// Logging the error for debugging
			log.error("Error retrieving general overview", e);

			// Returning an HTTP 500 Internal Server Error response in case of failure
			return ResponseEntity.internalServerError()
					.body("Unexpected error encountered while retrieving general overview.");
		}
	}

	// -----------------------------------------------------------------------------

}
