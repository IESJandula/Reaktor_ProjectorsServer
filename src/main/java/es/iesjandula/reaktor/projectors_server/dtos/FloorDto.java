package es.iesjandula.reaktor.projectors_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a floor entity for use in the
 * frontend. This class provides a simplified representation of the floor
 * entity.
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FloorDto
{
	/**
	 * The name of the floor.
	 */
	private String floorName;
}
