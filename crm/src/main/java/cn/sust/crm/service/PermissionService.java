package cn.sust.crm.service;

import cn.sust.crm.base.BaseService;
import cn.sust.crm.dao.PermissionMapper;
import cn.sust.crm.vo.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService extends BaseService<Permission, Integer> {
    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 通过用户ID查询用户拥有的角色 ==> 通过拥有的角色，查找拥有的权限
     * @return 资源权限码的集合
     */
    public List<String> queryPermissionByUserId(Integer userId) {
        return permissionMapper.queryPermissionByUserId(userId);
    }
}
