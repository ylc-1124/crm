package cn.sust.crm.dao;

import cn.sust.crm.base.BaseMapper;
import cn.sust.crm.vo.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleMapper extends BaseMapper<Role, Integer> {
    //查询所有角色列表 (只需要id和roleName)
    List<Map<String,Object>> queryAllRoles(Integer userId);


    Role selectByRoleName(String roleName);
}