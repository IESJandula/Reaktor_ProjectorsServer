package es.iesjandula.reaktor_projector_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a projector for use in the frontend.
 * This class provides a simplified representation of a projector entity.
 * 
 * <p>
 * Used to create new projector-to-classroom assignments and server events
 * creation.
 * </p>
 * 
 * @see ServerEventBatchDto
 * 
 *      <p>
 *      Author: David Jason Gianmoena
 *      (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 *      </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectorDto
{
	/**
	 * The model of the projector.
	 */
	private String model;

	/**
	 * The classroom where the projector is located.
	 */
	private String classroom;
}
