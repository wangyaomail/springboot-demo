package demo.user.dao;

import org.springframework.stereotype.Repository;

import demo.common.dao.BaseLocalMongoCacheDao;
import demo.user.bean.mongo.HBUserGroup;

@Repository("userGroupDao")
public class UserGroupDao extends BaseLocalMongoCacheDao<HBUserGroup> {
}
