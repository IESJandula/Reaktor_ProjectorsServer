package es.iesjandula.reaktor.projectors_server.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) used to create a server event for an action on a
 * given list of projectors. This class simplifies the server event creation
 * request and process.
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerEventBatchDto
{
	/**
	 * The action to be performed. Used to refer to the specific instruction for
	 * each projector.
	 */
	private String action;

	/**
	 * The list of projectors to which the action will be sent.
	 */
	private List<ProjectorDto> projectorList;
}
