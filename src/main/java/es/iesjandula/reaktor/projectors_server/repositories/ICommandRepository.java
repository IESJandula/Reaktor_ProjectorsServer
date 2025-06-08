package es.iesjandula.reaktor.projectors_server.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.iesjandula.reaktor.projectors_server.dtos.ActionDto;
import es.iesjandula.reaktor.projectors_server.dtos.CommandDto;
import es.iesjandula.reaktor.projectors_server.dtos.ProjectorModelDto;
import es.iesjandula.reaktor.projectors_server.entities.Command;
import es.iesjandula.reaktor.projectors_server.entities.ids.CommandId;
import jakarta.transaction.Transactional;

/**
 * Repository interface for managing {@link Command} entities. Provides database
 * operations and custom queries related to projector commands.
 * 
 * @author David Jason Gianmoena
 *         (<a href="https://github.com/JasonDGian">GitHub</a>)
 * @version 1.1
 */
@Repository
public interface ICommandRepository extends JpaRepository<Command, CommandId>
{

	// ---------------------------- ACTION QUERIES ----------------------------

	@Modifying
	@Transactional
	@Query("""
	    DELETE FROM Command c WHERE c.action IN :actionNames
	""")
	int deleteCommandsByActions(@Param("actionNames") List<String> actionNames);


	@Query("""
			SELECT COUNT(c) > 0 FROM Command c WHERE c.action = :actionName
			""")
	boolean actionExists(@Param("actionName") String actionName);
	
	@Query("SELECT c.action FROM Command c WHERE c.action IN :actionNames")
	List<String> findExistingActions(@Param("actionNames") List<String> actionNames);


	@Query("""
			    SELECT c.action
			    FROM Command c
			    GROUP BY c.action
			""")
	Page<String> findActionsPage(Pageable pageable);

	@Query("""
			SELECT NEW es.iesjandula.reaktor.projectors_server.dtos.ActionDto(c.action)
			FROM Command c
			GROUP BY c.action
			""")
	List<ActionDto> findActionsAsDto();
	
	Optional<Command> findByModelNameAndCommand(String modelName, String command);
	
	Optional<Command> findByModelNameAndAction(String modelName, String action);

	// ---------------------------- MODEL QUERIES ----------------------------

	@Query("""
			    SELECT NEW es.iesjandula.reaktor.projectors_server.dtos.ProjectorModelDto(c.modelName)
			    FROM Command c
			    GROUP BY c.modelName
			""")
	List<ProjectorModelDto> findAllProjectorModelsDto();

	@Query("""
			    SELECT NEW es.iesjandula.reaktor.projectors_server.dtos.ProjectorModelDto(c.modelName)
			    FROM Command c
			    WHERE c.modelName = :modelName
			""")
	Optional<ProjectorModelDto> findProjectorModelByModelName(@Param("modelName") String modelName);
	

	boolean existsByModelName(String modelName);
	
	// ---------------------------- OTHER QUERIES ----------------------------

	@Query("SELECT COUNT(DISTINCT c.modelName) FROM Command c")
	long countDistinctModels();

	@Query("SELECT COUNT(DISTINCT c.action) FROM Command c")
	long countDistinctActions();

	@Query("""
			    SELECT new es.iesjandula.reaktor.projectors_server.dtos.CommandDto(
			        c.modelName, c.action, c.command )
			    FROM Command c
			    WHERE LOWER(c.modelName) = LOWER(:modelName)
			""")
	List<CommandDto> findCommandsByModelNameAsDto(@Param("modelName") String modelName);

	@Query("""
			    SELECT COUNT(*)
			    FROM Command cmd
			    WHERE LOWER(cmd.modelName) = LOWER(:modelName)
			""")
	Integer countCommandsByModelName(@Param("modelName") String modelName);

	@Query("""
			SELECT new es.iesjandula.reaktor.projectors_server.dtos.CommandDto(
			c.modelName, c.action, c.command )
			FROM Command c
			WHERE ( :modelName = '' OR :modelName IS NULL OR c.modelName = :modelName)
			AND ( :action = '' OR :action IS NULL OR c.action = :action)
			""")
	Page<CommandDto> findAllCommandsPage(Pageable pageable, @Param("modelName") String modelName,
			@Param("action") String action);

	/// TODO: Investigate about JPQL Clause -> COALESCE()
}
