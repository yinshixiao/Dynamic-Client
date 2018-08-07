package com.vh.dc.module.startup.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vh.dc.module.startup.dao.InitSystemDateDao;

@Service
public class StartUpDataBase {

	private static Logger log = Logger.getLogger(StartUpDataBase.class);

	@Value("${spring.datasource.driver-class-name}")
	private String driver;

	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.data-username}")
	private String username;

	@Value("${spring.datasource.data-password}")
	private String password;
	
	private String dbName;

	@Autowired
	private InitSystemDateDao initSystemDateDao;

	public void startInit() {
		log.info("系统基础数据准备初始化");
		log.info("1开始检查用户配置的数据库是否存在");
		
		checkDataBaseExists();
		
		/*
		 * 数据库创建完毕，便可以开始走正常流程
		 * 检查用户表是否存在
		 * 检查admin用户是否存在
		 * */
		log.info("2开始检查用户表sys_user是否有存在");
		
		int i = initSystemDateDao.hasUserTable();
		if(i==0) {
			initSystemDateDao.createUserTable();
		}
	}

	/* 验证配置的数据库是否存在，若不存在则创建数据库，并初始化创建用户表和菜单权限表等 */
	private void checkDataBaseExists() {
		String tempUrl = url.substring(0, url.indexOf("=")) + "=tempdb";
		dbName = url.substring(url.indexOf("=") + 1, url.length());
		Connection conn = null;
		Statement statement = null;
		ResultSet dbcountRS = null;
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(tempUrl, username, password);
			conn.setAutoCommit(false);
			statement = conn.createStatement();
			dbcountRS = statement.executeQuery("select count(1) From master.dbo.sysdatabases where name='" + dbName + "' ");
			dbcountRS.next();
			int hasDb = dbcountRS.getInt(1);
			if(hasDb == 1) {
				log.info("配置的数据库："+dbName +"存在，无需创建");
				return ;
			}
			log.info("配置的数据库："+dbName +"不存在，开始创建");
			statement.executeUpdate(" create database "+dbName);
			conn.commit();
			log.info("配置的数据库："+dbName +" 创建完毕");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if (conn!=null) conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (dbcountRS != null)	
					dbcountRS.close();
				if (statement != null)	
					statement.close();
				if (conn != null)	
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
