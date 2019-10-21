package demo.common.controller.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import demo.common.bean.auth.HBUserSession;
import demo.common.bean.enums.ApiCode;
import demo.common.bean.http.ResponseBean;
import demo.common.controller.BaseController;
import demo.common.dao.LocalCacheDao;
import demo.common.service.AuthService;
import demo.common.util.set.HBStringUtil;
import demo.user.bean.http.HBUserLogin;
import demo.user.service.UserService;

@RestController
@RequestMapping(value = { "/${api.version}/f/auth/token", "/${api.version}/b/auth/token" })
public class AuthTokenController extends BaseController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Resource
    private LocalCacheDao stringRedisDao;

    /**
     * 使用用户名登陆
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/login", method = { RequestMethod.POST })
    public ResponseBean getTokenByNP(@RequestBody HBUserLogin hbuser) {
        ResponseBean responseBean = getReturn();
        String username = hbuser.getUserName();
        String password = hbuser.getPassword();
        if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
            try {
                HBUserSession session = authService.login(username, password);
                if (session != null) {
                    hbuser = userService.getLoginUserInfo(session.getUserid());
                    hbuser.setJwtToken(session.getToken());
                    responseBean.setData(hbuser);
                    userService.recordUserLogin(hbuser.getId(), request.getRemoteAddr());
                } else {
                    responseBean.setCode(ApiCode.PARAM_CONTENT_ERROR.getCode());
                    responseBean.setErrMsg("用户名密码不正确");
                }
            } catch (Exception e) {
                logger.debug("登陆失败", e);
                responseBean.setCode(ApiCode.PARAM_CONTENT_ERROR.getCode());
                responseBean.setErrMsg("用户名密码不正确");
            }
        } else {
            logger.debug("用户传来的信息格式错误" + hbuser);
            responseBean.setCode(ApiCode.PARAM_CONTENT_ERROR.getCode());
            responseBean.setErrMsg("登陆信息格式错误");
        }
        return returnBean(responseBean);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/refresh", method = { RequestMethod.GET })
    public ResponseBean refreshToken(HttpServletRequest request) {
        ResponseBean responseBean = getReturn();
        String userid = getUseridFromRequest();
        if (HBStringUtil.isNotBlank(userid)) {
            HBUserSession session = authService.loginById(userid);
            responseBean.setOneData("jwtToken", session.getToken());
        } else {
            responseBean.setCodeEnum(ApiCode.NO_AUTH);
        }
        return returnBean(responseBean);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/refreshUser", method = { RequestMethod.GET })
    public ResponseBean refreshUser(HttpServletRequest request) {
        ResponseBean responseBean = getReturn();
        String userid = getUseridFromRequest();
        HBUserLogin hbuser = userService.getLoginUserInfo(userid);
        responseBean.setData(hbuser);
        // 注意这里没有去刷新jwtToken，也没有修改redis中的内容，可能引起数据不一致
        return returnBean(responseBean);
    }

    /**
     * 使用用户ID密码登陆
     */
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/checkPasswordValid", method = { RequestMethod.POST })
    public ResponseBean checkPasswordValid(@RequestBody HBUserLogin hbuser) {
        ResponseBean responseBean = getReturn();
        String userid = getUseridFromRequest();
        String password = hbuser.getPassword();
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(password)) {
            responseBean.setErrMsg("用户没有权限");
            responseBean.setCode(ApiCode.NO_AUTH.getCode());
        } else {
            responseBean.setData(authService.checkPasswordValid(userid, password) != null);
        }
        return returnBean(responseBean);
    }

}
