package es.iesjandula.reaktor.projectors_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) used to provide a formatted and standardized
 * response in special cases to the frontend to display the information
 * properly.
 * 
 * Richer version of the Response DTO that can provide more than one message for
 * the same operation in a way that the frontend can handle separately.
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RichResponseDto
{
	/**
	 * The status of the operation (e.g., INFO, ERROR, SUCCESS).
	 */
	private String status1;

	/**
	 * The first message for the result of the operation.
	 */
	private String message1;
	
	/**
	 * The status of the operation (e.g., INFO, ERROR, SUCCESS).
	 */
	private String status2;

	/**
	 * The second message for the result of the operation.
	 */
	private String message2;

}
