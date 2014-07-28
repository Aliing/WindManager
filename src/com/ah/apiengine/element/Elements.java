package com.ah.apiengine.element;

import com.ah.apiengine.Element;
import com.ah.apiengine.ElementTypes;
import com.ah.util.Tracer;

public class Elements implements ElementTypes {

	private static final Tracer	log	= new Tracer(Elements.class.getSimpleName());

	public static Element getInstance(short elemType) {
		Element e = null;

		switch (elemType) {
		case LOGIN:
			e = new Login();
			break;
		case LOGOUT:
			e = new Logout();
			break;
		case SESSION:
			e = new Session();
			break;
		case VHM_OPERATION:
			e = new VhmOperation();
			break;
		case USER_OPERATION:
			e = new UserOperation();
			break;
		case UPDATE_DNS:
			e = new UpdateDNS();
			break;
		case HHM_LIST:
			e = new HhmList();
			break;
		case EXEC_RESULT:
			e = new ExecutionResult();
			break;
		case COMMAND_LINE:
			e = new CommandLine();
			break;
		case STRING_LIST:
			e = new StringList();
			break;
		case MOVE_VHM:
			e = new MvInfo();
			break;
		case QUERY_VHM_MOVING_STATUS:
			e = new VhmMovingStatusElement();
			break;
		case API_STRING:
			e = new ApiString();
			break;
		default:
			log.warning("getInstance", "Unknown Element Type '" + elemType + "'.");
			break;
		}

		return e;
	}

}