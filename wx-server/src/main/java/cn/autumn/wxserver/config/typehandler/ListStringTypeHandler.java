package cn.autumn.wxserver.config.typehandler;

import cn.autumn.util.Util;
import lombok.SneakyThrows;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * author: autumn
 * created in 2022/9/8 Class ListStringTypeHandler
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(List.class)
public class ListStringTypeHandler implements TypeHandler<List<String>> {

    @Override
    public void setParameter(PreparedStatement ps, int i, List parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, Util.gson().toJson(parameter));
    }

    @SneakyThrows
    @Override
    public List getResult(ResultSet rs, String columnName) throws SQLException {
        return Util.gson().fromJson(rs.getString(columnName), List.class);
    }

    @SneakyThrows
    @Override
    public List getResult(ResultSet rs, int columnIndex) throws SQLException {
        return Util.gson().fromJson(rs.getString(columnIndex), List.class);
    }

    @SneakyThrows
    @Override
    public List getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return Util.gson().fromJson(cs.getString(columnIndex), List.class);
    }
}