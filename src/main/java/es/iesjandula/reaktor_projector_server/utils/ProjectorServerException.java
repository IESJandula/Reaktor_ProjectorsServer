package es.iesjandula.reaktor_projector_server.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class represents a custom exception used to handle errors in the REAKTOR Projector Server.
 * It includes details such as an error ID, message, and an optional exception with its stack trace.
 * The class is designed to capture and return error information as a map for easier processing and logging.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ProjectorServerException extends Exception
{
	/**
	 * Serial version UID for the ProjectorServerException class.
	 */
	private static final long serialVersionUID = 8144321039123138732L;

	/**
	 * The unique identifier for this error.
	 */
	private int id;

	/**
	 * The error message providing a description of the error.
	 */
	private String message;

	/**
	 * The underlying exception causing this error, if any.
	 */
	private Exception exception;

	/**
	 * Constructs a new ProjectorServerException with the specified ID, message, and exception.
	 *
	 * @param id The unique identifier for this error.
	 * @param message The error message providing a description of the error.
	 * @param exception The underlying exception causing this error, if any.
	 */
	public ProjectorServerException(int id, String message, Exception exception)
	{
		super();
		this.id = id;
		this.message = message;
		this.exception = exception;
	}

	/**
	 * Constructs a new ProjectorServerException with the specified ID and message.
	 * This constructor does not include an exception.
	 *
	 * @param id The unique identifier for this error.
	 * @param message The error message providing a description of the error.
	 */
	public ProjectorServerException(int id, String message)
	{
		super();
		this.id = id;
		this.message = message;
	}

	/**
	 * Returns a map representation of the error details.
	 * The map includes the ID, message, and an optional stack trace of the exception, if present.
	 * 
	 * @return A map containing the error details. The keys are "id", "message", and "exception" (if an exception is present).
	 */
	public Map<String, String> getMapError()
	{
		Map<String, String> mapError = new HashMap<String, String>();

		mapError.put("id", "" + id);
		mapError.put("message", message);

		if (this.exception != null)
		{
			// Get the stack trace of the exception using Apache Commons ExceptionUtils.
			String stacktrace = ExceptionUtils.getStackTrace(this.exception); 
			mapError.put("exception", stacktrace);
		}
		
		return mapError;
	}
}
