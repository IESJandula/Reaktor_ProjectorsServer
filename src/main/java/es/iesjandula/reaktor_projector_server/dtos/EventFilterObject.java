package es.iesjandula.reaktor_projector_server.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) used to filter server events. It represents the
 * set of criteria by which events can be sorted or filtered on the page.
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterObject
{

	/**
	 * The unique ID of the event (e.g., "1").
	 */
	private Long eventId;

	/**
	 * The action that the event must perform.
	 */
	private String actionName;

	/**
	 * The model of the projector involved.
	 */
	private String modelName;

	/**
	 * The classroom where the event takes place.
	 */
	private String classroomName;

	/**
	 * The floor where the event takes place.
	 */
	private String floorName;

	/**
	 * The user that originated the event.
	 */
	private String user;

	/**
	 * The time of creation of the event.
	 */
	private LocalDateTime dateTime;

	/**
	 * The status of the event.
	 */
	private String actionStatus;
}
