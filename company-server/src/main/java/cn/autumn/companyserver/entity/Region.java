package cn.autumn.companyserver.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author cf
 * Created in 14:55 2022/9/27
 */
@Data
@Entity
@Table(name = "t_region")
public final class Region{

    @Id
    private Long id;

    private String name;

    private Long pid;

    private Integer level;

    public Region() {}

    public Region(Long id, String name, Long pid, Integer level) {
        this.id = id;
        this.name = name;
        this.pid = pid;
        this.level = level;
    }
}
