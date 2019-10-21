package demo.user.bean.enums;

/**
 * 一些常用的特殊用户组
 */
public enum HBUserGroupEnum {
    GROUP_NORMAL(1, "normal", "普通用户组"),
    GROUP_SELL(2, "admin", "管理员组");
    private Integer index;
    private String name;
    private String cname;// 中文名

    private HBUserGroupEnum(Integer index,
                            String name,
                            String cname) {
        this.index = index;
        this.name = name;
        this.cname = cname;
    }

    public Integer getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getCname() {
        return cname;
    }

    public static HBUserGroupEnum valueOf(int index) {
        for (HBUserGroupEnum serviceEnum : values()) {
            if (serviceEnum.getIndex() == index) {
                return serviceEnum;
            }
        }
        return null;
    }
}
