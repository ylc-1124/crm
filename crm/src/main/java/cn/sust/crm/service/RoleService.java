package cn.sust.crm.service;

import cn.sust.crm.base.BaseService;
import cn.sust.crm.dao.ModuleMapper;
import cn.sust.crm.dao.PermissionMapper;
import cn.sust.crm.dao.RoleMapper;
import cn.sust.crm.utils.AssertUtil;
import cn.sust.crm.vo.Permission;
import cn.sust.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleService extends BaseService<Role, Integer> {
    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private ModuleMapper moduleMapper;

    /**
     * 查询所有的角色列表
     */
    public List<Map<String, Object>> queryAllRoles(Integer userId) {
        return roleMapper.queryAllRoles(userId);
    }

    /**
     * 添加角色
     * 1、参数校验
     * 角色名非空且唯一，角色备注可选填
     * 2、设置参数的默认值
     * 是否有效
     * 创建时间
     * 修改时间
     * 3、执行添加操作，判断受影响行数
     */
    @Transactional
    public void addRole(Role role) {
        //1、参数校验
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "角色名不能为空");
        //通过角色名查询记录
        Role temp = roleMapper.selectByRoleName(role.getRoleName());
        //判断角色记录是否存在
        AssertUtil.isTrue(temp != null, "角色已存在!");
        //2、设置参数默认值
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        //3、执行添加操作，判断受影响行数
        AssertUtil.isTrue(roleMapper.insertSelective(role) < 1, "角色添加失败!");
    }

    /**
     * 修改角色
     * 1、参数校验
     * 角色 id     非空，且数据存在
     * 角色名称     非空，名称唯一
     * 2、设置参数的默认值
     * 修改时间
     * 3、执行更新操作，判断受影响行数
     */
    @Transactional
    public void updateRole(Role role) {
        //1、参数校验
        AssertUtil.isTrue(role.getId() == null, "待更新记录不存在!");
        Role temp = roleMapper.selectByPrimaryKey(role.getId());
        AssertUtil.isTrue(temp == null, "待更新记录不存在!");
        //角色名称  非空，名称唯一
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()), "角色名称不能为空!");
        //通过角色名查找角色
        temp = roleMapper.selectByRoleName(role.getRoleName());
        //判断角色是否存在
        // a）如果不存在，可以使用
        // b）如果存在且此角色 id不是正在更新的这个角色id，则不能使用
        AssertUtil.isTrue(temp != null && !temp.getId().equals(role.getId()),
                "角色名称已存在!");
        //2、设置默认值
        role.setUpdateDate(new Date());

        //3、修改操作
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role) < 1, "修改角色失败!");
    }

    /**
     * 删除角色
     * 1、参数校验
     *      角色 id 非空，数据存在
     * 2、设置相关参数的默认值
     *      是否有效  0（删除）
     *      更新时间  当前时间
     * 3、执行更新操作，判断受影响行数
     */
    @Transactional
    public void deleteRole(Integer roleId) {
        //1、参数校验
        AssertUtil.isTrue(roleId == null, "待删除记录不存在!");
        //通过角色id查询角色记录
        Role role = roleMapper.selectByPrimaryKey(roleId);
        //判断角色是否存在
        AssertUtil.isTrue(role == null, "待删除记录不存在!");
        //2、设置相关参数的默认值
        role.setIsValid(0);
        role.setUpdateDate(new Date());
        //3、执行更新操作，判断受影响行数
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role) < 1,
                "角色删除失败!");
    }

    /**
     * 添加授权：将对应的角色id和资源id添加到对应的权限表（角色表和模块表的那张中间表）中
     *          直接添加不合适，会出现重复的权限数据
     *          建议的实现方式：先将已经有的权限删除，再将需要添加的权限记录批量添加
     *                      1、通过角色id查找当前角色有的权限
     *                      2、如果权限记录存在，则删除对应的角色拥有的权限
     *                      3、如果有权限记录，则批量添加权限记录
     *
     */
    @Transactional
    public void addGrant(Integer roleId, Integer[] mIds) {
        //1、通过角色id查找当前角色有的权限
        Integer count = permissionMapper.countPermissionByRoleId(roleId);
        if (count > 0) {
            //删除该角色的所有权限记录
            permissionMapper.deletePermissionByRoleId(roleId);
        }
        if (mIds != null && mIds.length > 0) {
            //批量添加权限记录
            List<Permission> permissionList = new ArrayList<>();
            for (Integer mId : mIds) {
                Permission permission = new Permission();
                permission.setRoleId(roleId);
                permission.setModuleId(mId);
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mId).getOptValue());
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                //放入集合中保存，下面批量添加
                permissionList.add(permission);
            }
            //执行批量添加权限记录，判断受影响行数
            AssertUtil.isTrue(permissionMapper.insertBatch(permissionList) != permissionList.size(), "角色授权失败");
        }
    }
}
