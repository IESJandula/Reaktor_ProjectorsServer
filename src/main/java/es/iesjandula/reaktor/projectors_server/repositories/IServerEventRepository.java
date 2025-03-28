package es.iesjandula.reaktor.projectors_server.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.iesjandula.reaktor.projectors_server.dtos.SimplifiedServerEventDto;
import es.iesjandula.reaktor.projectors_server.dtos.TableServerEventDto;
import es.iesjandula.reaktor.projectors_server.entities.Projector;
import es.iesjandula.reaktor.projectors_server.entities.ServerEvent;

/**
 * Repository interface for managing ServerEvent entities. Provides CRUD
 * operations on ServerEvent entities and custom queries to retrieve event data.
 * 
 * @author David Jason Gianmoena
 *         (<a href="https://github.com/JasonDGian">GitHub</a>)
 * @version 1.1
 */
public interface IServerEventRepository extends JpaRepository<ServerEvent, Long>
{

	@Query("""
			    SELECT se
			    FROM ServerEvent se
			    WHERE se.command.action = :action
			""")
	List<ServerEvent> findAllByAction(@Param("action") String action);
	
    List<ServerEvent> findByProjector(Projector projector);

	// Find all server events by a specific command (using modelName and action)
	@Query("SELECT s FROM ServerEvent s WHERE s.command.modelName = :modelName AND s.command.action = :action")
	List<ServerEvent> findByCommand(@Param("modelName") String modelName, @Param("action") String action);

	/**
	 * Retrieves a list of simplified server event details for a given projector and
	 * action status. The results are ordered by event date in descending order.
	 * 
	 * @param projector    the projector whose events are to be retrieved
	 * @param actionStatus the status of the action (e.g., OPEN, CLOSED) to filter
	 *                     events
	 * @return a list of simplified server event details
	 */
	@Query("SELECT new es.iesjandula.reaktor.projectors_server.dtos.SimplifiedServerEventDto( se.eventId, se.command.command, se.actionStatus ) "
			+ "FROM ServerEvent se " + "WHERE se.projector = :projector " + "AND se.actionStatus = :actionStatus "
			+ "ORDER BY se.dateTime DESC")
	List<SimplifiedServerEventDto> findRecentServerEventsByStatus(Projector projector, String actionStatus);

	/**
	 * Retrieves a list of server events for a given projector and action status,
	 * ordered by date.
	 * 
	 * @param projector    the projector whose events are to be retrieved
	 * @param actionStatus the status of the action (e.g., OPEN, CLOSED) to filter
	 *                     events
	 * @return a list of server events
	 */
	@Query("SELECT se FROM ServerEvent se " + "WHERE se.projector = :projector "
			+ "AND se.actionStatus = :actionStatus " + "ORDER BY se.dateTime DESC")
	List<ServerEvent> findRecentServerEventsByProjector(Projector projector, String actionStatus);

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
			c.action,
			p.model,
			p.classroom,
			p.floor,
			se.user,
			se.dateTime,
			se.actionStatus
			)
			FROM ServerEvent se
			JOIN se.command c
			JOIN se.projector p
			WHERE (:classroom = '' OR :classroom IS NULL OR p.classroom = :classroom)
			AND (:floor = '' OR :floor IS NULL OR p.floor = :floor)
			AND (:model = '' OR :model IS NULL OR p.model = :model)
			AND (:actionStatus = '' OR :actionStatus IS NULL OR se.actionStatus = :actionStatus)
			ORDER BY se.dateTime DESC
			""")
	Page<TableServerEventDto> getFilteredServerEventDtosPage(Pageable pageable, @Param("classroom") String classroom,
			@Param("floor") String floor, @Param("model") String model, @Param("actionStatus") String actionStatus);

	/**
	 * Retrieves the count of server events for a specific action status.
	 * 
	 * @param actionStatus the action status to filter the events
	 * @return the count of events matching the action status
	 */
	@Query("""
			SELECT COUNT(*)
			FROM ServerEvent se
			WHERE se.actionStatus = :actionStatus
			""")
	Long countServerEventsByStatus(@Param("actionStatus") String actionStatus);

}
