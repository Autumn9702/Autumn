package cn.autumn.companyserver.repository;

import cn.autumn.companyserver.entity.Region;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author cf
 * Created in 14:57 2022/9/27
 */
@Repository
public interface RegionRepository extends CrudRepository<Region, Long> {

    @Query(value = "select r.* from t_region r", nativeQuery = true)
    List<Region> getRegionAll();

    @Query(value = "select r.name from t_region r", nativeQuery = true)
    List<String> getRegionByLevel();

    @Query(value = "select trd.* from t_region trd where trd.name in " +
            "(select trd1.name from t_region trd1 group by trd1.name having count(*)>1) " +
            "and trd.level < 4 and trd.name <> '市辖区' and trd.name <> '郊区' and trd.name <> '矿区' " +
            "and trd.name <> '城区' and trd.name <> '县' and trd.name <> '省直辖行政单位';", nativeQuery = true)
    List<Region> getRegionRepeat();

    @Query("SELECT r FROM Region r where r.name = :name and r.level < 4")
    List<Region> getRegionRepeatByName(@Param("name") String name);
}
