package es.iesjandula.reaktor_projector_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing an overview of a model for use in the
 * frontend. This class provides a simplified representation of a model entity
 * and its associated number of records.
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelOverviewDto
{
	/**
	 * The name of the model.
	 */
	private String modelname;

	/**
	 * The number of projectors associated with the model.
	 */
	private long associatedProjectors;

	/**
	 * The number of commands associated with the model.
	 */
	private long associatedCommands;
}
