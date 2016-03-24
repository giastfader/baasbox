import org.junit.Test;

import play.mvc.Http.Status;
import play.libs.F.Callback;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.TestBrowser;
import core.AbstractTest;
import core.TestConfig;

import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

import java.util.HashMap;


public class AdminDropDBTest extends AbstractTest{
	@Test
	public void testDeleteDB() throws Exception
	{
		running
		(
			getTestServer(), 
			HTMLUNIT, 
			new Callback<TestBrowser>() 
	        {
				public void invoke(TestBrowser browser) 
				{
					String sAuthEnc = TestConfig.AUTH_ADMIN_ENC;
					
					setHeader(TestConfig.KEY_APPCODE, TestConfig.VALUE_APPCODE);
					setHeader(TestConfig.KEY_AUTH, sAuthEnc);
					
					int status = httpRequest("http://localhost:3333/admin/db/2000", "DELETE",new HashMap<String,String>());
					assertTrue("Failed! Status: " + status,status==200);	
				}
			}
			);
	}

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
