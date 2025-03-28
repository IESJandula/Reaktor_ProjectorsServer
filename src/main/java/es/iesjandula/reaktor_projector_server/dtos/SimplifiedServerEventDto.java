package es.iesjandula.reaktor_projector_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a simplified server event for use in
 * the frontend. This class provides a basic representation of a server event
 * entity.
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) ersion: 1.0
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimplifiedServerEventDto
{
	/**
	 * The unique ID of the event.
	 */
	private Long eventId;

	/**
	 * The command instruction for the event.
	 */
	private String commandInstruction;

	/**
	 * The status of the action.
	 */
	private String actionStatus;
}
