package es.iesjandula.reaktor.projectors_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing an Action entity for use in the
 * frontend. This class provides a simplified representation of the Action
 * entity.
 * 
 * @author David Jason Gianmoena
 *         (<a href="https://github.com/JasonDGian">GitHub</a>)
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionDto
{

	/**
	 * The name of the action.
	 */
	private String actionName;

}
