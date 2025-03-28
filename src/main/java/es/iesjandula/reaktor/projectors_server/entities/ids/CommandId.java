package es.iesjandula.reaktor.projectors_server.entities.ids;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the composite key for a Command entity, uniquely identifying a
 * command by the projector model, the action it performs, and the binary or
 * hexadecimal instruction.
 * 
 * <p>
 * The composite key consists of:
 * <ul>
 * <li>{@code modelName} - The projector model for which the command is
 * used.</li>
 * <li>{@code action} - The specific action that the command triggers.</li>
 * <li>{@code command} - The binary instruction sent to the projector.</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandId
{
	/**
	 * The projector model associated with the command.
	 */
	private String modelName;

	/**
	 * The action that the command is intended to perform.
	 */
	private String action;


}
