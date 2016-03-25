/*
 * Copyright (c) 2014.
 *
 * BaasBox - info-at-baasbox.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.baasbox.db.hook;

import java.util.Date;
import java.util.List;

import com.baasbox.BBConfiguration;
import com.baasbox.BBInternalConstants;
import com.baasbox.dao.NodeDao;
import com.baasbox.db.DbHelper;
import com.baasbox.service.logging.BaasBoxLogger;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;


public class Audit extends BaasBoxHook {
	
	public static Audit getIstance(){
		return new Audit();
	}
	
	protected Audit() {
		super();
	}
	
	@Override
	 public com.orientechnologies.orient.core.hook.ORecordHook.RESULT onRecordBeforeCreate(ORecord<?> iRecord){
		if (BaasBoxLogger.isTraceEnabled()) BaasBoxLogger.trace("Method Start");
		if (iRecord instanceof ODocument){
			ODocument doc = (ODocument)iRecord;
				if ( 
					 ( doc.field("type")!=null && !doc.field("type").equals(BBInternalConstants.FIELD_AUDIT) )
					||
					 ( doc.field("type")==null )
					){
					if(!doc.isEmbedded() && doc.getClassName()!=null && (doc.getSchemaClass().isSubClassOf(NodeDao.CLASS_NODE_NAME) || doc.getSchemaClass().getName().equals("E") || doc.getSchemaClass().isSubClassOf("E"))){
						if (BaasBoxLogger.isDebugEnabled()) BaasBoxLogger.debug("  AuditHook.onRecordBeforeCreate: creation of audit fields for document " + doc.getIdentity());
						ODocument auditDoc = new ODocument();
						Date data = new Date();
						ORID user = getCurrentUserRid(iRecord);
						auditDoc.field("type",BBInternalConstants.FIELD_AUDIT);
						auditDoc.field("createdBy",user);
						auditDoc.field("createdOn",data); 
						auditDoc.field("modifiedBy",user);
						auditDoc.field("modifiedOn",data);
						doc.field(BBInternalConstants.FIELD_AUDIT,auditDoc);		
						return RESULT.RECORD_CHANGED;
					}//doc.getClassName()
				}
		}//iRecord instanceof ODocument
		if (BaasBoxLogger.isTraceEnabled()) BaasBoxLogger.trace("Method End");
		return RESULT.RECORD_NOT_CHANGED;
	 }//onRecordBeforeCreate

	private ORID getCurrentUserRid(ORecord<?> iRecord) {
		ORID user = null;
		if (BBConfiguration.getInstance().isConfiguredDBLocal()){
			user = iRecord.getDatabase().getUser().getDocument().getIdentity();
		} else {
			//the OUser object, when using remote connection, does not have any RID
			user = ((ODocument)((ODocument)((List) DbHelper.genericSQLStatementExecute("select @rid as rid from OUser where name = ?", new String[]{iRecord.getDatabase().getUser().getName()}))
					.get(0)).field("rid")).getIdentity();
		}
		return user;
	}

	@Override
	 public com.orientechnologies.orient.core.hook.ORecordHook.RESULT onRecordBeforeUpdate (ORecord<?> iRecord){
		if (BaasBoxLogger.isTraceEnabled()) BaasBoxLogger.trace("Method Start");
		if (iRecord instanceof ODocument){
			ODocument doc = (ODocument)iRecord;
				if ( 
					 ( doc.field("type")!=null && !doc.field("type").equals(BBInternalConstants.FIELD_AUDIT) )
					||
					 ( doc.field("type")==null )
					){
					if(!doc.isEmbedded() && doc.getClassName()!=null && doc.getSchemaClass().isSubClassOf(NodeDao.CLASS_NODE_NAME)){
						if (BaasBoxLogger.isDebugEnabled()) BaasBoxLogger.debug("  AuditHook.onRecordBeforeUpdate: update of audit fields for ORecord:{} " , iRecord.getIdentity());
						ODocument auditDoc = doc.field(BBInternalConstants.FIELD_AUDIT);
						if (auditDoc==null) auditDoc = new ODocument();
						Date data = new Date();
						auditDoc.field("modifiedBy",getCurrentUserRid(iRecord));
						auditDoc.field("modifiedOn",data);
						doc.field(BBInternalConstants.FIELD_AUDIT,auditDoc);	
						if (BaasBoxLogger.isDebugEnabled()) BaasBoxLogger.debug("  AuditHook.onRecordBeforeUpdate: update of audit fields for ORecord: {} done." , iRecord.getIdentity());
						return RESULT.RECORD_CHANGED;
					}
				}
		}
		if (BaasBoxLogger.isTraceEnabled()) BaasBoxLogger.trace("Method End");
		return RESULT.RECORD_NOT_CHANGED;
	 }//onRecordBeforeUpdate

	@Override
	public String getHookName() {
		return "Audit";
	}
}
