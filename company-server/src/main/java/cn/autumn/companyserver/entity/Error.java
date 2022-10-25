package cn.autumn.companyserver.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author cf
 * Created in 16:36 2022/9/27
 */
@Data
@Entity
@Table(name = "t_error")
public final class Error extends EntityBase{

    private String name;

    public Error() {}

    public Error(String name) {
        this.name = name;
    }
}
