package es.iesjandula.reaktor_projector_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a Classroom entity for use in the
 * frontend. This class provides a simplified representation of the Classroom
 * entity.
 * 
 * @author David Jason Gianmoena
 *         (<a href="https://github.com/JasonDGian">GitHub</a>)
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDto
{

	/**
	 * The name of the classroom (e.g., "0.01").
	 */
	private String classroomName;

	/**
	 * The name of the floor the classroom belongs to (e.g., "Planta 1").
	 */
	private String floorName;
}
