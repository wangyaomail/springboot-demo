package demo.user.bean.http;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * 在一些基本的场合，提供用户最基础的信息返回 后台可以看到更多的信息
 */
@Data
@Document(collection = "hb_users")
public class HBUserBasicAdmin implements Serializable {
    private static final long serialVersionUID = 1926681181232419176L;
    @Id
    private String id; // 用户id
    private String userName; // 用户登录名
    private String nickName; // 用户昵称
    private String trueName; // 用户真实姓名

    public HBUserBasicAdmin() {}

    public HBUserBasicAdmin(String id) {
        setId(id);
    }
}
