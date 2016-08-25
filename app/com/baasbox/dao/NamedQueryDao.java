package com.baasbox.dao;

import com.baasbox.db.DbHelper;
import com.orientechnologies.orient.core.db.record.ODatabaseRecordTx;

public class NamedQueryDao {
	
	 private ODatabaseRecordTx db;

	 protected NamedQueryDao(){
		 db = DbHelper.getConnection();
	 }

	 public static NamedQueryDao getInstance(){
	     return new NamedQueryDao();
	 }
}
