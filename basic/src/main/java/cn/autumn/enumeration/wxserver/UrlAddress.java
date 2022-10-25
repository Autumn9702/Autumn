package cn.autumn.enumeration.wxserver;

/**
 * @author: autumn
 * created in 2022/9/10 Class UrlAddress
 */
public enum UrlAddress {

    /**
     * Query url
     */

    HOLIDAYS("https://api.apihubs.cn/holiday/get?cn=1&date="),

    ;

    private final String address;

    public String getAddress() {
        return this.address;
    }

    UrlAddress(String address) {
        this.address = address;
    }
}
