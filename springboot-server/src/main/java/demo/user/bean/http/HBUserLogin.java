package demo.user.bean.http;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import demo.common.bean.mongo.BaseMgBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户登陆需要的类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "hb_users")
public class HBUserLogin extends BaseMgBean<HBUserLogin> implements Serializable {
    private static final long serialVersionUID = 4034391602063997660L;
    @Id
    private String id; // 用户id
    private String userName; // 用户名
    private String password; // 用户密码
    private String jwtToken; // 用户当前的jwt token，非存数据库
    @Transient
    private String smsCode; // 手机验证码，非存数据库
    private String phoneNum; // 用户手机号
    private String email; // 用户邮箱
    private Integer age; // 用户年龄
    private String gender; // 性别
    private Date regDate; // 用户注册日期
    private Boolean valid; // 用户是否有效
    private List<String> roles; // 角色
    private List<String> group; // 所属用户组
    private String company; // 用户所属公司
    private Integer level; // 用户等级
    private String nickName;// 用户昵称
    private String comment; // 用户个人签名，短（追在头像后面 30个字）
    private String logo; // 用户头像链接
    private Integer vip; // 会员，0-非会员，1-普通会员，...
    private String openId; // 微信openId
    @Transient
    private List<String> modules; // 用户所拥有的module，加载的时候填充
}
