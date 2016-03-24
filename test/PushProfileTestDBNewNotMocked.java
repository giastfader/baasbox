import static org.junit.Assert.assertTrue;
import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.routeAndCall;
import static play.test.Helpers.running;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;

import com.baasbox.BBConfiguration;

import play.libs.F.Callback;
import play.mvc.Http.Status;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.TestBrowser;
import core.TestConfig;


public class PushProfileTestDBNewNotMocked extends PushProfileAbstractTestNotMocked {

	private Boolean oldMockValue;
	public PushProfileTestDBNewNotMocked() {}

	@Before
	public void beforeTest(){
		running
		(
			getTestServerWithDefaultConf(), 
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
					
					oldMockValue=BBConfiguration.getInstance().getPushMock();
					BBConfiguration.getInstance()._overrideConfigurationPushMock(false);
				}//run
			}//Runnable() 
		);//running
	}//beforeTest()

	@After
	public void afterTest(){
		//import db
		running	(
			getTestServer(), 
			HTMLUNIT, 
			new Callback<TestBrowser>()  {
				public void invoke(TestBrowser browser) {
					BBConfiguration.getInstance()._overrideConfigurationPushMock(oldMockValue);
				}
			}
			);
	}
	
	@Override
	protected int getProfile1DisabledReturnCode() {
		return 503;
	}

	@Override
	protected int getProfile1SwitchReturnCode() {
		return 200;
	}
	
}//class
