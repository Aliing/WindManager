package com.ah.be.rest.server.business;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.rest.server.exception.HmException;
import com.ah.be.rest.server.exception.ItemNotFoundException;
import com.ah.be.rest.server.exception.PostDataException;
import com.ah.be.rest.server.exception.RestBaseException;
import com.ah.be.rest.server.models.MACAuthModel;
import com.ah.be.rest.server.models.ResultStatus;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.useraccess.MACAuth;
import com.ah.util.Tracer;

public class RestMACAuthService implements IRestConstants{

	private static final Tracer log = new Tracer(RestMACAuthService.class.getSimpleName());

	public static ResultStatus singleUpsert(MACAuthModel macAuthModel, String operation)throws RestBaseException{
		ResultStatus resultStatus = new ResultStatus(operation);
		try{
			macAuthDataValidate(macAuthModel,operation);
			MACAuth macAuth = getMACAuth(macAuthModel.getStudentId(),macAuthModel.getSchoolId(),operation);
			macAuth.setMacAddress(macAuthModel.getMacAddress());
			macAuth.setStudentName(macAuthModel.getStudentName());
			QueryUtil.updateBo(macAuth);
			resultStatus.setEffectRows(1);
			resultStatus.setResultFlag(SUCCESS);
			resultStatus.setResultDetail(SUCCESS);
			return resultStatus;
		}catch(Exception exception){
			if(exception instanceof ItemNotFoundException){
				try {
					MACAuth macAuth = new MACAuth();
					macAuth.setMacAddress(macAuthModel.getMacAddress());
					macAuth.setSchoolId(macAuthModel.getSchoolId());
					macAuth.setStudentId(macAuthModel.getStudentId());
					macAuth.setStudentName(macAuthModel.getStudentName());
					macAuth.setOwner(getGlobalDomain());
					QueryUtil.createBo(macAuth);
					resultStatus.setEffectRows(1);
					resultStatus.setResultFlag(SUCCESS);
					resultStatus.setResultDetail(SUCCESS);
					return resultStatus;
				} catch (Exception e) {
					log.error("RestMACAuthService","upsertMACAuth",exception);
					resultStatus.setEffectRows(0);
					resultStatus.setResultFlag(ERROR);
					resultStatus.setResultDetail(RESULT_ERROR_INTERNALSERVERERROR);
					throw new HmException(resultStatus);
				}
			}else{
				log.error("RestMACAuthService","updateMACAuth",exception);
				resultStatus.setEffectRows(0);
				resultStatus.setResultFlag(ERROR);
				resultStatus.setResultDetail(RESULT_ERROR_INTERNALSERVERERROR);
				throw new HmException(resultStatus);
			}
		}
	}

	public static ResultStatus bulkUpsert(List<MACAuthModel> macAuthModels, String operation) throws RestBaseException{
		ResultStatus resultStatus = new ResultStatus(operation);
		List<MACAuth> createMACAuths = new ArrayList<MACAuth>();
		List<MACAuth> updateMACAuths = new ArrayList<MACAuth>();
		int errorNum = 0;
		int successNum = 0;
		for(MACAuthModel macAuthModel : macAuthModels){
			MACAuth macAuth;
			try{
				macAuth = getMACAuth(macAuthModel.getStudentId(), macAuthModel.getSchoolId(), operation);
				updateMACAuths.add(macAuth);
			}catch(Exception exception){
				if(exception instanceof ItemNotFoundException){
					try{
						macAuth = new MACAuth();
						macAuth.setMacAddress(macAuthModel.getMacAddress());
						macAuth.setSchoolId(macAuthModel.getSchoolId());
						macAuth.setStudentId(macAuthModel.getStudentId());
						macAuth.setStudentName(macAuthModel.getStudentName());
						macAuth.setOwner(getGlobalDomain());
						createMACAuths.add(macAuth);
					}catch(Exception e){
						errorNum++;
						log.error("RestMACAuthService","upsertMACAuth",e);
					}
				}
			}
		}

		try{
			if(!createMACAuths.isEmpty()){
				QueryUtil.bulkCreateBos(createMACAuths);
				successNum += createMACAuths.size();
			}

			if(!updateMACAuths.isEmpty()){
				QueryUtil.bulkUpdateBos(updateMACAuths);
				successNum += updateMACAuths.size();
			}

			resultStatus.setResultFlag(SUCCESS);
			resultStatus.setResultDetail(SUCCESS+": "+ successNum +" Records, and Fail: " + errorNum +" Records.");
			resultStatus.setEffectRows(successNum);

		}catch(Exception exception){
			resultStatus.setResultFlag(ERROR);
			resultStatus.setResultDetail(RESULT_ERROR_INTERNALSERVERERROR);
			resultStatus.setEffectRows(0);
			return resultStatus;
		}

		return resultStatus;
	}

