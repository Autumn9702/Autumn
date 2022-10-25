package cn.autumn.entity.business.wxserver;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cf
 * Created in 9:44 2022/8/24
 */
@Data
public final class TemplateMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String title;

    private String word1;
    private String word2;
    private String word3;
    private String word4;
    private String word5;
    private String word6;
    private String word7;
    private String word8;

    private String acquaintance;
    private String formalConnect;
    private String firstConnect;

    private String remark;

    private String info;

    private String flag;

    private String birth;

    private String jumpUrl;

    private String encoding;

    private int status;

}
