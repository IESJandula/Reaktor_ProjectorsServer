package es.iesjandula.reaktor_projector_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) used to provide a general overview of the total
 * records present in the database for the specified entities. - Used in the
 * dashboard or control panel.
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralCountOverviewDto
{

	/**
	 * The total number of models.
	 */
	private Long numberOfModels;

	/**
	 * The total number of actions.
	 */
	private Long numberOfActions;

	/**
	 * The total number of commands.
	 */
	private Long numberOfCommands;

	/**
	 * The total number of projectors.
	 */
	private Long numberOfProjectors;

	/**
	 * The total number of floors.
	 */
	private Long numberOfFloors;

	/**
	 * The total number of classrooms.
	 */
	private Long numberOfClassrooms;
}
