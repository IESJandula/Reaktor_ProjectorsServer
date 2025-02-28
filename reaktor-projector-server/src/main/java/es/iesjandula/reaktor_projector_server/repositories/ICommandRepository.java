package es.iesjandula.reaktor_projector_server.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.iesjandula.reaktor_projector_server.dtos.CommandDto;
import es.iesjandula.reaktor_projector_server.entities.Action;
import es.iesjandula.reaktor_projector_server.entities.Command;
import es.iesjandula.reaktor_projector_server.entities.ProjectorModel;
import es.iesjandula.reaktor_projector_server.entities.ids.CommandId;

public interface ICommandRepository extends JpaRepository<Command, CommandId >
{
    @Query("SELECT new es.iesjandula.reaktor_projector_server.dtos.CommandDto( " +
            "c.modelName.modelName, c.action.actionName, c.command ) " +
            "FROM Command c " +
            "WHERE c.modelName.modelName = :modelName")
     List<CommandDto> findCommandsByModel(@Param("modelName") String modelName);
    
    Optional<Command> findByModelNameAndAction(ProjectorModel modelName, Action action);
}
