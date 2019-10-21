package demo.common.service;

import java.lang.reflect.Field;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import demo.common.util.set.HBStringUtil;
import lombok.Getter;

/**
 * 所有配置文件内的程序用到的配置项都从这里走，这样整个系统的调用是干净的，可以很快知道哪个配置项有没有用到
 * @INFO 使用PropertiesUtil线下生成
 * @INFO 将来会改为这里是默认值，如果需要改动再配置到property文件中的方式
 */
@Getter
@Service
public class SysConfService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private Environment environment;

    /**
     * 从这里对所有配置项进行填入
     */
    @PostConstruct
    public void init() {
        Field[] fields = SysConfService.class.getDeclaredFields();
        boolean goon = false;
        for (Field field : fields) {
            if (field.getName().equals("runOnDev")) {
                // 从这个配置开示
                goon = true;
            }
            if (goon) {
                try {
                    char[] keyChars = field.getName().toCharArray();
                    StringBuilder sb = new StringBuilder();
                    for (char c : keyChars) {
                        if (Character.isUpperCase(c)) {
                            sb.append(".").append(Character.toLowerCase(c));
                        } else {
                            sb.append(c);
                        }
                    }
                    String propertyValue = environment.getProperty(sb.toString());
                    if (HBStringUtil.isNotBlank(propertyValue)) {
                        field.setAccessible(true);
                        switch (field.getType().getName()) {
                        case "java.lang.Boolean":
                            field.set(this, Boolean.parseBoolean(propertyValue));
                            break;
                        case "java.lang.Integer":
                            field.set(this, Integer.parseInt(propertyValue));
                            break;
                        case "java.lang.Long":
                            field.set(this, Long.parseLong(propertyValue));
                            break;
                        case "java.lang.String":
                            field.set(this, propertyValue);
                        default:
                            break;
                        }
                    }
                    logger.info("配置项【" + sb.toString() + "】=" + field.get(this));
                } catch (Exception e) {
                    logger.error("配置项【" + field.getName() + "】读取出错", e);
                }
            }
        }
    }

    // 从这里开始都是配置文件内的变量，如果需要赋值默认值直接后面设置就行
    private Boolean runOnDev = true;
    private String springProfilesActive;
    private String serverHost = "localhost";
    private Integer serverPort = 9001;
    private Boolean springDevtoolsRestartEnabled;
    // 各种开关，默认都是关闭的，如果需要开启在配置文件中进行开启
    private Boolean switchOnlineQueue = true; // 是否启用队列服务
    // mongo的配置，spring开头的配置必须在配置文件中写
    private String springDataMongodbHost;
    private Integer springDataMongodbPort;
    private String springDataMongodbDatabase;
    private String springDataMongodbAuthenticationDatabase;
    private String springDataMongodbUsername;
    private String springDataMongodbPassword;
    private String mongoHomeLoc = "/data/libs/mongo";
    private String apiPrefixName = "server";
    private String apiVersion = "v1";
    private String springApplicationName = "springboot-server";
    // 特殊名称
    private String anonymouslyModuleName = "no_auth"; // 系统模块中匿名的模块
    // 队列相关
    private String scheduledThreadPoolSize;
    private Integer queueJobSize;
    private Integer queueSlaveSize;
    private Integer queueExitWaitSecond;
    private Boolean threadFactoryMakeDaemon;
    private String loggingLevelRoot;
    private String loggingLevelHb;
    private String loggingLevelSpringfoxDocumentationSpringWeb;
    private String loggingLevelHbBkUserAuthFilterMyFilterSecurityInterceptor;
    private String loggingLevelOrgSpringframeworkWebServletMvcMethodAnnotationRequestMappingHandlerMapping;
    private String loggingLevelOrgSpringframeworkDataMongodbCoreMappingBasicMongoPersistentProperty;
    private String loggingPatternConsole;
    private String loggingPatternFile;
    private Boolean useSystemLog = false; // 是否使用系统日志，主动记录log到mongo中的时候用到
    private String sysLogDir;
    // 上传下载相关
    private long mongoTempCleanPeriod = 24l * 60 * 60; // mongo中的临时表的数据清理时间，默认24小时，对应hb_temp
    private String downloadLoc = "/data/images/download/";
    private String tempLoc = "/data/images/temp/";
    // 账户配置
    private String tokenHeader = "jwttoken"; // 秘钥在用户浏览器的header中保留的头数据
    private String jwtSecret;
    private Long sessionExpiration = 10800000l; // 用户session过期时间（默认3小时）
    private Long tokenExpiration = 172800000l; // 登陆秘钥过期时间（默认2天）
    //private String tokenPrefix = "token_"; // 秘钥的前缀
    private String tokenSaltKey = "springboot-server";
    private String tokenFstAesSalt = "b7c7acf56a91f9b817471f44cfadee2d"; // 秘钥第一段加秘的key
    private String tokenSndAesSalt = "674aba59781fa1c97a9abc41418323b5"; // 秘钥第二段加密的key
}
