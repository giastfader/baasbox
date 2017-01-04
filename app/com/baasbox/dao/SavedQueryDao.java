package com.baasbox.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.baasbox.BBConfiguration;
import com.baasbox.dao.exception.SavedQueryAlreadyExistsException;
import com.baasbox.dao.exception.SavedQueryDoesNotExistException;
import com.baasbox.dao.exception.SqlInjectionException;
import com.baasbox.util.BBJson;
import com.baasbox.util.JSONFormats;
import com.baasbox.util.JSONFormats.Formats;
import com.baasbox.util.QueryParams;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class SavedQueryDao {
	public static final String MODEL_NAME = "_BB_Saved_Query";

	public static enum Resources {
			USERS,
			DOCUMENTS,
			FILES,
			FREE
	};
	
	public static class SavedQuery {
		private BBConfiguration configuration = BBConfiguration.getInstance();
		private String name;
		private Resources resource;
		private String collectionName;
		private String fields;
		private String where;
		private String groupBy;
		private String orderBy;
		private String freeStatement;
		private int recordsPerPage = configuration.getQueryRecordsPerPage().intValue();
		private int skip = 0;
		private int page = 0;
		private Object[] params = new Object[]{};
		private String fetchPlan = JSONFormats.Formats.DOCUMENT.toString();
		private boolean runAsAdmin = false;
		private boolean canOverridePagination = false;
		private boolean canOverrideParameters = false;
		private boolean canBeCalledOnlyByAdmins = false;
		private boolean canBeCalledWithoutAuthentication = false;
		
		public String getName() {
			return name;
		}
		public String getCollectionName() {
			return collectionName;
		}
		public Resources getResource() {
			return resource;
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
		public int getRecordsPerPage() {
			return recordsPerPage;
		}
		public int getSkip() {
			return skip;
		}
		public int getPage() {
			return page;
		}
		public Object[] getParams() {
			return params;
		}
		public String getFetchPlan() {
			return fetchPlan;
		}
		public boolean isRunningAsAdmin() {
			return runAsAdmin;
		}
		public boolean isCanOverridePagination() {
			return canOverridePagination;
		}
		public boolean isCanOverrideParameters() {
			return canOverrideParameters;
		}
		
		public boolean isCanBeCalledOnlyByAdmins() {
			return canBeCalledOnlyByAdmins;
		}
		public boolean isCanBeCalledWithoutAuth() {
			return canBeCalledWithoutAuthentication;
		}
		
		public String getFreeStatement(){
			return freeStatement;
		}
		
		public QueryParams getCriteria(){
			QueryParams qp = QueryParams.getInstance();
			qp.fields(getFields())
			  .orderBy(getOrderBy())
			  .groupBy(getGroupBy())
			  .page(getPage())
			  .params(getParams())
			  .recordPerPage(getRecordsPerPage())
			  .skip(getSkip())
			  .where(getWhere());
			return qp;
		}
		
		public static SavedQuery build (String name, Resources resource, String collectionName, String fields, String where, String groupBy, String orderBy,
				String freeStatement,int recordsPerPage, int skip, int page, Object[] params, String fetchPlan, Boolean runAsAdmin,
				Boolean canOverridePagination, Boolean canOverrideParameters,
				Boolean canBeCalledOnlyByAdmin, Boolean canBeCalledWithoutAuth) {
			return new SavedQuery(
					name, resource, collectionName,fields, where, groupBy, orderBy, freeStatement,
					recordsPerPage, skip, page, params, fetchPlan, runAsAdmin, canOverridePagination,  canOverrideParameters,
					canBeCalledOnlyByAdmin,canBeCalledWithoutAuth
			);
		}
		
		public static SavedQuery build (ObjectNode json){
			return build(BBJson.mapper().asTextOrNull(json.get(NAME)), 
					Resources.valueOf(BBJson.mapper().asTextOrNull(json.get(RESOURCE)).toUpperCase()), 
					BBJson.mapper().asTextOrNull(json.get(COLLECTION_NAME)), 
					BBJson.mapper().asTextOrNull(json.get(FIELDS)), 
					BBJson.mapper().asTextOrNull(json.get(WHERE)), 
					BBJson.mapper().asTextOrNull(json.get(GROUP_BY)), 
					BBJson.mapper().asTextOrNull(json.get(ORDER_BY)), 
					BBJson.mapper().asTextOrNull(json.get(FREE_STATEMENT)), 
					BBJson.mapper().asIntOrDefault(json.get(RECORDS_PER_PAGE),BBConfiguration.getInstance().getQueryRecordsPerPage().intValue()), 
					BBJson.mapper().asIntOrDefault(json.get(SKIP),0), 
					BBJson.mapper().asIntOrDefault(json.get(PAGE),0), 
					BBJson.mapper().asArrayOfStringsOrNull(json.get(PARAMS)), 
					BBJson.mapper().asTextOrNull(json.get(FETCH_PLAN)), 
					BBJson.mapper().asBooleanOrDefault(json.get(RUN_AS_ADMIN),false),
					BBJson.mapper().asBooleanOrDefault(json.get(CAN_OVERRIDE_PAGINATION),false),
					BBJson.mapper().asBooleanOrDefault(json.get(CAN_OVERRIDE_PARAMETERS),false),
					BBJson.mapper().asBooleanOrDefault(json.get(CAN_BE_CALLED_ONLY_BY_ADMINS),false),
					BBJson.mapper().asBooleanOrDefault(json.get(CAN_BE_CALLED_WITHOUT_AUTH),false)
			);
		}
		
		public static SavedQuery build (ODocument doc){	
			return build(doc.field(NAME), 
					Resources.valueOf(((String)doc.field(RESOURCE)).toUpperCase()), 
					doc.field(COLLECTION_NAME), 
					doc.field(FIELDS), 
					doc.field(WHERE), 
					doc.field(GROUP_BY), 
					doc.field(ORDER_BY), 
					doc.field(FREE_STATEMENT), 
					doc.field(RECORDS_PER_PAGE), 
					doc.field(SKIP), 
					doc.field(PAGE), 
					((List)doc.field(PARAMS)==null?new ArrayList<String>():(List)doc.field(PARAMS)).stream().toArray(size -> new String[size]), 
					doc.field(FETCH_PLAN), 
					doc.field(RUN_AS_ADMIN),
					doc.field(CAN_OVERRIDE_PAGINATION),
					doc.field(CAN_OVERRIDE_PARAMETERS),
					doc.field(CAN_BE_CALLED_ONLY_BY_ADMINS),
					doc.field(CAN_BE_CALLED_WITHOUT_AUTH)
			);
		}//build (ODocument doc)
		
		public ObjectNode toJSON(){
			ObjectNode toRet = BBJson.mapper().createObjectNode();
			toRet.put(NAME, getName());
			toRet.put(RESOURCE, getResource().name().toUpperCase());
			toRet.put(COLLECTION_NAME, getCollectionName());
			toRet.put(FIELDS, getFields());
			toRet.put(WHERE, getWhere());
			toRet.put(GROUP_BY, getGroupBy());
			toRet.put(ORDER_BY, getOrderBy());
			toRet.put(FREE_STATEMENT, getFreeStatement());
			toRet.put(RECORDS_PER_PAGE, getRecordsPerPage());
			toRet.put(SKIP, getSkip());
			toRet.put(PAGE, getPage());
			toRet.put(PARAMS, BBJson.mapper().valueToTree(getParams()));
			toRet.put(FETCH_PLAN, getFetchPlan());
			toRet.put(RUN_AS_ADMIN, isRunningAsAdmin());
			toRet.put(CAN_OVERRIDE_PAGINATION, isCanOverridePagination());
			toRet.put(CAN_OVERRIDE_PARAMETERS, isCanOverrideParameters());
			toRet.put(CAN_BE_CALLED_ONLY_BY_ADMINS, isCanBeCalledOnlyByAdmins());
			toRet.put(CAN_BE_CALLED_WITHOUT_AUTH, isCanBeCalledWithoutAuth());
			return toRet;
		}
		
		
		@Override
		public String toString(){
			return this.toJSON().toString();
		}
		
		private SavedQuery(String name, Resources resource, String collectionName, QueryParams criteria, String fetchPlan,Boolean runAsAdmin,
				Boolean canOverridePagination, Boolean canOverrideParameters,
				Boolean canBeCalledOnlyByAdmins, Boolean canBeCalledWithoutAuthentication) {
			this (
					name,
					resource,
					collectionName,
					criteria.getFields(),
					criteria.getWhere(),
					criteria.getGroupBy(),
					criteria.getOrderBy(),
					"", //freeStatement
					criteria.getRecordPerPage(),
					criteria.getSkip(),
					criteria.getPage(),
					criteria.getParams(),
					fetchPlan,
					runAsAdmin,
					canOverridePagination,
					canOverrideParameters,
					canBeCalledOnlyByAdmins,
					canBeCalledWithoutAuthentication
					);
		}
		
		private SavedQuery(String name, Resources resource, String collectionName, String fields, String where, String groupBy, String orderBy, String freeStatement,
				int recordsPerPage, int skip, int page, Object[] params, String fetchPlan, Boolean runAsAdmin, Boolean canOverridePagination, Boolean canOverrideParameters,
				Boolean canBeCalledOnlyByAdmins, Boolean canBeCalledWithoutAuthentication) {
			if (StringUtils.isBlank(name)) throw new IllegalArgumentException("SavedQuery: the query name is mandatory and cannot be blank");
			this.name = name;
			this.resource=resource;
			this.collectionName=collectionName;
			this.fields = fields;
			this.where = where;
			this.groupBy = groupBy;
			this.orderBy = orderBy;
			this.freeStatement = freeStatement;
			this.recordsPerPage = recordsPerPage;
			this.skip = skip;
			this.page = page;
			this.params = params;
			this.fetchPlan = calculateFetchPlan(fetchPlan, resource) ;
			this.runAsAdmin = runAsAdmin != null ? runAsAdmin:false; 
			this.canOverridePagination = canOverridePagination != null ? canOverridePagination:false;
			this.canOverrideParameters = canOverrideParameters != null ? canOverrideParameters:false;
			this.canBeCalledOnlyByAdmins = canBeCalledOnlyByAdmins != null ? canBeCalledOnlyByAdmins : false;
			if (!canBeCalledOnlyByAdmins) {
				this.canBeCalledWithoutAuthentication = canBeCalledWithoutAuthentication != null ? canBeCalledWithoutAuthentication:false;
			} else canBeCalledWithoutAuthentication = false;
		}
		
		private String calculateFetchPlan(String fetchplan,Resources resource ){
			if (fetchplan != null ) return fetchplan;
			switch (resource){
			case DOCUMENTS:
				return Formats.DOCUMENT.toString();
			case USERS:
				return Formats.USER.toString();
			case FILES:
				return Formats.FILE.toString();
			case FREE:
				return Formats.GENERIC.toString();
			}
			return Formats.GENERIC.toString();
		}//calculateFetchPlan
		
	}	//public class SavedQuery
	   
	public static final String NAME= "name";
    public static final String RESOURCE= "resource";
    public static final String COLLECTION_NAME= "collection_name";
    public static final String FIELDS= "fields";
    public static final String WHERE= "where";
    public static final String GROUP_BY= "group_by";
    public static final String ORDER_BY= "order_by";
    public static final String FREE_STATEMENT= "free_statement";
    
    public static final String RECORDS_PER_PAGE= "records_per_page";
    public static final String SKIP = "skip";
    public static final String PAGE ="page";
    public static final String PARAMS = "params";
    public static final String FETCH_PLAN = "fetchPlan";
    public static final String RUN_AS_ADMIN = "runAsAdmin";
    public static final String CAN_OVERRIDE_PAGINATION = "can_override_pagination";
    public static final String CAN_OVERRIDE_PARAMETERS = "can_override_parameters";
    public static final String CAN_BE_CALLED_ONLY_BY_ADMINS = "admins_only";
    public static final String CAN_BE_CALLED_WITHOUT_AUTH = "anonymous_too";
    
    
    private GenericDao genericDao;

	 protected SavedQueryDao(){
		 genericDao = GenericDao.getInstance();
	 }

	 public static SavedQueryDao getInstance(){
	     return new SavedQueryDao();
	 }
	 
	 public List<SavedQuery> getAll() throws SqlInjectionException{
		 List<ODocument> listOfDoc = genericDao.executeQuery(
				 MODEL_NAME, 
				 QueryParams.getInstance().orderBy(SavedQueryDao.NAME)
		 );
		 return listOfDoc.stream().map(x->{
			 return SavedQuery.build(x);
		 }).collect(Collectors.toList());
	 }; //getAll
	 
	 public SavedQuery get(String name) throws SqlInjectionException{
		 List<ODocument> listOfDoc = genericDao.executeQuery(
				 MODEL_NAME, 
				 QueryParams.getInstance().where(NAME + " = ?").params(new String[]{name})
		 );
		 return listOfDoc.isEmpty() ? null : SavedQuery.build(listOfDoc.get(0));
	 }
	 
	 public void delete(String name) throws SqlInjectionException{
		 SavedQuery docToDelete = get(name);
		 if (docToDelete != null) {
			 GenericDao.getInstance().executeCommand("delete from " + MODEL_NAME + " where name = ?", new Object []{name});
		 }
	 }
	 
	 public SavedQuery create(SavedQuery data) throws SqlInjectionException, SavedQueryAlreadyExistsException {
		 if (this.get(data.getName()) !=null) throw new SavedQueryAlreadyExistsException("A query with the name ' + data.name + ' already exists");
		 String insert_statement = "insert into " + MODEL_NAME + " CONTENT " + data.toJSON().toString();
		 GenericDao.getInstance().executeCommand(insert_statement, new Object []{});
		 return this.get(data.getName());
	 }
	 
	 public SavedQuery update(SavedQuery data) throws SqlInjectionException, SavedQueryDoesNotExistException{
		 if (this.get(data.getName()) ==null) throw new SavedQueryDoesNotExistException("A query with the name ' + data.name + ' does not exist");
		 String insert_statement = "update " + MODEL_NAME + " CONTENT " + data.toJSON().toString() + " where name = ?";
		 GenericDao.getInstance().executeCommand(insert_statement, new Object []{data.getName()});
		 return this.get(data.getName());
	 }
}