	public static ResultStatus bulkDelete(List<MACAuthModel> macAuthModels, String operation) throws RestBaseException{
		ResultStatus resultStatus = new ResultStatus(operation);
		int errorNum = 0;
		int successNum = 0;
		List<MACAuth> deleteMACAuthList = new ArrayList<MACAuth>();
		for (MACAuthModel macAuthModel : macAuthModels) {
			try {
				deleteMacAuthDataValidate(macAuthModel, operation);
				MACAuth macAuth = getMACAuth(macAuthModel.getStudentId(),
						macAuthModel.getSchoolId(), operation);
				deleteMACAuthList.add(macAuth);
			} catch (ItemNotFoundException ex) {
				errorNum++;
			} catch (PostDataException ex) {
				errorNum++;
			}
		}

		for (MACAuth macAuth : deleteMACAuthList) {
			try{
				QueryUtil.removeBo(MACAuth.class, macAuth.getId());
				successNum++;
			} catch (Exception exception) {
				errorNum++;
				log.error("RestMACAuthService","bulkDeleteMACAuth",exception);
			}

		}

		resultStatus.setResultFlag(SUCCESS);
		resultStatus.setResultDetail(SUCCESS+":"+ successNum +"Records, and Fail:" + errorNum +"Records.");
		resultStatus.setEffectRows(successNum);
		return resultStatus;
	}
	
	public static ResultStatus singleDelete(MACAuthModel macAuthModel, String operation) throws RestBaseException{
		ResultStatus resultStatus = new ResultStatus(operation);
		try{
			deleteMacAuthDataValidate(macAuthModel,operation);
			MACAuth macAuth = getMACAuth(macAuthModel.getStudentId(),macAuthModel.getSchoolId(),operation);
			QueryUtil.removeBo(MACAuth.class, macAuth.getId());
			resultStatus.setEffectRows(1);
			resultStatus.setResultFlag(SUCCESS);
			resultStatus.setResultDetail(SUCCESS);
			return resultStatus;
		}catch(Exception exception){
			if(exception instanceof ItemNotFoundException){
				resultStatus.setEffectRows(1);
				resultStatus.setResultFlag(ERROR);
				resultStatus.setResultDetail(RESULT_ERROR_OBJECT_NOTXEXIST);
				return resultStatus;
			}else{
				log.error("RestMACAuthService","singleDeleteMACAuth",exception);
				resultStatus.setEffectRows(0);
				resultStatus.setResultFlag(ERROR);
				resultStatus.setResultDetail(RESULT_ERROR_INTERNALSERVERERROR);
				throw new HmException(resultStatus);
			}
		}
	}

