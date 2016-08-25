package com.baasbox.controllers;

import com.baasbox.controllers.actions.filters.ConnectToDBFilterAsync;
import com.baasbox.controllers.actions.filters.ExtractQueryParameters;
import com.baasbox.controllers.actions.filters.UserOrAnonymousCredentialsFilterAsync;

import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.With;

public class NamedQuery extends Controller {

	/*
	@With({UserOrAnonymousCredentialsFilterAsync.class, ConnectToDBFilterAsync.class, ExtractQueryParameters.class})
	public static Promise<Result> executeNamedQuery(String idQuery) {
		
	}
	*/
}
