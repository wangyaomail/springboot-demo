package demo.user.controller.b;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import demo.common.bean.enums.ApiCode;
import demo.common.bean.http.ResponseBean;
import demo.common.controller.BaseCRUDController;
import demo.common.service.AuthService;
import demo.common.service.BaseCRUDService;
import demo.user.bean.http.HBUserAccount;
import demo.user.bean.http.HBUserChangPwd;
import demo.user.bean.mongo.HBUser;
import demo.user.dao.RoleDao;
import demo.user.service.UserService;

/**
 * 用户修改，正常用户修改，只能修改自己的相关信息
 */
@RestController
@RequestMapping(value = "/${api.version}/b/user")
public class UserBController extends BaseCRUDController<HBUser> {
    @Autowired
    private UserService userService;
    @Resource
    public RoleDao roleDao;
    @Autowired
    private AuthService authService;

    @Override
    protected BaseCRUDService<HBUser> getService() {
        return userService;
    }

    /**
     * 用户只能更新自己的信息
     */
    @Override
    protected HBUser prepareUpdate(HBUser hbuser,
                                   ResponseBean responseBean) {
        String userid = getUseridFromRequest();
        if (StringUtils.isEmpty(userid)) {
            responseBean.setCodeEnum(ApiCode.NO_AUTH);
        }
        return super.prepareUpdate(hbuser, responseBean);
    }

    @RequestMapping(value = "/update", method = { RequestMethod.POST })
    public ResponseBean update(@RequestBody HBUser object) {
        return super.update(object);
    }

    @Override
    protected HBUser prepareInsert(HBUser object,
                                   ResponseBean responseBean) {
        List<String> group = new ArrayList<>();
        if (object.getGroup() != null) {
            group = object.getGroup();
        }
        group.add("normal");// 所有用户归为normal用户组
        if (object.getLastPasswordResetDate() != null) {
            // 这个如果不设置，解析jwttoken会出问题
            object.setLastPasswordResetDate(new Date());
        }
        // 最后进行一下重置
        object = userService.initNewHBUser(object.toMongoHashMap());
        object.setGroup(group);
        return super.prepareInsert(object, responseBean);
    }

    @RequestMapping(value = "", method = { RequestMethod.PUT })
    public ResponseBean insert(@RequestBody HBUser object) {
        return super.insert(object);
    }

    @Override
    protected HBUser postInsert(HBUser object,
                                ResponseBean responseBean) {
        return super.prepareInsert(object, responseBean);
    }

    @Override
    protected String prepareRemove(String id,
                                   ResponseBean responseBean) {
        // 删除钱包
        Map<String, Object> params = new HashMap<>();
        params.put("user", id);
        return super.prepareRemove(id, responseBean);
    }

    @RequestMapping(value = "/{id}", method = { RequestMethod.DELETE })
    public ResponseBean remove(@PathVariable("id") String id) {
        return super.remove(id);
    }

    @RequestMapping(value = "/changPwd", method = { RequestMethod.POST })
    public ResponseBean changPassword(@RequestBody HBUserChangPwd user) {
        ResponseBean responseBean = getReturn();
        String userid = getUseridFromRequest();
        if (StringUtils.isNotEmpty(userid) && StringUtils.isNotEmpty(user.getNewPass())
                && StringUtils.isNotEmpty(user.getRePass())) {
            if (!user.getNewPass().equals(user.getRePass())) {
                responseBean.setOneData(ApiCode.NO_DATA.getCode(), "信息错误-两次输入密码不一致");
                return responseBean;
            }
            if (authService.checkPasswordValid(userid, user.getOldPass()) != null) {
                Update update = new Update();
                update.set("password", authService.encodePassword(user.getNewPass()));
                mongoTemplate.updateFirst(new Query(Criteria.where("id").is(userid)),
                                          update,
                                          HBUserAccount.class);
            } else {
                responseBean.setOneData(ApiCode.NO_DATA.getCode(), "密码错误");
            }
        } else {
            responseBean.setOneData(ApiCode.NO_DATA.getCode(), "没有查询到有效的用户信息");
        }
        return responseBean;
    }
}
