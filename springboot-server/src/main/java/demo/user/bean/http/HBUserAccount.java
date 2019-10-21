package demo.user.bean.http;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import demo.common.bean.mongo.BaseMgBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用于修改用户的登陆信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "hb_users")
public class HBUserAccount extends BaseMgBean<HBUserAccount> implements Serializable {
    private static final long serialVersionUID = -7268794177985060187L;
    @Id
    private String id; // 用户id
    private String userName; // 用户名
    private String password; // 用户输入密码
}