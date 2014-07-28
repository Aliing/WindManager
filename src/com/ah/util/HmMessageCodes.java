package com.ah.util;

/*
 * @author Chris Scheers
 */
public interface HmMessageCodes {
	/*
	 * Error codes
	 */
	public static final String UNKNOWN_ERROR = "error.unknown";

	public static final String STALE_OBJECT = "error.staleObject";

	public static final String STALE_SESSION_OBJECT = "error.staleSessionObject";

	public static final String OBJECT_EXISTS = "error.objectExists";

	public static final String CONSTRAINT_VIOLATION = "error.constraintViolation";

	public static final String OBJECT_IN_USE = "error.objectInUse";

	public static final String OBJECTS_IN_USE = "error.objectsInUse";

	public static final String OBJECT_IS_DEFAULT_VALUE = "error.objectIsDefault";

	public static final String OBJECT_IS_NONHOME_DOMAIN_VALUE = "error.objectIsNonHomeDomain";

	public static final String PERMISSION_DENIED_FEATURE = "error.permissionDeniedFeature";

	public static final String PERMISSION_DENIED_OPERATION = "error.permissionDeniedOperation";
	
	public static final String PERMISSION_DENIED_OBJECT_OPERATION = "error.permissionDeniedObjectOperation";

	public static final String AUTH_FAILED = "error.authFailed";
	
	public static final String OPERATION_INVALID = "error.operation.invalid";
	
	public static final String SECURITY_REQUEST_INVALID = "error.security.invalidRequest";

	/*
	 * Info codes
	 */
	public static final String OBJECT_CREATED = "info.objectCreated";

	public static final String OBJECT_UPDATED = "info.objectUpdated";
	
	public static final String OBJECT_REVOKED = "info.objectsRevoked";
	
	public static final String OBJECT_RESETED = "info.objectReseted";
	
	public static final String OBJECT_EMAIL = "info.objectsEmail";

	public static final String OBJECT_MOVED = "info.objectMoved";

	public static final String OBJECTS_REMOVED = "info.objectsRemoved";
	
	public static final String OBJECT_REMOVED = "info.objectRemoved";
	
	public static final String OBJECTS_REMOVENOTEXIST = "info.remove.objectsNotExist";
	
	public static final String OBJECT_REMOVENOTEXIST = "info.remove.objectNotExist";
	
	public static final String OBJECTS_REMOVED_WITH_DEFAULT = "info.objectsRemovedWithDefault";
	
	public static final String OBJECT_REMOVED_WITH_DEFAULT = "info.objectRemovedWithDefault";
	
	public static final String NO_OBJECTS_REMOVED = "info.noObjectsRemoved";

	public static final String SELECT_OBJECT = "info.selectObject";

	public static final String ALARMS_CLEARED = "info.alarmsCleared";

	public static final String NO_ALARMS_CLEARED = "info.noAlarmsCleared";

	public static final String HiveAP_REASSIGN = "info.hiveAP.reassign";
	
	public static final String KEY_COLUMN_UNSELECTED = "info.keyColumnUnselected";
}
