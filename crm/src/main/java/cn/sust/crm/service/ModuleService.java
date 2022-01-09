package cn.sust.crm.service;

import cn.sust.crm.base.BaseService;
import cn.sust.crm.dao.ModuleMapper;
import cn.sust.crm.dao.PermissionMapper;
import cn.sust.crm.model.TreeModel;
import cn.sust.crm.utils.AssertUtil;
import cn.sust.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module, Integer> {
    @Autowired
    private ModuleMapper moduleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * 查询所有资源列表
     */
    public List<TreeModel> queryAllModules(Integer roleId) {
        //查询所有的资源列表
        List<TreeModel> treeModelList = moduleMapper.queryAllModules();
        //查询角色已经拥有哪些资源
        List<Integer> moduleIds = permissionMapper.queryModuleIdsByRoleId(roleId);

        //判断角色是否拥有该资源，有则默认选中
        if (moduleIds != null && moduleIds.size() > 0) {
            for (TreeModel treeModel : treeModelList) {
                if (moduleIds.contains(treeModel.getId())) {
                    treeModel.setChecked(true);
                }
            }
        }
        return treeModelList;
    }

    /**
     * 查询资源数据
     */
    public Map<String, Object> queryModuleList() {
        Map<String, Object> map = new HashMap<>();
        //查询资源列表
        List<Module> moduleList = moduleMapper.queryModuleList();
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", moduleList.size());
        map.put("data", moduleList);
        return map;
    }

    /**
     * 添加资源模块
     * 1、参数校验
     * 模块名称 moduleName        非空，同一层级下模块名唯一
     * 地址 url                  二级菜单非空，且不可重复
     * 父级菜单 parentId          一级菜单null，二级||三级菜单  非空且父级必须存在
     * 层级 grade                 非空 0|1|2
     * 权限码 optvalue            非空 不可重复
     * 2、设置参数默认值
     * 是否有效 1
     * 创建时间    当前时间
     * 修改时间    当前时间
     * 3、执行添加操作，判断受影响行数
     */
    @Transactional
    public void addModule(Module module) {
        //1、参数校验
        //层级 grade 非空 0|1|2
        Integer grade = module.getGrade();
        AssertUtil.isTrue(grade == null || !(grade == 0 || grade == 1 || grade == 2), "菜单层级不合法");
        //模块名非空
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()), "模块名称不能为空");
        //判断模块名称在同一层级是否唯一
        AssertUtil.isTrue(moduleMapper.queryModuleByGradeAndModuleName(grade, module.getModuleName()) != null,
                "该层级下模块名称已存在!");

        if (grade == 1) {
            //地址 url     二级菜单非空，且同一层级不可重复
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "url不能为空");
            AssertUtil.isTrue(moduleMapper.queryModuleByGradeAndUrl(grade, module.getUrl()) != null,
                    "url不可重复");

        }

        //父级菜单 parentId     一级菜单  -1
        //父级菜单 parentId     二级||三级菜单  非空且父级必须存在
        if (grade == 0) {
            module.setParentId(-1);
        } else {
            AssertUtil.isTrue(module.getParentId() == null, "父级菜单不能为空!");
            //父级ID作为主键查找资源
            AssertUtil.isTrue(moduleMapper.selectByPrimaryKey(module.getParentId()) == null,
                    "父级菜单不存在!");
        }

        //权限码 optvalue     非空
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()), "权限码不能为空!");
        //权限码 optvalue    不可重复
        AssertUtil.isTrue(moduleMapper.queryModuleByOptValue(module.getOptValue()) != null, "权限码已存在!");

        //2、设置默认值
        //      是否有效 1
        //     创建时间    当前时间
        //      修改时间    当前时间
        module.setIsValid((byte) 1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());

        //3、执行添加操作
        AssertUtil.isTrue(moduleMapper.insertSelective(module)!=1,"添加资源失败!");
    }

    /**
     * 修改资源模块
     * 1、参数校验
     *      id
     *          非空，数据存在
     *      层级
     *          非空，0|1|2
     *      模块名称 moduleName
     *              非空，同一层级下模块名唯一（不包括当前修改记录本身）
     *      地址 url
     *              二级菜单非空，且不可重复 （不包括当前修改记录本身）
     *      权限码 optvalue
     *              非空 不可重复（不包括当前修改记录本身）
     * 2、设置默认值
     *      修改时间     当前时间
     * 3、执行更新，判断受影响行数
     */
    @Transactional
    public void updateModule(Module module) {
        /*1、参数校验*/
        //id非空，数据存在
        AssertUtil.isTrue(module.getId() == null, "待修改记录不存在!");
        //通过id查资源
        Module temp = moduleMapper.selectByPrimaryKey(module.getId());
        AssertUtil.isTrue(temp == null, "待修改记录不存在!");
        // 层级   非空，0|1|2
        Integer grade = module.getGrade();
        AssertUtil.isTrue(grade==null||!(grade==0||grade==1||grade==2),
                "菜单层级不合法!");

        //模块名称 moduleName  非空，同一层级下模块名唯一（不包括当前修改记录本身）
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),
                "模块名不能为空!");
        //通过层级与模块名查询资源对象
        temp = moduleMapper.queryModuleByGradeAndModuleName(grade, module.getModuleName());
        if (temp != null) {
            AssertUtil.isTrue(!temp.getId().equals(module.getId()),"该层级下菜单名已存在！");
        }

        //地址 url   二级菜单非空，且不可重复 （不包括当前修改记录本身）
        if (grade == 1) {
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()), "菜单url不能为空");
            //通过层级与菜单url查询资源对象
            temp = moduleMapper.queryModuleByGradeAndUrl(grade, module.getUrl());
            if (temp != null) {
                //查到了资源对象，如果是当前修改的资源本身，允许进行修改操作，如果不是，则不允许
                AssertUtil.isTrue(!temp.getId().equals(module.getId()),
                        "该层级下菜单url已存在!");

            }
        }

        //权限码 optvalue 非空 不可重复（不包括当前修改记录本身）
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),
                "权限码不能为空!");
        //通过权限码查询资源对象
        temp = moduleMapper.queryModuleByOptValue(module.getOptValue());
        if (temp != null) {
            AssertUtil.isTrue(!temp.getId().equals(module.getId()),
                    "权限码已存在");
        }
        /*2、设置默认值*/
        module.setUpdateDate(new Date());

        /*3、执行更新，判断受影响行数*/
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module) != 1,
                "修改资源失败");
    }

    /**
     * 删除资源
     * 1、判断删除记录是否存在
     * 2、如果当前资源存在子记录，则不可删除
     * 3、删除资源时，将对应的权限表记录也删除（判断权限表是否有关联数据，有则删除）
     * 4、执行删除操作（逻辑删除），判断受影响行数
     */
    @Transactional
    public void deleteModule(Integer id) {
        //判断id是否为空
        AssertUtil.isTrue(id == null, "待删除记录不存在!");
        //通过id查询资源对象
        //1、判断删除记录是否存在
        Module temp = moduleMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(temp == null, "待删除记录不存在!");
        //2、如果当前资源存在子记录，则不可删除（id当作父id查询）
        Integer count = moduleMapper.countModuleByParentId(id);
        //如果存在子记录，则不可删除
        AssertUtil.isTrue(count > 0, "存在子记录不可删除!");

        //3、删除资源时，将对应的权限表记录也删除（判断权限表是否有关联数据，有则删除）
        count = permissionMapper.countPermissionByModuleId(id);
        if (count > 0) {
            //删除关联数据
            permissionMapper.deletePermissionByModuleId(id);
        }

        //4、执行删除操作（逻辑删除），判断受影响行数
        temp.setIsValid((byte) 0);
        temp.setUpdateDate(new Date());
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(temp)!=1,
                "删除资源失败!");
    }
}
