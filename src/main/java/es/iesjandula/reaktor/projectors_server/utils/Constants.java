package es.iesjandula.reaktor.projectors_server.utils;

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
	
	public static final String PROJECTORS_ORDER_CRITERIA_MODELNAME = "modelname";
	
	public static final String RESPONSE_STATUS_SUCCESS = "EXITO";
	public static final String RESPONSE_STATUS_ERROR = "ERROR";
	public static final String RESPONSE_STATUS_INFO = "INFO";
	public static final String RESPONSE_STATUS_WARNING = "ATENCION";
	
	
	public static final String EVENT_STATUS_PENDING = "PENDIENTE";
	public static final String EVENT_STATUS_EXECUTED = "REALIZADO";
	public static final String EVENT_STATUS_SERVED = "ENVIADO";
	public static final String EVENT_STATUS_CANCELED = "CANCELADO";
	public static final String EVENT_STATUS_ERROR = "ERROR";
	
	public static final String ACKNWOLEDGE_ACTION_NAME = "ACK";
	public static final String ERROR_ACTION_NAME = "ERR";
	public static final String LAMP_ON = "LAMP_ON";
	public static final String LAMP_OFF = "LAMP_OFF";
	public static final String STATUS_INQUIRY_COMMAND = "STATUS_INQUIRY";
	
	public static final String PROJECTOR_ON= "Encendido";
	public static final String PROJECTOR_OFF = "Apagado";
	
	public static final Set<String> POSSIBLE_EVENT_STATUS = Set.of(
		    EVENT_STATUS_PENDING, 
		    EVENT_STATUS_CANCELED, 
		    EVENT_STATUS_EXECUTED,
		    EVENT_STATUS_SERVED,
		    EVENT_STATUS_ERROR
		);


}
