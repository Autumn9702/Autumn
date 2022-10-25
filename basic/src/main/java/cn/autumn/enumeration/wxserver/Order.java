package cn.autumn.enumeration.wxserver;

/**
 * @author: autumn
 * created in 2022/9/8 Class Order
 */
public enum Order {

    /**
     * The command
     */

    NAME("姓名"),
    BIRTHDAY("生日"),
    ADDRESS("地址"),
    OPEN_NOTIFY("开启通知"),
    LOOK_NOTIFY("查看通知"),
    LOOK_USER_DATA("查看个人信息"),
    CLOSE_NOTIFY("关闭通知"),
    CLOSE_ALL_NOTIFY("关闭所有通知"),


    MORNING("上午"),
    NOON("中午"),
    AFTERNOON("下午"),
    ;

    private String name;

    Order(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
