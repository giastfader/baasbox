package com.baasbox.service.SavedQuery;

import com.baasbox.dao.FileDao;
import com.baasbox.dao.SavedQueryDao.SavedQuery;
import com.baasbox.dao.UserDao;
import com.baasbox.db.DbHelper;
import com.baasbox.service.user.UserService;

public class SavedQueryService {

	public static String getSelectStatement(SavedQuery nq) {
		String select = null;
		switch (nq.getResource()){
		case DOCUMENTS:
			select = DbHelper.selectQueryBuilder(nq.getCollectionName(), false, nq.getCriteria().clone());
			break;
		case USERS:
			UserService.excludeInternalUsersFromCriteria(nq.getCriteria());
			select = DbHelper.selectQueryBuilder(UserDao.MODEL_NAME, false, nq.getCriteria().clone());
			break;
		case FILES:
			select = DbHelper.selectQueryBuilder(FileDao.MODEL_NAME, false, nq.getCriteria().clone());
			break;
		case FREE:
			select = nq.getFreeStatement();
			break;
		}	//switch
		return select;
	}

} //SavedQueryService
