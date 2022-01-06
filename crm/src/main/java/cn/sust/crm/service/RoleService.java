package cn.sust.crm.service;

import cn.sust.crm.base.BaseService;
import cn.sust.crm.dao.RoleMapper;
import cn.sust.crm.utils.AssertUtil;
import cn.sust.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleService extends BaseService<Role, Integer> {
    @Autowired
    private RoleMapper roleMapper;

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
}
