package com.baasbox.controllers;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.baasbox.BBConfiguration;
import com.baasbox.controllers.actions.filters.ConnectToDBFilterAsync;
import com.baasbox.controllers.actions.filters.ExtractQueryParameters;
import com.baasbox.controllers.actions.filters.UserOrAnonymousCredentialsFilterAsync;
import com.baasbox.controllers.helpers.SavedQueryOrientChunker;
import com.baasbox.dao.SavedQueryDao;
import com.baasbox.dao.SavedQueryDao.SavedQuery;
import com.baasbox.dao.exception.SavedQueryDoesNotExistException;
import com.baasbox.dao.exception.SqlInjectionException;
import com.baasbox.db.DbHelper;
import com.baasbox.exception.InvalidAppCodeException;
import com.baasbox.service.SavedQuery.SavedQueryService;
import com.baasbox.service.logging.BaasBoxLogger;
import com.baasbox.util.ErrorToResult;
import com.baasbox.util.IQueryParametersKeys;
import com.baasbox.util.QueryParams;

import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.With;

public class SavedQueryController extends Controller {

	/*
	@With({UserOrAnonymousCredentialsFilterAsync.class, ConnectToDBFilterAsync.class, ExtractQueryParameters.class})
	public static Promise<Result> executeNamedQuery(String idQuery) {
		
	}
	*/
	
	@With({UserOrAnonymousCredentialsFilterAsync.class, ConnectToDBFilterAsync.class, ExtractQueryParameters.class})
	public static Promise<Result> execute(String queryName) {
		return F.Promise.promise(DbHelper.withDbFromContext(ctx(), () -> {
			
			return getQueryChunked(queryName);
			
		})).recover(ErrorToResult
				.when(Exception.class,
						e -> {
							BaasBoxLogger.error(ExceptionUtils.getFullStackTrace(e));
							return internalServerError(ExceptionUtils.getFullStackTrace(e));
						})
				.when(SavedQueryDoesNotExistException.class,
						e -> {
							return notFound(ExceptionUtils.getMessage(e));
						})
		); //recover
	}//execute
	
	//this method is called by execute()
	private static Result getQueryChunked(String queryName) throws InvalidAppCodeException {
		final Context ctx = Http.Context.current.get();
		QueryParams criteria = (QueryParams) ctx.args.get(IQueryParametersKeys.QUERY_PARAMETERS);
		if (criteria.isPaginationEnabled()) criteria.enablePaginationMore();
		return chunksFromNamedQuery(queryName);
	}

		
	private static Result chunksFromNamedQuery(String queryName) throws InvalidAppCodeException {

		final Context ctx = Http.Context.current.get();
		String select = "";
		SavedQuery nq = null;
				
		try{
			DbHelper.openFromContext(ctx);
			nq = SavedQueryDao.getInstance().get(queryName);
			if (nq==null) throw new SavedQueryDoesNotExistException();
			if (nq.isCanBeCalledOnlyByAdmins() && !DbHelper.isConnectedAsAdmin(true)){
					throw new UnsupportedOperationException();
			}
			if (DbHelper.isConnectedLikeBaasBox() && !nq.isCanBeCalledWithoutAuth()){
				throw new UnsupportedOperationException();
			}
		} catch (SavedQueryDoesNotExistException | SqlInjectionException  e){
			return notFound(queryName + " is not a valid query name");
		} catch (UnsupportedOperationException e){
			return forbidden("You cannot run this query");
		}finally{
			DbHelper.close(DbHelper.getConnection());
		}

		String user= DbHelper.getCurrentHTTPUsername();
		String pass= DbHelper.getCurrentHTTPPassword();  
		
		if (nq.isRunningAsAdmin()){
			BBConfiguration bbconf = BBConfiguration.getInstance();
			user = bbconf.getBaasBoxAdminUsername();
			pass = bbconf.getBaasBoxAdminPassword();
		} 
		  		
		final String appcode= DbHelper.getCurrentAppCode();
		SavedQueryOrientChunker chunks = new SavedQueryOrientChunker(
				appcode
				,user
				,pass
				,ctx,
				nq);
		
		select = SavedQueryService.getSelectStatement(nq);
		chunks.setQuery(select);
		return ok(chunks).as("application/json");
	}

	
}
