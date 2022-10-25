package cn.autumn.companyserver.repository;

import cn.autumn.companyserver.entity.Error;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author cf
 * Created in 16:37 2022/9/27
 */
@Repository
public interface ErrorRepository extends CrudRepository<Error, Long> {

    Error getErrorByName(@Param("name") String name);

    @Query(value = "select e.* from t_error e", nativeQuery = true)
    List<Error> getErrorAll();

    @Query(value = "select e.name from t_error e", nativeQuery = true)
    List<String> getErrAllStr();

    @Query(value = "select e.name from t_error e where e.name like :name", nativeQuery = true)
    String getByName(@Param("name") String name);

    @Modifying
    @Transactional
    @Query(value = "delete from t_error where name in :list", nativeQuery = true)
    void deleteErrorByName(@Param("list") List<String> list);

    @Modifying
    @Transactional
    void deleteErrorByName(@Param("name") String name);
}
