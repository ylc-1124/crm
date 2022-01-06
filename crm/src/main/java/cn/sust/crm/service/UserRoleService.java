package cn.sust.crm.service;

import cn.sust.crm.base.BaseService;
import cn.sust.crm.dao.UserRoleMapper;
import cn.sust.crm.vo.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService extends BaseService<UserRole, Integer> {
    @Autowired
    private UserRoleMapper userRoleMapper;

}
