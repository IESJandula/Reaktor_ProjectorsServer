package es.iesjandula.reaktor_projector_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing projector information for use in the
 * frontend. This class provides a simplified representation of a projector
 * entity and its associated details. Used to return a paged result.
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectorInfoDto
{
	/**
	 * The model of the projector.
	 */
	private String model;

	/**
	 * The classroom where the projector is located.
	 */
	private String classroom;

	/**
	 * The name of the floor where the projector is located.
	 */
	private String floorname;
}
