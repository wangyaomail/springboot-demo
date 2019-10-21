package demo.user.controller.b;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import demo.common.bean.http.ResponseBean;
import demo.common.controller.BaseCRUDController;
import demo.common.service.BaseCRUDService;
import demo.user.bean.mongo.HBRole;
import demo.user.bean.mongo.HBUserGroup;
import demo.user.service.RoleService;
import demo.user.service.UserGroupService;

/**
 * 用户组管理
 */
@RestController
@RequestMapping(value = "/${api.version}/b/usergroup")
public class UserGroupBController extends BaseCRUDController<HBUserGroup> {
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private RoleService roleService;

    @Override
    protected BaseCRUDService<HBUserGroup> getService() {
        return userGroupService;
    }

    @RequestMapping(value = "/query", method = { RequestMethod.POST })
    public ResponseBean query(@RequestBody HBUserGroup object) {
        return super.query(object);
    }

    @RequestMapping(value = "/{id}", method = { RequestMethod.DELETE })
    public ResponseBean remove(@PathVariable("id") String id) {
        return super.remove(id);
    }

    @RequestMapping(value = "/update", method = { RequestMethod.POST })
    public ResponseBean update(@RequestBody HBUserGroup object) {
        return super.update(object);
    }

    @RequestMapping(value = "", method = { RequestMethod.PUT })
    public ResponseBean insert(@RequestBody HBUserGroup object) {
        return super.insert(object);
    }

    @RequestMapping(value = "/get/{id}", method = { RequestMethod.GET })
    public ResponseBean get(@PathVariable("id") String id) {
        return super.get(id);
    }

    @Override
    protected HBUserGroup prepareUpdate(HBUserGroup object,
                                        ResponseBean responseBean) {
        if (object.getRoles() != null) { // 增加roles中文介绍
            List<String> roles = new ArrayList<String>();
            List<String> rnames = new ArrayList<String>();
            for (String role : object.getRoles()) {
                HBRole r = roleService.dao().findOne(role);
                if (r == null) {
                    roles.add(role);
                } else {
                    rnames.add(r.getName());
                }
            }
            object.getRoles().removeAll(roles);
            object.setRolesname(rnames);
        }
        return super.prepareUpdate(object, responseBean);
    }

    /**
     * 获取后台初始化系统需要用到的信息
     */
    @RequestMapping(value = "/init", method = { RequestMethod.GET })
    public ResponseBean getInitSysTags() {
        ResponseBean responseBean = getReturn();
        responseBean.setData(userGroupService.dao().findAll());
        return returnBean(responseBean);
    }
}
