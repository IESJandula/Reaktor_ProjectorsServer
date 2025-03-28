package es.iesjandula.reaktor_projector_server.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a table view of server events for use
 * in the frontend. This class provides a comprehensive representation of a
 * server event entity.
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableServerEventDto
{
	/**
	 * The unique ID of the event.
	 */
	private Long eventId;

	/**
	 * The action performed by the event.
	 */
	private String action;

	/**
	 * The model of the projector involved in the event.
	 */
	private String model;

	/**
	 * The classroom where the event takes place.
	 */
	private String classroom;

	/**
	 * The floor where the event takes place.
	 */
	private String floor;

	/**
	 * The user who initiated the event.
	 */
	private String user;

	/**
	 * The date and time when the event was created.
	 */
	private LocalDateTime dateTime;

	/**
	 * The status of the action performed by the event.
	 */
	private String actionStatus;
}
