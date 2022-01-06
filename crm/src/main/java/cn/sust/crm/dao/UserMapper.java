package cn.sust.crm.dao;

import cn.sust.crm.base.BaseMapper;
import cn.sust.crm.vo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User, Integer> {

    /**
     * 通过用户名查询对象
     */
    User queryUserByName(String userName);

    /**
     * 查询所有销售人员
     */
    List<Map<String, Object>> queryAllSales();
}