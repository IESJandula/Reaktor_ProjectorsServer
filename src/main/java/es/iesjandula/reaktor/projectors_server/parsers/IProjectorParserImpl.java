package es.iesjandula.reaktor.projectors_server.parsers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.iesjandula.reaktor.projectors_server.entities.Projector;
import es.iesjandula.reaktor.projectors_server.parsers.interfaces.IProjectorParser;
import es.iesjandula.reaktor.projectors_server.repositories.IProjectorRepository;
import es.iesjandula.reaktor.projectors_server.utils.Constants;
import es.iesjandula.reaktor.projectors_server.utils.ProjectorServerException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link IProjectorParser} that processes projector data from
 * a CSV file and updates the database accordingly.
 * 
 * <p>
 * This service ensures that projector models and classrooms exist in the
 * database, creating new entries when necessary.
 * </p>
 * 
 * <h2>CSV File Format</h2>
 * <p>
 * The expected CSV format:
 * </p>
 * 
 * <pre>
 * model_name,classroom,floor
 * Epson EB-S41,0.01,1st Floor
 * BenQ MW535,0.02,2nd Floor
 * ViewSonic PX701-4K,0.03,3rd Floor
 * </pre>
 * 
 * <h2>Error Handling</h2>
 * <p>
 * If the CSV file is empty or an error occurs during parsing, a
 * {@link ProjectorServerException} is thrown.
 * </p>
 * 
 * @see IProjectorParser
 * 
 * @author David Jason Gianmoena
 *         (<a href="https://github.com/JasonDGian">GitHub</a>)
 * @version 1.2
 */
@Slf4j
@Service
public class IProjectorParserImpl implements IProjectorParser
{

	@Autowired
	private IProjectorRepository projectorRepository;

	/**
	 * Parses projector records from the provided {@link Scanner} input, reading a
	 * CSV file.
	 * 
	 * <p>
	 * Ensures that all necessary entities exist in the database before creating new
	 * projectors.
	 * </p>
	 * 
	 * <p>
	 * Processing Steps
	 * </p>
	 * <ul>
	 * <li>Reads and skips the first line (header).</li>
	 * <li>Processes each line, extracting the projector model, classroom, and
	 * floor.</li>
	 * <li>Ensures the floor exists, creating it if necessary.</li>
	 * <li>Ensures the classroom exists, linking it to the correct floor.</li>
	 * <li>Ensures the projector model exists.</li>
	 * <li>Checks for an existing projector entry; creates and saves if absent.</li>
	 * </ul>
	 * 
	 * @param scanner The {@link Scanner} instance reading the CSV file.
	 * @return A summary indicating records saved and skipped.
	 * @throws ProjectorServerException If an error occurs during parsing or
	 *                                  database operations.
	 */
	@Override
	@Transactional
	public String parseProjectors(Scanner scanner) throws ProjectorServerException
	{

		log.debug("Projector parsing process initiated.");

		// Check if the CSV file is empty
		if (!scanner.hasNextLine())
		{
			log.error("The received file is empty. No projectors to parse.");
			throw new ProjectorServerException(492, "Empty CSV file received in parseProjectors() method.");
		}

		String message;
		int recordsSaved = 0;
		int recordLine = 0;
		List<Projector> projectorsList = new ArrayList<>();

		// Skip the first line (assumed to be headers)
		scanner.nextLine();

		// Process each line in the CSV file
		while (scanner.hasNextLine())
		{
			log.debug("---------------------------- PROJECTOR ENTITY " + recordLine
					+ " -------------------------------------------");

			recordLine++;

			// Read and split the CSV line
			String[] csvFields = scanner.nextLine().split(Constants.CSV_DELIMITER);

			if (csvFields.length != 3)
			{
				message = "ERROR: Malformed line in the Projectors CSV file in line " + recordLine + ".";
				log.error(message);
				throw new ProjectorServerException(499, message);
			}

			String modelName = csvFields[0].trim();
			String classroomName = csvFields[1].trim();
			String floorName = csvFields[2].trim();

			// If the current line is not exactly 3 fields long, throw exception.
			if (modelName.isBlank() || classroomName.isBlank() || floorName.isBlank())
			{
				message = "ERROR: Blank or empty value detected in the Projectors CSV file in line " + recordLine + ".";
				log.error(message);
				throw new ProjectorServerException(499, message);
			}

			// Check if projector in already exists...
			Optional<Projector> projectorOptional = this.projectorRepository.findById(classroomName);

			if (projectorOptional.isPresent())
			{
				message = "ERROR: Classroom " + classroomName + " already has an associated projector.";
				log.debug(message);
				throw new ProjectorServerException(499, message);
			}


			// if doesn't exists: create and save a new projector entry
			Projector newProjector = new Projector();
			newProjector.setClassroom(classroomName);
			newProjector.setModel(modelName);
			newProjector.setFloor(floorName);

			// add to list to save.
			projectorsList.add(newProjector);

			recordsSaved++;
			log.debug("Projector unit with model '{}' in classroom '{}' successfully added to save list.", modelName, classroomName);

		}
		
		this.projectorRepository.saveAllAndFlush(projectorsList);

		message = "PROJECTORS: Records saved: " + recordsSaved + ".";
		log.info(message);
		return message;
	}
}
