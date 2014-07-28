package com.ah.test;

import java.io.Serializable;

import org.apache.commons.lang.SerializationUtils;

import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.bo.HmBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.useraccess.UserProfile;

import edu.emory.mathcs.backport.java.util.Arrays;

public class MatchTest {

	private static byte[] getObjectByteStream(Serializable obj) {
		long time = System.currentTimeMillis();
		byte[] res = SerializationUtils.serialize(obj);
		System.out.println("serialize spend: "+(System.currentTimeMillis() - time) + "ms, and length: "+res.length);
		return res;
//		
//		ByteArrayOutputStream byteOS = null;
//		ObjectOutputStream ObjectOS = null;
//		byte[] byteAry = null;
//		try{
//			byteOS = new ByteArrayOutputStream();
//			ObjectOS = new ObjectOutputStream(byteOS);
//			ObjectOS.writeObject(obj);
//			byteAry = byteOS.toByteArray();
//		}catch(IOException e){
//			e.printStackTrace();
//		}finally{
//			ObjectOS.close();
//			byteOS.close();
//		}
//		
//		return byteAry;
	}
	
	private static <T extends HmBo> T changeBo(T bo){
		UserProfile profile = (UserProfile)bo;
		
		profile.setDescription("1212121");
		return bo;
	}
	
	public static void main(String[] args) throws Exception{
		long up_id = (long)2881;
		UserProfile upObj = QueryUtil.findBoById(UserProfile.class, up_id, new ConfigLazyQueryBo());
		System.out.println("-------------------------------------------------------------------");
		
		byte[] ary_1 = getObjectByteStream(upObj);
		
		//do some change
		upObj = changeBo(upObj);
		byte[] ary_2 = getObjectByteStream(upObj);
		
		System.out.println("Is Profile Match: "+Arrays.equals(ary_1, ary_2));
	}
}
