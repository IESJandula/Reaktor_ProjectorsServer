package es.iesjandula.reaktor_projector_server.parsers.interfaces;

import java.util.Scanner;

import es.iesjandula.reaktor_projector_server.utils.ProjectorServerException;

/**
 * Defines the contract for parsing projector data from an input source.
 * <p>
 * Implementations of this interface are responsible for reading, validating,
 * and processing projector information from a {@link Scanner} input. The parsed
 * data is typically persisted in a database or used for further processing.
 * </p>
 *
 * <p>
 * If any error occurs during parsing (e.g., malformed data, database issues), a
 * {@link ProjectorServerException} is thrown.
 * </p>
 *
 * <h2>Expected Input Format</h2>
 * <p>
 * The input data should be in CSV format, where each line represents a
 * projector unit and contains the following fields:
 * </p>
 * <ul>
 * <li><strong>Model Name</strong> - The name of the projector model (e.g.,
 * "Epson EB-S41").</li>
 * <li><strong>Classroom</strong> - The identifier of the classroom where the
 * projector is located (e.g., "0.01").</li>
 * </ul>
 *
 * <h2>Example Input</h2>
 * 
 * <pre>
 * model_name,classroom,floor
 * Epson EB-S41,0.01,planta 0
 * BenQ MW535,0.02,planta 0
 * ViewSonic PX701-4K,0.03,planta 0
 * </pre>
 *
 * @author David Jason Gianmoena
 *         (<a href="https://github.com/JasonDGian">GitHub</a>)
 * @version 1.1
 */
public interface IProjectorParser
{
	/**
	 * Parses projector data from the provided {@link Scanner} input.
	 * <p>
	 * The method processes each line of the CSV input, validates the data, and
	 * stores the projector information in the system. If a projector already exists
	 * in the database, it is skipped.
	 * </p>
	 *
	 * @param scanner The {@link Scanner} containing the projector data to parse.
	 * @return A summary message indicating the number of records saved and skipped.
	 * @throws ProjectorServerException If an error occurs during parsing or
	 *                                  database operations.
	 */
	public String parseProjectors(Scanner scanner) throws ProjectorServerException;
}
