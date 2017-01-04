package com.baasbox.controllers.helpers;

import org.apache.commons.lang.BooleanUtils;

import com.baasbox.dao.SavedQueryDao.SavedQuery;
import com.baasbox.db.DbHelper;
import com.baasbox.util.JSONFormats;
import com.baasbox.util.QueryParams;
import com.orientechnologies.orient.core.record.impl.ODocument;

import play.mvc.Http.Context;

public class SavedQueryOrientChunker extends AbstractOrientChunker {

	boolean withAcl = false; //for Documents
	SavedQuery nq;
	
	public SavedQueryOrientChunker(String appcode, String user, String pass,
			Context ctx, SavedQuery nq) {
		super (appcode,user,pass,ctx);
		if (ctx!=null) withAcl=BooleanUtils.toBoolean(ctx.request().getQueryString("withAcl"));
		
		//Overrides the context criteria 
		QueryParams criteriaToApply = nq.getCriteria().clone();
		if (nq.isCanOverridePagination() && criteria.getPage() !=-1){
			criteriaToApply.page(criteria.getPage());
			criteriaToApply.recordPerPage(criteria.getRecordPerPage());
			criteriaToApply.skip(criteria.getSkip());
		}
		if (nq.isCanOverrideParameters() && criteria.getParams().length!=0){
			criteriaToApply.params(criteria.getParams());
		}
		this.criteria = criteriaToApply.clone();
		this.nq = nq;
	} //SavedQueryOrientChunker constructor

	@Override
	protected String prepareDocToJson(ODocument doc) {
		String format = nq.getFetchPlan();
        try {
            DbHelper.filterOUserPasswords(true);
            if (this.withAcl) {
                return JSONFormats.prepareResponseToJson(doc, format, true);
            } else {
                return JSONFormats.prepareResponseToJson(doc, format, false);
            }
        } finally {
            DbHelper.filterOUserPasswords(false);
        }
	}	//prepareDocToJson

}
