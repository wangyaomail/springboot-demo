package demo.user.dao;

import org.springframework.stereotype.Repository;

import demo.common.dao.BaseLocalMongoCacheDao;
import demo.user.bean.mongo.HBRole;

@Repository("roleDao")
public class RoleDao extends BaseLocalMongoCacheDao<HBRole> {
}