	private static void macAuthDataValidate(MACAuthModel macAuthModel, String operation) throws PostDataException{
		ResultStatus resultStatus = new ResultStatus(operation);
		resultStatus.setEffectRows(0);
		resultStatus.setResultFlag(ERROR);
		try{
			String studentId = macAuthModel.getStudentId().trim();
			if("null".equalsIgnoreCase(studentId)||"".equalsIgnoreCase(studentId)){
				resultStatus.setResultDetail(RESULT_ERROR_MACAUTH_STUDENTID_NULL);
				throw new PostDataException(resultStatus);
			}
		}catch(NullPointerException exception){
			resultStatus.setResultDetail(RESULT_ERROR_MACAUTH_STUDENTID_NULL);
			throw new PostDataException(resultStatus);
		}

		try{
			String studentName = macAuthModel.getStudentName().trim();
			if("null".equalsIgnoreCase(studentName)||"".equalsIgnoreCase(studentName)){
				resultStatus.setResultDetail(RESULT_ERROR_MACAUTH_STUDENTNAME_NULL);
				throw new PostDataException(resultStatus);
			}
		}catch(NullPointerException exception){
			resultStatus.setResultDetail(RESULT_ERROR_MACAUTH_STUDENTNAME_NULL);
			throw new PostDataException(resultStatus);
		}

		try{
			String macAddress = macAuthModel.getMacAddress().trim();
			if("null".equalsIgnoreCase(macAddress)||"".equalsIgnoreCase(macAddress)){
				resultStatus.setResultDetail(RESULT_ERROR_MACAUTH_MACADDRESS_NULL);
				throw new PostDataException(resultStatus);
			}
		}catch(NullPointerException exception){
			resultStatus.setResultDetail(RESULT_ERROR_MACAUTH_MACADDRESS_NULL);
			throw new PostDataException(resultStatus);
		}
		
		try{
			String schoolId = macAuthModel.getSchoolId().trim();
			if("null".equalsIgnoreCase(schoolId)||"".equalsIgnoreCase(schoolId)){
				resultStatus.setResultDetail(RESULT_ERROR_MACAUTH_SCHOOLID_NULL);
				throw new PostDataException(resultStatus);
			}
		}catch(NullPointerException exception){
			resultStatus.setResultDetail(RESULT_ERROR_MACAUTH_SCHOOLID_NULL);
			throw new PostDataException(resultStatus);
		}
	}

	private static void deleteMacAuthDataValidate(MACAuthModel macAuthModel, String operation) throws PostDataException{
		ResultStatus resultStatus = new ResultStatus(operation);
		resultStatus.setEffectRows(0);
		resultStatus.setResultFlag(ERROR);
		try{
			String studentId = macAuthModel.getStudentId().trim();
			if("null".equalsIgnoreCase(studentId)||"".equalsIgnoreCase(studentId)){
				resultStatus.setResultDetail(RESULT_ERROR_MACAUTH_STUDENTID_NULL);
				throw new PostDataException(resultStatus);
			}
		}catch(NullPointerException exception){
			resultStatus.setResultDetail(RESULT_ERROR_MACAUTH_STUDENTID_NULL);
			throw new PostDataException(resultStatus);
		}
		
		try{
			String schoolId = macAuthModel.getSchoolId().trim();
			if("null".equalsIgnoreCase(schoolId)||"".equalsIgnoreCase(schoolId)){
				resultStatus.setResultDetail(RESULT_ERROR_MACAUTH_SCHOOLID_NULL);
				throw new PostDataException(resultStatus);
			}
		}catch(NullPointerException exception){
			resultStatus.setResultDetail(RESULT_ERROR_MACAUTH_SCHOOLID_NULL);
			throw new PostDataException(resultStatus);
		}
	}

	private static MACAuth getMACAuth(String studentId, String schoolId,String operation) throws ItemNotFoundException{
		List<MACAuth> macAuthList = QueryUtil.executeQuery(MACAuth.class, null, new FilterParams("studentId=:s1 AND schoolId=:s2", new String[]{studentId,schoolId}));
		if(macAuthList.isEmpty()){
			ResultStatus resultStatus = new ResultStatus(operation);
			resultStatus.setEffectRows(0);
			resultStatus.setResultFlag(ERROR);
			resultStatus.setResultDetail(RESULT_ERROR_OBJECT_NOTXEXIST);
			throw new ItemNotFoundException(resultStatus);
		}else{
			return macAuthList.get(0);
		}
	}

	private static HmDomain getGlobalDomain() throws Exception {
		HmDomain globalDomain = null;
		FilterParams filterParams = new FilterParams("domainName",
				HmDomain.GLOBAL_DOMAIN);
		List<HmDomain> domainList = QueryUtil.executeQuery(HmDomain.class,
				null, filterParams);
		if (domainList.isEmpty()) {
			globalDomain = new HmDomain();
			globalDomain.setDomainName(HmDomain.GLOBAL_DOMAIN);
			QueryUtil.createBo(globalDomain);
		} else {
			globalDomain = domainList.get(0);
		}

		return globalDomain;
	}
}
