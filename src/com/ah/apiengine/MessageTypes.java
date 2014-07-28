package com.ah.apiengine;

public interface MessageTypes {

	/*
	 * API Client/Engine Message Type Definitions.
	 */
	int						ILLEGAL_REQUEST						= 1;

	int						ILLEGAL_REQUEST_RESPONSE			= 2;

	int						LOGIN_REQUEST						= 3;

	int						LOGIN_RESPONSE						= 4;

	int						LOGOUT_REQUEST						= 5;

	int						LOGOUT_RESPONSE						= 6;

	int						HEART_BEAT_REQUEST					= 7;

	int						HEART_BEAT_RESPONSE					= 8;

	int						VHM_OPERATION_REQUEST				= 9;

	int						VHM_OPERATION_RESPONSE				= 10;

	int						USER_OPERATION_REQUEST				= 11;

	int						USER_OPERATION_RESPONSE				= 12;

	int						UPDATE_HHM_LIST_REQUEST				= 13;

	int						UPDATE_HHM_LIST_RESPONSE			= 14;

	int						HHM_INFO_QUERY_REQUEST				= 15;

	int						HHM_INFO_QUERY_RESPONSE				= 16;

	int						COMMAND_LINE_REQUEST				= 17;

	int						COMMAND_LINE_RESPONSE				= COMMAND_LINE_REQUEST + 1;

	public static final int	SEND_DENY_MAIL_LIST_REQUEST			= 19;
	public static final int	SEND_DENY_MAIL_LIST_RESPONSE		= SEND_DENY_MAIL_LIST_REQUEST + 1;

	public static final int	MOVE_VHM_REQUEST					= 21;
	public static final int	MOVE_VHM_RESPONSE					= MOVE_VHM_REQUEST + 1;

	public static final int	QUERY_VHM_MOVING_STATUS_REQUEST		= 23;
	public static final int	QUERY_VHM_MOVING_STATUS_RESPONSE	= QUERY_VHM_MOVING_STATUS_REQUEST + 1;
}