package cn.sust.crm.dao;

import cn.sust.crm.base.BaseMapper;
import cn.sust.crm.vo.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {
    /**
     * 根据用户id查询用户分配了多个角色
     */
    Integer countUserRoleByUserId(Integer userId);

    /**
     * 根据用户id删除用户角色记录
     */
    Integer deleteUserRoleByUserId(Integer userId);
}