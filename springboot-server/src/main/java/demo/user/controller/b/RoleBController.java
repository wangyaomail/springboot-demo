package demo.user.controller.b;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import demo.common.bean.http.ResponseBean;
import demo.common.controller.BaseCRUDController;
import demo.common.service.BaseCRUDService;
import demo.user.bean.mongo.HBModule;
import demo.user.bean.mongo.HBRole;
import demo.user.service.ModuleService;
import demo.user.service.RoleService;

/**
 * 角色管理
 */
@RestController
@RequestMapping(value = "/${api.version}/b/role")
public class RoleBController extends BaseCRUDController<HBRole> {
    @Autowired
    private RoleService roleService;
    @Autowired
    private ModuleService moduleService;

    @Override
    protected BaseCRUDService<HBRole> getService() {
        return roleService;
    }

    /**
     * 增加Modulesname字段
     */
    @Override
    protected HBRole prepareUpdate(HBRole role,
                                   ResponseBean responseBean) {
        if (role.getModules() != null) {
            List<String> mnames = new ArrayList<String>();
            for (String modules : role.getModules()) {
                HBModule module = moduleService.dao().findOne(modules);
                mnames.add(module.getName());
            }
            role.setModulesname(mnames);
        }
        return super.prepareUpdate(role, responseBean);
    }

    /**
     * 获取后台初始化系统需要用到的信息
     */
    @RequestMapping(value = "/init", method = { RequestMethod.GET })
    public ResponseBean getInitSysTags() {
        ResponseBean responseBean = getReturn();
        responseBean.setData(roleService.dao().findAll());
        return returnBean(responseBean);
    }
}
