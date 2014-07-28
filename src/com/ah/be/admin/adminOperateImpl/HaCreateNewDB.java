package com.ah.be.admin.adminOperateImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import arlut.csd.ganymede.boolean_field;

import com.ah.be.log.BeLogTools;

public class HaCreateNewDB {
	/**
	 * param 1:db url
	 * param 2:username
	 * param 3:password
	 * param 4:db name
	 * @param args
	 * @return void
	 * @author fhu
	 * @date Feb 20, 2012
	 */
	public static void main(String[] args){

		/*if(args.length < 4){
			BeLogTools.restoreLog(BeLogTools.ERROR, "class:HaCreateNewDB. number of parameters less then 4,cann't get db connection!");
			return;
		}*/
		Connection db = null;
		Statement st = null;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			String checkSql = "select datname from pg_database where datname='" + args[3] + "'";
			String createSql = "create database " + args[3] + " owner hivemanager TABLESPACE = pg_default";
			String dropSql = "drop database " + args[3];
			db = DriverManager.getConnection(args[0], args[1], args[2]);

			st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(checkSql);
			boolean boo = rs.last();
			if(boo)
				st.execute(dropSql);
			st.execute(createSql);

		} catch (SQLException e) {
			BeLogTools.restoreLog(BeLogTools.ERROR, "class:HaCreateNewDB. create new db failed!");
			e.printStackTrace();
		} finally{
			try {
				st.close();
				db.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}
}
