package cn.autumn.companyserver.repository;

import cn.autumn.companyserver.entity.Division;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author cf
 * Created in 15:30 2022/9/26
 */
@Repository
public interface DivisionRepository extends CrudRepository<Division, Long> {

    Division getDivisionByName(@Param("name") String name);

    @Query("select d.name from Division d")
    List<String> getDivisionNameAll();

    @Query("select d.name from Division d where d.name like :name")
    String getByName(@Param("name") String name);

    @Query(value = "select trd.* from t_region trd where trd.name in " +
            "(select trd1.name from t_region trd1 group by trd1.name having count(*)>1) " +
            "and trd.level < 4 and trd.name <> '市辖区' and trd.name <> '郊区' and trd.name <> '矿区' " +
            "and trd.name <> '城区' and trd.name <> '县' and trd.name <> '省直辖行政单位';", nativeQuery = true)
    List<Division> getSameDivision();

    @Modifying
    @Transactional
    @Query("DELETE FROM Division d where d.name = :name")
    void delRegionRepeat(@Param("name") String name);
}
