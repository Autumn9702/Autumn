package cn.autumn.entity.mybatis;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * author: autumn
 * created in 2022/9/8 Class DataBase
 */
@Data
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DataBase<T extends Serializable> implements Serializable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long createTime;
}
