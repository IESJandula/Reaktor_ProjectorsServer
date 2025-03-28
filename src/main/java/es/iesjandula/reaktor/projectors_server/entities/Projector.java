package es.iesjandula.reaktor.projectors_server.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a projector entity that includes information about the projector
 * model and the classroom where the projector is located.
 * <p>
 * This entity is used to track individual projectors, linking each projector to
 * a specific model and physical location.
 * </p>
 * <p>
 * The combination of the model and classroom uniquely identifies each projector
 * and is used as a composite primary key via the {@link ProjectorId}.
 * </p>
 * 
 * <p>
 * Author: David Jason Gianmoena
 * (<a href="https://github.com/JasonDGian">GitHub</a>) Version: 1.0
 * </p>
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Projector
{
	@Id
	private String classroom;
	
	@Column( name = "floor")
	private String floor;
	
	@Column ( name = "model") 
	private String model;

	/**
	 * List of server events associated with the projector.
	 */
	@OneToMany(mappedBy = "projector", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<ServerEvent> serverEvents;

	@Override
	public String toString() {
	    return String.format("Projector{classroom='%s', floor='%s', model='%s'}", classroom, floor, model);
	}


	
}
