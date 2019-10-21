package demo.user.dao;

import org.springframework.stereotype.Repository;

import demo.common.dao.BaseMongoDao;
import demo.user.bean.http.HBUserEdit;

@Repository("userEditCachedDao")
public class UserEditCachedDao extends BaseMongoDao<HBUserEdit> {
}
