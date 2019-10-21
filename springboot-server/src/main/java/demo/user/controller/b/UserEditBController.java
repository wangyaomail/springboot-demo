package demo.user.controller.b;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import demo.common.bean.enums.ApiCode;
import demo.common.bean.http.ResponseBean;
import demo.common.controller.BaseBeanCRUDController;
import demo.common.service.AuthService;
import demo.user.bean.http.HBUserAccount;
import demo.user.bean.http.HBUserBasic;
import demo.user.bean.http.HBUserEdit;
import demo.user.service.UserService;

/**
 * 后台对各个用户数据的管理
 */
@RestController
@RequestMapping(value = "/${api.version}/b/user/edit")
public class UserEditBController extends BaseBeanCRUDController<HBUserEdit> {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/get/{id}", method = { RequestMethod.GET })
    public ResponseBean get(@PathVariable("id") String id) {
        return super.get(id);
    }

    @Override
    protected HBUserEdit prepareUpdate(HBUserEdit user,
                                       ResponseBean responseBean) {
        HBUserAccount userAccount = mongoTemplate.findOne(new Query(Criteria.where("id")
                                                                            .is(user.getId())),
                                                          HBUserAccount.class);
        if (userAccount != null && !userAccount.getPassword().equals(user.getPassword())) {
            user.setPassword(authService.encodePassword(user.getPassword()));
        }
        return super.prepareUpdate(user, responseBean);
    }

    @RequestMapping(value = "/update", method = { RequestMethod.POST })
    public ResponseBean update(@RequestBody HBUserEdit object) {
        return super.update(object);
    }

    @RequestMapping(value = "/basic/{id}", method = { RequestMethod.GET })
    public ResponseBean getUserBasic(@PathVariable("id") String id) {
        ResponseBean responseBean = getReturn();
        if (StringUtils.isEmpty(id)) {
            responseBean.setCode(ApiCode.PARAM_CONTENT_ERROR.getCode());
            responseBean.setErrMsg("id号未传入");
        } else {
            responseBean.setData(mongoTemplate.findOne(new Query(Criteria.where("id").is(id)),
                                                       HBUserBasic.class,
                                                       "hb_users"));
        }
        return returnBean(responseBean);
    }

    @RequestMapping(value = "/alladmins", method = { RequestMethod.GET })
    public ResponseBean getAllAdmins() {
        ResponseBean responseBean = getReturn();
        responseBean.setData(userService.getAllAdmin());
        return returnBean(responseBean);
    }

    /**
     * 注册用户管理查询，只查询注册用户组，
     */
    @RequestMapping(value = "/normalquery", method = { RequestMethod.POST })
    public ResponseBean getContentById(@RequestBody HBUserEdit searhBean) {
        ResponseBean responseBean = getReturn();
        Query query = new Query();
        if (searhBean.getId() != null) {
            query.addCriteria(Criteria.where("id").is(searhBean.getId()));
        }
        if (searhBean.getValid() != null) {
            query.addCriteria(Criteria.where("valid").is(searhBean.getValid()));
        }
        if (searhBean.getUserName() != null) {
            query.addCriteria(Criteria.where("userName").is(searhBean.getUserName()));
        }
        query.addCriteria(Criteria.where("group").nin(searhBean.getGroup()));
        if (searhBean.getPage() != null) {
            List<Order> orders = new ArrayList<Order>(); // 排序
            orders.add(new Order(Direction.DESC, "regDate"));
            Sort sort = Sort.by(orders);
            searhBean.getPage().setSort(sort);
            Long count = mongoTemplate.count(query, HBUserEdit.class);
            searhBean.getPage().setTotalSize(count.intValue());
            query.with(searhBean.getPage());
        }
        List<HBUserEdit> list = mongoTemplate.find(query, HBUserEdit.class);
        searhBean.getPage().setList(list);
        responseBean.setData(searhBean.getPage());
        return returnBean(responseBean);
    }
}
