package cn.autumn.companyserver.entity;


import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * @author cf
 * Created in 15:30 2022/9/26
 */
@Data
@Entity
@Table(name = "t_region_data")
public final class Division extends EntityBase {

    private String name;

    private String coordType;

    @Type(type = "jsonb")
    @Column(name = "parameter", columnDefinition = "jsonb")
    private Args parameter;
}
