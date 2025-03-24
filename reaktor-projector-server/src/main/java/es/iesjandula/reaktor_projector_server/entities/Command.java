package es.iesjandula.reaktor_projector_server.entities;

import java.util.List;

import es.iesjandula.reaktor_projector_server.entities.ids.CommandId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CommandId.class)
public class Command
{
	@Id
	private String modelName;

	@Id
	private String action;

	private String command;
	
	/**
	 * List of server events associated with the projector.
	 */
    @OneToMany(mappedBy = "command", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServerEvent> serverEvents;

	@Override
	public String toString()
	{
		return "Command{" + "modelName='" + modelName + '\'' + ", action='" + action + '\'' + ", command='" + command
				+ '\'' + '}';
	}

}
