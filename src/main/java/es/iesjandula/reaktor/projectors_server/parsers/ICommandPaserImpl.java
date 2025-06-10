package es.iesjandula.reaktor.projectors_server.parsers;

import java.util.Optional;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.iesjandula.reaktor.projectors_server.entities.Command;
import es.iesjandula.reaktor.projectors_server.entities.ids.CommandId;
import es.iesjandula.reaktor.projectors_server.parsers.interfaces.ICommandParser;
import es.iesjandula.reaktor.projectors_server.repositories.ICommandRepository;
import es.iesjandula.reaktor.projectors_server.utils.Constants;
import es.iesjandula.reaktor.projectors_server.utils.ProjectorServerException;
import lombok.extern.slf4j.Slf4j;


/**
 * Implementation of the {@link ICommandParser} interface for parsing command data
 * from CSV files. This service is responsible for reading a CSV file containing
 * action names, commands, and projector models, checking for existing records
 * in the database, and saving new records.
 * 
 * @see ICommandParser
 * 
 * <p>
 * Author: David Jason Gianmoena (<a href="https://github.com/JasonDGian">GitHub</a>) 
 * Version: 1.1
 * </p>
 */
@Slf4j
@Service
public class ICommandPaserImpl implements ICommandParser
{

    @Autowired
    private ICommandRepository commandRepo;
    


    public static String replaceEscapeSequences(String input) {
    if (input.endsWith("\\r")) {
        return input.substring(0, input.length() - 2) + "\r";
    } else if (input.endsWith("\\n")) {
        return input.substring(0, input.length() - 2) + "\n";
    }
    return input;
    }

    /**
     * Parses commands from the given {@link Scanner} input and updates the database.
     *
     * <p>Processing steps:</p>
     * <ul>
     *     <li>Checks if the file is empty.</li>
     *     <li>Skips the first line (assumed to be the header).</li>
     *     <li>Reads and trims each subsequent line to extract the action name, command, and model name.</li>
     *     <li>Checks if they exist in the database.</li>
     *     <li>Saves new records if missing.</li>
     *     <li>Skips over malformed or already existing records</li>
     * </ul>
     *
     * @param scanner The {@link Scanner} instance reading the CSV file.
     * @return Summary of records saved and skipped.
     * @throws ProjectorServerException If the file is empty or an error occurs.
     */
    @Override
    public String parseCommands(Scanner scanner) throws ProjectorServerException
    {
        log.debug("Commands parsing process initiated.");
        
        // Check if the CSV file is empty
        if (!scanner.hasNextLine())
        {
            log.error("The received file is empty. No commands to parse.");
            throw new ProjectorServerException(493, "Empty CSV file received in parseCommands() method.");
        }
        String message;
        int recordLine = 0;
        int recordsSkipped = 0;
        int recordsSaved = 0;
        
        // Ignore the first line of the CSV file (column headers).
        scanner.nextLine();

        while (scanner.hasNextLine()) {
            log.debug("-----------------------------------------------------------------------");
            
            recordLine++;
            
            // Expected format: action_name, command, model_name
            String[] csvFields = scanner.nextLine().split(Constants.CSV_DELIMITER);
            
			if ( csvFields.length != 3 )
			{
				message = "ERROR: Missing value detected in the Commands CSV file in line " + recordLine + ".";
				log.error(message);
				throw new ProjectorServerException(499, message);
			}
            
            // Skips malformed lines.
            String actionName = csvFields[0].trim(); 
            String command = csvFields[1].trim();
            String modelName = csvFields[2].trim();
            
			// If the current line is not exactly 3 fields long, throw exception.
			if ( actionName.isBlank() || command.isBlank() || modelName.isBlank())
			{
				message = "ERROR: Blank or empty value detected in the Commands CSV file in line " + recordLine + ".";
				log.error(message);
				throw new ProjectorServerException(499, message);
			}

            command = replaceEscapeSequences(command);
                                    
            // Generate a unique command ID
            CommandId currentCommandId = new CommandId();
            currentCommandId.setAction(actionName);
            currentCommandId.setModelName(modelName);
            
            // Check if the command already exists...
            log.debug("Parsing command '{}'.", command);
            Optional<Command> currentCommandOptional = commandRepo.findById(currentCommandId);
            
            if (currentCommandOptional.isEmpty()) {
                log.debug("Command with ID '{}' not present in DB, saving now.", currentCommandId);
                Command currentCommand = new Command();
                currentCommand.setAction(actionName);
                currentCommand.setModelName(modelName);
                currentCommand.setCommand(command);
                commandRepo.saveAndFlush(currentCommand);
                recordsSaved++;
            } else {
                log.debug("Command with ID '{}' already exists in the database. Skipping it now.", currentCommandId);
                recordsSkipped++;
            }
        }
        
        message = "COMMANDS: Records saved: " + recordsSaved + " - Records skipped: " + recordsSkipped;
        log.info(message);
		return message;
    }
}
