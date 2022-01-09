package cn.sust.crm.controller;

import cn.sust.crm.base.BaseController;
import cn.sust.crm.base.ResultInfo;
import cn.sust.crm.model.TreeModel;
import cn.sust.crm.service.ModuleService;
import cn.sust.crm.vo.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {
    @Autowired
    private ModuleService moduleService;

    /**
     * 查询所有模块
     */
    @RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeModel> queryAllModules(Integer roleId) {
        return moduleService.queryAllModules(roleId);
    }

    /**
     * 打开授权页面
     */
    @RequestMapping("toAddGrantPage")
    public String toAddGrantPage(Integer roleId, Model model) {
        model.addAttribute("roleId", roleId);
        return "role/grant";
    }

    /**
     * 查询资源列表
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryModuleList() {
        return moduleService.queryModuleList();
    }

    /**
     * 进入资源管理页面
     */
    @RequestMapping("index")
    public String index() {
        return "module/module";
    }

    /**
     * 添加资源
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addModule(Module module) {
        moduleService.addModule(module);
        return success("添加资源成功");
    }

    /**
     * 前往添加资源的页面
     *
     * @param grade    层级
     * @param parentId 父ID
     */
    @RequestMapping("toAddModulePage")
    public String toAddModulePage(Integer grade, Integer parentId, Model model) {
        //将数据放入请求域
        model.addAttribute("grade", grade);
        model.addAttribute("parentId", parentId);
        return "module/add";
    }

    /**
     * 修改资源
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateModule(Module module) {
        moduleService.updateModule(module);
        return success("修改资源成功!");
    }

    /**
     * 进入修改资源页面
     */
    @RequestMapping("toUpdateModulePage")
    public String toUpdateModulePage(Integer id, Model model) {
        //将要修改的对象设置到请求域
        model.addAttribute("module", moduleService.selectByPrimaryKey(id));
        return "module/update";
    }

    /**
     * 删除资源
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteModule(Integer id) {
        moduleService.deleteModule(id);
        return success("删除资源成功");
    }
}
