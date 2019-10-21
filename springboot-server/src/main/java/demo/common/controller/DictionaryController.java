package demo.common.controller;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import demo.common.bean.anno.RegisterMethod;
import demo.common.bean.http.ResponseBean;
import demo.common.dao.LocalCacheDao;
import demo.common.service.TimerService;
import demo.user.service.ModuleService;
import demo.user.service.RoleService;
import demo.user.service.UserGroupService;

/**
 * 返回数据常用字典数据
 */
@RestController
@RequestMapping(value = { "/${api.version}/b/dictionary", "/${api.version}/f/dictionary" })
public class DictionaryController extends BaseController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private UserGroupService userGroupService;
    @Resource
    private LocalCacheDao localCacheDao;

    @Override
    @RequestMapping(value = "/{func}", method = { RequestMethod.GET })
    public ResponseBean man(@PathVariable String func) {
        return super.man(func);
    }

    /**
     * 获取后台初始化系统需要用到的信息
     */
    @RegisterMethod()
    public JSONObject init() {
        String dicKey = "system-dictionary";
        JSONObject dicJson = (JSONObject) localCacheDao.get(dicKey);
        // 检查是否过期，手动实现5min的过期
        if (dicJson != null
                && dicJson.getLongValue("time") - TimerService.now_to_timestamp < 5l * 60 * 1000) {
            dicJson = null;
        }
        if (dicJson == null) {
            dicJson = new JSONObject();
            dicJson.put("time", TimerService.now_to_timestamp);
            dicJson.put("roles", roleService.dao().findAll());
            dicJson.put("modules", moduleService.dao().findAll());
            dicJson.put("groups", userGroupService.dao().findAll());
            localCacheDao.put(dicKey, dicJson);
        }
        return dicJson;
    }

}
