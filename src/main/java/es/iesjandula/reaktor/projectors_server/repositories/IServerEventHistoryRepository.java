package es.iesjandula.reaktor.projectors_server.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.iesjandula.reaktor.projectors_server.dtos.SimplifiedServerEventDto;
import es.iesjandula.reaktor.projectors_server.dtos.TableServerEventDto;
import es.iesjandula.reaktor.projectors_server.entities.Projector;
import es.iesjandula.reaktor.projectors_server.entities.ServerEvent;
import es.iesjandula.reaktor.projectors_server.entities.ServerEventHistory;

@Repository
public interface IServerEventHistoryRepository extends JpaRepository<ServerEventHistory, Long> {


	/**
	 * Retrieves a list of server events for a given projector and action status,
	 * ordered by date.
	 * 
	 * @param projector    the projector whose events are to be retrieved
	 * @param actionStatus the status of the action (e.g., OPEN, CLOSED) to filter
	 *                     events
	 * @return a list of server events
	 */
	// TODO: Cambiar literal por referencia a constante.
	@Query(
			"""
					SELECT seh FROM ServerEventHistory seh 
					WHERE seh.classroom = :classroom 
					AND seh.actionStatus = "PENDIENTE" 
					ORDER BY seh.dateTime DESC
					""")
	public List<ServerEventHistory> findRecentPendingServerEventsByClassroom( String classroom );

	/**
	 * Retrieves a paginated list of table-formatted server event details with
	 * optional filters. Filters can be applied for classroom, floor, model, and
	 * action status.
	 * 
	 * @param pageable     pagination information
	 * @param classroom    optional classroom name filter (can be null or empty)
	 * @param floor        optional floor name filter (can be null or empty)
	 * @param model        optional projector model filter (can be null or empty)
	 * @param actionStatus optional action status filter (can be null or empty)
	 * 
	 * @return a paginated list of table-formatted server event details
	 */
	@Query("""
			SELECT new es.iesjandula.reaktor.projectors_server.dtos.TableServerEventDto(
			se.eventId,
			se.action,
			se.modelName,
			se.classroom,
			se.floor,
			se.user,
			se.dateTime,
			se.actionStatus
			)
			FROM ServerEventHistory se
			WHERE (:classroom = '' OR :classroom IS NULL OR se.classroom = :classroom)
			AND (:floor = '' OR :floor IS NULL OR se.floor = :floor)
			AND (:modelName = '' OR :modelName IS NULL OR se.modelName = :modelName)
			AND (:actionStatus = '' OR :actionStatus IS NULL OR se.actionStatus = :actionStatus)
			ORDER BY se.dateTime DESC
			""")
	public Page<TableServerEventDto> getFilteredServerEventDtosPage(
			Pageable pageable, 
			@Param("classroom") String classroom,
			@Param("floor") String floor, 
			@Param("modelName") String modelName, 
			@Param("actionStatus") String actionStatus
			);
		

	/**
	 * Retrieves the count of server events for a specific action status.
	 * 
	 * @param actionStatus the action status to filter the events
	 * @return the count of events matching the action status
	 */
	@Query("""
			SELECT COUNT(*)
			FROM ServerEventHistory seh
			WHERE seh.actionStatus LIKE :actionStatus
			""")
	public Long countServerEventsByStatus(@Param("actionStatus") String actionStatus);
	

}
