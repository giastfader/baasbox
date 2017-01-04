package com.baasbox.db;

import java.util.List;

import com.baasbox.dao.PermissionTagDao;
import com.baasbox.dao.exception.SqlInjectionException;
import com.baasbox.service.logging.BaasBoxLogger;
import com.orientechnologies.orient.core.command.OCommandRequest;
import com.orientechnologies.orient.core.db.ODatabaseRecordWrapperAbstract;
import com.orientechnologies.orient.core.db.record.ODatabaseRecordTx;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClusters;
import com.orientechnologies.orient.core.record.ORecordInternal;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.storage.OStorage;



/**
 * Created by eto on 10/23/14.
 */

/**
 * Issue 914 - Enormous default.pcl file size
 * https://github.com/baasbox/baasbox/issues/914
 * This evolution fixes the database removing old BLOBs associated to deleted file objects
 * @author giastfader
 *
 */
public class Evolution_a_001_000_000_010 implements IEvolution {
    private String version = "a.001.000.000.010";

    @Override
    public String getFinalVersion() {
        return version;
    }

    @Override
    public void evolve(ODatabaseRecordTx db) {
        BaasBoxLogger.info("Applying evolutions to evolve to the " + version + " level");
        try{
            createSavedQueryDataStructure(db);
            createPermissionTag(db);
        }catch (Throwable e){
        	BaasBoxLogger.error("Error applying evolution to " + version + " level!!" ,e);
            throw new RuntimeException(e);
        }
        BaasBoxLogger.info ("DB now is on " + version + " level");
    }


    private void createPermissionTag(ODatabaseRecordTx db) {
    	try {
			if (!PermissionTagDao.getInstance().existsPermissionTag("query.read")) {
					PermissionTagDao.getInstance().createReserved("baasbox.query.read","Allows the execution of pre-saved queries on server.");
			}
		} catch (SqlInjectionException e) {
			BaasBoxLogger.warn("Permission for tag query.read already exists");
		} catch (Throwable e) {
			BaasBoxLogger.error("Error creating permission tag for SavedQueries",e);
		}
	}

	private void createSavedQueryDataStructure(ODatabaseRecordTx db){
    	BaasBoxLogger.info("Starting creating SavedQueries data structure...");
    	 DbHelper.execMultiLineCommands(db,true,
                 "create class _BB_Saved_Query;",
                 "create property _BB_Saved_Query.name String;"
          		);
    	BaasBoxLogger.info("...done!");
    }

   
}
