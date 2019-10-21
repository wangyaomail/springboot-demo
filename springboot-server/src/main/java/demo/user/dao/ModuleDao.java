package demo.user.dao;

import org.springframework.stereotype.Repository;

import demo.common.dao.BaseLocalMongoCacheDao;
import demo.user.bean.mongo.HBModule;

@Repository("moduleDao")
public class ModuleDao extends BaseLocalMongoCacheDao<HBModule> {
}
