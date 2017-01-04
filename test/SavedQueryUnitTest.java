/*
     Copyright 2012-2013 
     Claudio Tesoriero - c.tesoriero-at-baasbox.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

// @author: Marco Tibuzzi

import static play.test.Helpers.running;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Assert;
import org.junit.Test;

import com.baasbox.dao.SavedQueryDao;
import com.baasbox.dao.SavedQueryDao.SavedQuery;
import com.baasbox.db.DbHelper;
import com.baasbox.util.BBJson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.orientechnologies.orient.core.db.record.ODatabaseRecordTx;

import core.AbstractTest;

public class SavedQueryUnitTest extends AbstractTest
{
	
	@Test
	public void testDaoCreateWithVars()	{
		running(
			getFakeApplication(), 
			new Runnable() 	{
				public void run() 	{
					
					ODatabaseRecordTx db=null;
					try{
						String name = "qry_test_" + UUID.randomUUID().toString();
 						db = DbHelper.open("1234567890", "admin", "admin");
 						
						SavedQuery nq = SavedQuery.build(
								name, 
								SavedQueryDao.Resources.DOCUMENTS, 
								"test", 
								"*", 
								null, 
								null, 
								null, 
								null,
								0, 
								0, 
								0, 
								null, 
								null, 
								false,
								false,
								false, false, false);
						SavedQueryDao dao = SavedQueryDao.getInstance();
						dao.create(nq);
					} catch (Throwable e) {
						Assert.fail(ExceptionUtils.getMessage(e));
					}finally{
						DbHelper.close(db);
					}
				}
			}
			);
	}//testDaoCreateWithVars

	@Test
	public void testDaoCreateWithJSON()	{
		running(
			getFakeApplication(), 
			new Runnable() 	{
				public void run() 	{
					
					ODatabaseRecordTx db=null;
					try{
						String name = "qry_test_" + UUID.randomUUID().toString();
 						db = DbHelper.open("1234567890", "admin", "admin");
 						
 						String strJson = "{" + 
 						   "\"name\":\"" + name + "\"," +
 						   "\"resource\":\"DOCUMENTS\"," +
 						   "\"collection_name\": \"the collection name\"," +
 						   "\"fields\": \"first_name, last_name,....\"," +
 						   "\"where\":\"\"," +
 						   "\"groupBy\":\"....\"," +
 						   "\"orderBy\":\"....\"," +
 						   "\"free_statement\":\"... free db statement...\"," +
 						   "\"recordsPerPage\":10," +
 						   "\"skip\": 10," +
 						   "\"page\": 0," +
 						   "\"params\": [\"\",\"\",3]," +
 						   "\"fetchPlan\": \"a value of the JSONFormat Enumerator, or a valid ODB fetchPlan string\"," +
 						   "\"runAsAdmin\":true," +
 						   "\"can_override_pagination\": true," +
 						   "\"can_override_parameters\":true" +
 						"}";
 						
 						JsonNode objJson = BBJson.mapper().readTree(strJson);
 						
						SavedQuery nq = SavedQuery.build((ObjectNode) objJson);
						SavedQueryDao dao = SavedQueryDao.getInstance();
						dao.create(nq);
					} catch (Throwable e) {
						Assert.fail(ExceptionUtils.getMessage(e));
					}finally{
						DbHelper.close(db);
					}
				}
			}
			);
	}//testDaoCreateWithJSON
	
	@Test
	public void testDaoGetAllWithVars()	{
		running(
			getFakeApplication(), 
			new Runnable() 	{
				public void run() 	{
					
					ODatabaseRecordTx db=null;
					try{
 						db = DbHelper.open("1234567890", "admin", "admin");
 						SavedQueryDao.getInstance().getAll();
					}catch (Throwable e) {
						Assert.fail(ExceptionUtils.getMessage(e));
					}finally{
						DbHelper.close(db);
					}
				}
			});
	}//testDaoGetAllWithVars
	
	@Test
	public void testDaoDelete()	{
		running(
			getFakeApplication(), 
			new Runnable() 	{
				public void run() 	{
					
					ODatabaseRecordTx db=null;
					try{
						String name = "qry_test_" + UUID.randomUUID().toString();
 						db = DbHelper.open("1234567890", "admin", "admin");
 						
						SavedQuery nq = SavedQuery.build(
								name, 
								SavedQueryDao.Resources.DOCUMENTS, 
								"test", 
								"*", 
								null, 
								null, 
								null, 
								null,
								0, 
								0, 
								0, 
								null, 
								null, 
								false,
								false,
								false,false,false);
						SavedQueryDao dao = SavedQueryDao.getInstance();
						dao.create(nq);
						List<SavedQuery> listBefore = dao.getAll();
						dao.delete(name);
						List<SavedQuery> listAfter = dao.getAll();
						Assert.assertTrue("testDaoDelete: " + listBefore.size() + " - " + listAfter.size()
							,listBefore.size()==listAfter.size() + 1);
					} catch (Throwable e) {
						Assert.fail(ExceptionUtils.getMessage(e));
					}finally{
						DbHelper.close(db);
					}
				}
			}
			);
	}//testDaoDelete
	
	
	@Override
	public String getRouteAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void assertContent(String s) {
		// TODO Auto-generated method stub
		
	}
	
}
