package cn.sust.crm.controller;

import cn.sust.crm.base.BaseController;
import cn.sust.crm.base.ResultInfo;
import cn.sust.crm.query.RoleQuery;
import cn.sust.crm.service.RoleService;
import cn.sust.crm.vo.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;

    /**
     * 查询所有的角色列表
     */
    @RequestMapping("queryAllRoles")
    @ResponseBody
    public List<Map<String, Object>> queryAllRoles(Integer userId) {
        return roleService.queryAllRoles(userId);
    }

    /**
     * 分页条件查询
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> selectByParams(RoleQuery roleQuery) {
        return roleService.queryByParamsForTable(roleQuery);
    }

    /**
     * 进入角色管理页面
     */
    @RequestMapping("index")
    public String index() {
        return "role/role";
    }

    /**
     * 添加角色
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addRole(Role role) {
        roleService.addRole(role);
        return success("角色添加成功");
    }

    /**
     * 去添加/修改的页面
     */
    @RequestMapping("toAddOrUpdateRolePage")
    public String toAddOrUpdateRolePage(Integer roleId, Model model) {
        if (roleId != null) {
            //表示是修改操作
            //通过角色id查找角色信息，存到请求域中
            Role role = roleService.selectByPrimaryKey(roleId);
            model.addAttribute("role", role);
        }
        return "role/add_update";
    }

    /**
     * 修改角色
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role) {
        roleService.updateRole(role);
        return success("角色修改成功");
    }

    /**
     * 删除角色
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer roleId) {
        roleService.deleteRole(roleId);
        return success("角色删除成功");
    }

    /**
     * 角色授权
     */
    @PostMapping("addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer roleId, Integer[] mIds) {
        roleService.addGrant(roleId, mIds);
        return success("角色授权成功!");
    }



}
