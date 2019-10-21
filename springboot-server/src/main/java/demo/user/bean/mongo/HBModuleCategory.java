package demo.user.bean.mongo;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import demo.common.bean.mongo.BaseTreeMgBean;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模块策略
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "hb_modules_categorys")
public class HBModuleCategory extends BaseTreeMgBean<HBModuleCategory> implements Serializable {
    private static final long serialVersionUID = -1580843791657747151L;
    @Id
    private String id; // id
}
