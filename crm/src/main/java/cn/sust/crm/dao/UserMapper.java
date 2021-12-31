package cn.sust.crm.dao;

import cn.sust.crm.base.BaseMapper;
import cn.sust.crm.vo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User, Integer> {

    //通过用户名查询对象
    User queryUserByName(String userName);
}