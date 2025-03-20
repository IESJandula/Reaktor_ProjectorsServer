package es.iesjandula.reaktor_projector_server.utils;

import java.util.Set;

/**
 * Class containing constant values used across the project.
 * Constants should be defined in this class to avoid duplication and hardcoded values.
 */
public final class Constants
{
    /**
     * The delimiter used for separating CSV values.
     */
	public static final String CSV_DELIMITER = ",";
	
	public static final String RESPONSE_STATUS_SUCCESS = "SUCCESS";
	public static final String RESPONSE_STATUS_ERROR = "ERROR";
	public static final String RESPONSE_STATUS_INFO = "INFO";
	public static final String RESPONSE_STATUS_WARNING = "WARNING";
	
	
	public static final String EVENT_STATUS_PENDING = "PENDING";
	public static final String EVENT_STATUS_EXECUTED = "EXECUTED";
	public static final String EVENT_STATUS_SERVED = "SERVED";
	public static final String EVENT_STATUS_CANCELED = "CANCELED";
	public static final String EVENT_STATUS_ERROR = "ERROR";
	
	public static final Set<String> POSSIBLE_EVENT_STATUS = Set.of(
		    EVENT_STATUS_PENDING, 
		    EVENT_STATUS_CANCELED, 
		    EVENT_STATUS_EXECUTED,
		    EVENT_STATUS_SERVED,
		    EVENT_STATUS_ERROR
		);


}
