package com.ah.apiengine.request;

import com.ah.apiengine.MessageTypes;
import com.ah.apiengine.Request;

public class Requests implements MessageTypes {

	public static Request getInstance(int msgType) {
		Request request;

		switch (msgType) {
		case LOGIN_REQUEST:
			request = new LoginRequest();
			break;
		case LOGOUT_REQUEST:
			request = new LogoutRequest();
			break;
		case HEART_BEAT_REQUEST:
			request = new HeartBeatRequest();
			break;
		case VHM_OPERATION_REQUEST:
			request = new VhmOperationRequest();
			break;
		case USER_OPERATION_REQUEST:
			request = new UserOperationRequest();
			break;
		case UPDATE_HHM_LIST_REQUEST:
			request = new UpdateHhmListRequest();
			break;
		case HHM_INFO_QUERY_REQUEST:
			request = new HhmInfoQueryRequest();
			break;
		case COMMAND_LINE_REQUEST:
			request = new CommandLineRequest();
			break;
		case SEND_DENY_MAIL_LIST_REQUEST:
			request = new SendEmailListRequest();
			break;
		case MOVE_VHM_REQUEST:
			request = new MoveVhmRequest();
			break;
		case QUERY_VHM_MOVING_STATUS_REQUEST:
			request = new QueryVhmMovingStatusRequest();
			break;
		default:
			request = null;
			break;
		}

		return request;
	}

}