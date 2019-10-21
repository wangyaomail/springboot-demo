package demo.user.controller.b;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import demo.common.bean.http.ResponseBean;
import demo.common.controller.BaseCRUDController;
import demo.common.service.BaseCRUDService;
import demo.user.bean.mongo.HBModule;
import demo.user.service.ModuleService;

/**
 * 模块管理
 */
@RestController
@RequestMapping(value = "/${api.version}/b/module")
public class ModuleBController extends BaseCRUDController<HBModule> {
    @Autowired
    private ModuleService moduleService;

    @Override
    protected BaseCRUDService<HBModule> getService() {
        return moduleService;
    }

    @RequestMapping(value = "/query", method = { RequestMethod.POST })
    public ResponseBean query(@RequestBody HBModule object) {
        return super.query(object);
    }

    @RequestMapping(value = "/get/{id}", method = { RequestMethod.GET })
    public ResponseBean get(@PathVariable("id") String id) {
        return super.get(id);
    }

    @RequestMapping(value = "/update", method = { RequestMethod.POST })
    public ResponseBean update(@RequestBody HBModule object) {
        return super.update(object);
    }

    @RequestMapping(value = "/{id}", method = { RequestMethod.DELETE })
    public ResponseBean remove(@PathVariable("id") String id) {
        return super.remove(id);
    }

    /**
     * 获取后台初始化系统需要用到的信息
     */
    @RequestMapping(value = "/init", method = { RequestMethod.GET })
    public ResponseBean getInitSysTags() {
        ResponseBean responseBean = getReturn();
        responseBean.setData(moduleService.dao().findAll());
        return returnBean(responseBean);
    }
}
