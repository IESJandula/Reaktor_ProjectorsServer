package es.iesjandula.reaktor_projector_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) used to provide a formatted and standardized
 * response in special cases to the frontend to display the information
 * properly.
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto
{
	/**
	 * The status of the operation (e.g., INFO, ERROR, SUCCESS).
	 */
	private String status;

	/**
	 * The message for the result of the operation.
	 */
	private String message;
}
