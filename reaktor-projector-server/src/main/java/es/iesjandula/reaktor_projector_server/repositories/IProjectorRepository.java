package es.iesjandula.reaktor_projector_server.repositories;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.iesjandula.reaktor_projector_server.dtos.ProjectorDto;
import es.iesjandula.reaktor_projector_server.dtos.ProjectorInfoDto;
import es.iesjandula.reaktor_projector_server.entities.Projector;
import es.iesjandula.reaktor_projector_server.entities.ids.ProjectorId;

/**
 * Repository interface for managing Projector entities.
 * This interface extends JpaRepository to provide CRUD operations on Projector entities.
 * It also includes custom query methods for fetching Projector data with filtering and pagination.
 * 
 * @author David Jason Gianmoena (<a href="https://github.com/JasonDGian">GitHub</a>)
 * @version 1.0
 */
public interface IProjectorRepository extends JpaRepository<Projector, ProjectorId> {

    /**
     * Retrieves a paginated list of ProjectorInfoDto objects, ordered by model name, floor name, and classroom name.
     * Filters projectors by classroom, floor, and model name if provided (handles null values).
     * 
     * @param pageable the pagination information
     * @param classroom the classroom name filter (can be null)
     * @param floor the floor name filter (can be null)
     * @param model the projector model filter (can be null)
     * @return a paginated list of ProjectorInfoDto objects
     */
    @Query("""
            SELECT new es.iesjandula.reaktor_projector_server.dtos.ProjectorInfoDto( 
                pro.model.modelName, 
                pro.classroom.classroomName, 
                pro.classroom.floor.floorName
            ) 
            FROM Projector pro 
            WHERE ( :classroom IS NULL OR pro.classroom.classroomName = :classroom ) 
            AND ( :floor IS NULL OR pro.classroom.floor.floorName = :floor ) 
            AND ( :model IS NULL OR pro.model.modelName = :model ) 
            ORDER BY pro.model.modelName, pro.classroom.floor.floorName, pro.classroom.classroomName 
        """)
    public Page<ProjectorInfoDto> findProjectorsOrderedByModel(Pageable pageable, String classroom, String floor, String model);

    /**
     * Retrieves a paginated list of ProjectorInfoDto objects, ordered by floor name and classroom name.
     * Filters projectors by classroom, floor, and model name if provided (handles null values).
     * 
     * @param pageable the pagination information
     * @param classroom the classroom name filter (can be null)
     * @param floor the floor name filter (can be null)
     * @param model the projector model filter (can be null)
     * @return a paginated list of ProjectorInfoDto objects
     */
    @Query("""
            SELECT new es.iesjandula.reaktor_projector_server.dtos.ProjectorInfoDto( 
                pro.model.modelName, 
                pro.classroom.classroomName, 
                pro.classroom.floor.floorName
            ) 
            FROM Projector pro 
            WHERE ( :classroom IS NULL OR pro.classroom.classroomName = :classroom ) 
            AND ( :floor IS NULL OR pro.classroom.floor.floorName = :floor ) 
            AND ( :model IS NULL OR pro.model.modelName = :model )
            ORDER BY pro.classroom.floor.floorName, pro.classroom.classroomName 
        """)
    public Page<ProjectorInfoDto> findProjectorsOrderedByFloorAndClassroom(Pageable pageable, String classroom, String floor, String model);

    /**
     * Retrieves a list of ProjectorDto objects for a given classroom.
     * Filters projectors by classroom name (case-insensitive).
     * 
     * @param classroom the classroom name filter
     * @return a list of ProjectorDto objects representing projectors in the specified classroom
     */
    @Query("""
            SELECT new es.iesjandula.reaktor_projector_server.dtos.ProjectorDto( 
                pro.model.modelName, 
                pro.classroom.classroomName
            ) 
            FROM Projector pro 
            WHERE LOWER(pro.classroom.classroomName) = LOWER(:classroom)
        """)
    public List<ProjectorDto> findProjectorsByClassroom(@Param("classroom") String classroom);

    /**
     * Counts the number of projectors associated with a specific model name.
     * The search is case-insensitive.
     * 
     * @param modelname the projector model name
     * @return the number of projectors associated with the specified model
     */
    @Query("""
            SELECT COUNT(*) 
            FROM Projector pro 
            WHERE LOWER(pro.model.modelName) = LOWER(:modelname) 
        """)
    public long countProjectorsByModel(@Param("modelname") String modelname);

    /**
     * Counts the number of projectors on a specific floor.
     * The search is case-insensitive.
     * 
     * @param floorname the floor name
     * @return the number of projectors located on the specified floor
     */
    @Query("""
            SELECT COUNT(*) 
            FROM Projector pro 
            WHERE LOWER(pro.classroom.floor.floorName) = LOWER(:floorname) 
        """)
    public long countProjectorsOnFloor(@Param("floorname") String floorname);

}
