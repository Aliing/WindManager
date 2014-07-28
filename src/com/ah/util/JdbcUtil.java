package com.ah.util;

import java.io.InputStream;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;

public class JdbcUtil {
	
	// suit for java 7 try-with,other version need close the statement
	public static boolean updateOrInsert( PreparedStatement statement,Object[] args) {
		try{
			for( int i =0 ; i < args.length ; i ++ ){
				if ( args[i] instanceof java.lang.Long){
					statement.setLong(i+1, (long)args[i]);
				}
				else if ( args[i] instanceof java.lang.Integer){
					statement.setInt(i+1, (int)args[i]);
				}
				else if ( args[i] instanceof java.lang.Byte){
					statement.setByte(i+1, (byte)args[i]);
				}
				else if ( args[i] instanceof java.lang.Byte[]){
					statement.setBytes(i+1, (byte[])args[i]);
				}
				else if ( args[i] instanceof java.lang.String){
					statement.setString(i+1, (String)args[i]);
				}
				else if ( args[i] instanceof java.sql.Timestamp){
					statement.setTimestamp(i+1, (Timestamp)args[i]);
				}
				else if ( args[i] instanceof java.lang.Boolean){
					statement.setBoolean(i+1, (boolean)args[i]);
				}
				else if ( args[i] instanceof java.sql.Array){
					statement.setArray(i+1, (Array)args[i]);
				}
				else if ( args[i] instanceof java.io.InputStream){
					statement.setBinaryStream(i+1, (InputStream)args[i],((InputStream)args[i]).available());
				}
				else {
					statement.setObject(i+1, args[i]) ;
				}
			}
			statement.executeUpdate();
			return true;
		}catch(Exception e){
			BeLogTools.error(HmLogConst.M_RESTORE,"JdbcUtil updateOrInsert hive_ap data into network_device_history table exception: "+e.getMessage(),e);
			return false;
		}
		
	}
	
	// suit for java 7 try-with,other version need close the statement
	public static ResultSet query( PreparedStatement statement,Object[] args) {
		try{
			for( int i =0 ; i < args.length ; i ++ ){
				if ( args[i] instanceof java.lang.Long){
					statement.setLong(i+1, (Long)args[i]);
				}
				else if ( args[i] instanceof java.lang.Integer){
					statement.setInt(i+1, (Integer)args[i]);
				}
				else if ( args[i] instanceof java.lang.Byte){
					statement.setByte(i+1, (Byte)args[i]);
				}
				else if ( args[i] instanceof java.lang.Byte[]){
					statement.setBytes(i+1, (byte[])args[i]);
				}
				else if ( args[i] instanceof java.lang.String){
					statement.setString(i+1, (String)args[i]);
				}
				else if ( args[i] instanceof java.sql.Timestamp){
					statement.setTimestamp(i+1, (Timestamp)args[i]);
				}
				else if ( args[i] instanceof java.lang.Boolean){
					statement.setBoolean(i+1, (boolean)args[i]);
				}
				else if ( args[i] instanceof java.sql.Array){
					statement.setArray(i+1, (Array)args[i]);
				}
				else if ( args[i] instanceof java.io.InputStream){
					statement.setBinaryStream(i+1, (InputStream)args[i],((InputStream)args[i]).available());
				}
				else {
					statement.setObject(i+1, args[i]) ;
				}
			}
			return statement.executeQuery();
		}catch(Exception e){
			BeLogTools.error(HmLogConst.M_RESTORE,"JdbcUtil query data exception: "+e.getMessage(),e);
			return null;
		}
	}

}
