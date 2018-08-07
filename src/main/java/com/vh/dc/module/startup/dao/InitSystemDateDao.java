package com.vh.dc.module.startup.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface InitSystemDateDao {

	/*sys_user表是否存在，存在返回1不存在返回0*/
	@Select("select count(1) from sysobjects where name ='sys_user' and xtype = 'u' ")
	int hasUserTable();
	
	/*创建sys_user表*/
	void createUserTable();
	
	/* */
	@Select(" select count(1) from sys_user where user_code = 'sys_user' ")
	int hasUserAdmin();
	
	@Insert(" insert into sys_user (user_code,user_name,password ) values ('admin','admin','1')")
	void createUserAdmin();
}
