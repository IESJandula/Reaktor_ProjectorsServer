package es.iesjandula.reaktor.projectors_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a command for use in the frontend.
 * This class provides a simplified representation of the command entity.
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandDto
{
	/**
	 * The name of the model of the projector the command belongs to.
	 */
	private String modelName;

	/**
	 * The action that the command will perform.
	 */
	private String action;

	/**
	 * The literal instruction, such as an array of bytes or binary sequence.
	 */
	private String command;
}
