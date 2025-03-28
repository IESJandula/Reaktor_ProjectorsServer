package es.iesjandula.reaktor_projector_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing an overview of server events. This
 * class provides a summary of all the event statuses. Used to show in the
 * frontend the status of all the recorded server-events.
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerEventOverviewDto
{
	/**
	 * The number of error events.
	 */
	private Long errorEvents;

	/**
	 * The number of canceled events.
	 */
	private Long canceledEvents;

	/**
	 * The number of pending events.
	 */
	private Long pendingEvents;

	/**
	 * The number of delivered events.
	 */
	private Long deliveredEvents;

	/**
	 * The number of completed events.
	 */
	private Long completedEvents;
}
