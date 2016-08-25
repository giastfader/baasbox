package com.baasbox.dao;

import java.util.List;

import com.baasbox.BBConfiguration;
import com.baasbox.dao.exception.SqlInjectionException;
import com.baasbox.db.DbHelper;
import com.baasbox.util.BBJson;
import com.baasbox.util.JSONFormats;
import com.baasbox.util.QueryParams;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.orientechnologies.orient.core.db.record.ODatabaseRecordTx;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class NamedQueryDao {
	public static final String MODEL_NAME = "_BB_Named_Query";
	
	public NamedQuery build (String name, String fields, String where, String groupBy, String orderBy,
			long recordsPerPage, long skip, long page, String[] params, String fetchPlan, boolean runAsAdmin) {
		return new NamedQuery(
				name, fields, where, groupBy, orderBy,
				recordsPerPage, skip, page, params, fetchPlan, runAsAdmin
		);
	}
	
	public NamedQuery build (ObjectNode json){
		return build(BBJson.mapper().asTextOrNull(json.get(NAME)), 
				BBJson.mapper().asTextOrNull(json.get(FIELDS)), 
				BBJson.mapper().asTextOrNull(json.get(WHERE)), 
				BBJson.mapper().asTextOrNull(json.get(GROUP_BY)), 
				BBJson.mapper().asTextOrNull(json.get(ORDER_BY)), 
				BBJson.mapper().asLongOrDefault(json.get(RECORDS_PER_PAGE),BBConfiguration.getInstance().getQueryRecordsPerPage()), 
				BBJson.mapper().asLongOrDefault(json.get(SKIP),0), 
				BBJson.mapper().asLongOrDefault(json.get(PAGE),0), 
				BBJson.mapper().asArrayOfStringsOrNull(json.get(PARAMS)), 
				BBJson.mapper().asTextOrNull(json.get(FETCH_PLAN)), 
				BBJson.mapper().asBooleanOrDefault(json.get(RUN_AS_ADMIN),false)
		);
	}
	
	public class NamedQuery {
		private BBConfiguration configuration = BBConfiguration.getInstance();
		private String name;
		private String fields;
		private String where;
		private String groupBy;
		private String orderBy;
		private long recordsPerPage = configuration.getQueryRecordsPerPage();
		private long skip = 0;
		private long page = 0;
		private String[] params = new String[]{};
		private String fetchPlan = JSONFormats.Formats.DOCUMENT.toString();
		private boolean runAsAdmin = false;

		public String getName() {
			return name;
		}
		public String getFields() {
			return fields;
		}
		public String getWhere() {
			return where;
		}
		public String getGroupBy() {
			return groupBy;
		}
		public String getOrderBy() {
			return orderBy;
		}
		public long getRecordsPerPage() {
			return recordsPerPage;
		}
		public long getSkip() {
			return skip;
		}
		public long getPage() {
			return page;
		}
		public String[] getParams() {
			return params;
		}
		public String getFetchPlan() {
			return fetchPlan;
		}
		public boolean isRunningAsAdmin() {
			return runAsAdmin;
		}
		
		public ObjectNode toJSON(){
			ObjectNode toRet = BBJson.mapper().createObjectNode();
			toRet.put(NAME, getName());
			toRet.put(FIELDS, getFields());
			toRet.put(WHERE, getWhere());
			toRet.put(GROUP_BY, getGroupBy());
			toRet.put(ORDER_BY, getOrderBy());
			toRet.put(RECORDS_PER_PAGE, getRecordsPerPage());
			toRet.put(SKIP, getSkip());
			toRet.put(PAGE, getPage());
			toRet.put(PARAMS, BBJson.mapper().valueToTree(getParams()));
			toRet.put(FETCH_PLAN, getFetchPlan());
			toRet.put(RUN_AS_ADMIN, isRunningAsAdmin());
	
			return toRet;
		}
		
		private NamedQuery(String name, String fields, String where, String groupBy, String orderBy,
				long recordsPerPage, long skip, long page, String[] params, String fetchPlan, boolean runAsAdmin) {
			this.name = name;
			this.fields = fields;
			this.where = where;
			this.groupBy = groupBy;
			this.orderBy = orderBy;
			this.recordsPerPage = recordsPerPage;
			this.skip = skip;
			this.page = page;
			this.params = params;
			this.fetchPlan = fetchPlan;
			this.runAsAdmin = runAsAdmin;
		}
	}	//public class NamedQuery
	   
	public static final String NAME= "name";
    public static final String COLLECTION= "collection";
    public static final String FIELDS= "fields";
    public static final String WHERE= "where";
    public static final String GROUP_BY= "group_by";
    public static final String ORDER_BY= "order_by";
    
    public static final String RECORDS_PER_PAGE= "records_per_page";
    public static final String SKIP = "skip";
    public static final String PAGE ="page";
    public static final String PARAMS = "params";
    public static final String FETCH_PLAN = "fetchPlan";
    public static final String RUN_AS_ADMIN = "runAsAdmin";
    
    public static final String SELECT_STMT= "select_stmt";
    
    
	 private ODatabaseRecordTx db;
	 private GenericDao genericDao;

	 protected NamedQueryDao(){
		 db = DbHelper.getConnection();
		 genericDao = GenericDao.getInstance();
	 }

	 public static NamedQueryDao getInstance(){
	     return new NamedQueryDao();
	 }
	 
	 public ODocument get(String name) throws SqlInjectionException{
		 List<ODocument> listOfDoc = genericDao.executeQuery(
				 NAME, 
				 QueryParams.getInstance().where(NAME + " = ?").params(new String[]{name})
		 );
		 return listOfDoc.isEmpty() ? null : listOfDoc.get(0);
	 }
	 
	 public void delete(String name) throws SqlInjectionException{
		 ODocument docToDelete = get(name);
		 if (docToDelete != null) {
			 docToDelete.delete();
		 }
	 }
	 
	 public ODocument create(NamedQuery data){
		 String insert_statement = "insert into " + MODEL_NAME + " CONTENT " + 
	 }
	 
}
