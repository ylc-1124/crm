package cn.sust.crm.controller;

import cn.sust.crm.annotation.RequiredPermission;
import cn.sust.crm.base.BaseController;
import cn.sust.crm.base.ResultInfo;
import cn.sust.crm.enums.StateStatus;
import cn.sust.crm.query.SaleChanceQuery;
import cn.sust.crm.service.SaleChanceService;
import cn.sust.crm.utils.CookieUtil;
import cn.sust.crm.utils.LoginUserUtil;
import cn.sust.crm.vo.SaleChance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {
    @Autowired
    private SaleChanceService saleChanceService;

    /**
     * 营销机会分页多条件查询
     *
     * @Param flag 不为空且为1，表示查询的客户开发计划，否则查询营销机会数据
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery,
                                                       Integer flag,
                                                       HttpServletRequest request) {
        if (flag != null && flag == 1) {
            //查询客户开发机会
            //设置分配状态
            saleChanceQuery.setState(StateStatus.STATED.getType());
            //设置指派人（当前用户id）
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(userId);
        }
        return saleChanceService.querySaleChanceByParams(saleChanceQuery);
    }

    /**
     * 删除营销机会 101003
     */
    @PostMapping("delete")
    @ResponseBody
    @RequiredPermission(code = "101003")
    public ResultInfo deleteSaleChance(Integer[] ids) {
        //调用service层的删除方法
        saleChanceService.deleteSaleChance(ids);
        return success("营销机会数据删除成功");
    }

    /**
     * 添加营销机会 101002
     */
    @PostMapping("add")
    @ResponseBody
    @RequiredPermission(code = "101002")
    public ResultInfo addSaleChance(SaleChance saleChance,
                                    HttpServletRequest request) {
        //从cookie中获取当前登录的用户名
        String userName = CookieUtil.getCookieValue(request, "userName");
        //设置用户名到当前营销对象中
        saleChance.setCreateMan(userName);
        //调用service层的添加方法
        saleChanceService.addSaleChance(saleChance);
        return success("营销机会数据添加成功");
    }

    /**
     * 更新营销机会
     */
    @PostMapping("update")
    @ResponseBody
    @RequiredPermission(code = "101004")
    public ResultInfo updateSaleChance(SaleChance saleChance) {
        //调用service层的添加方法
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据修改成功!");
    }

    /**
     * 进入添加或者修改营销机会数据页面
     */
    @RequestMapping("toSaleChancePage")
    public String toSaleChancePage(Integer saleChanceId, Model model) {
        //判断saleChanceId是否为空
        if (saleChanceId != null) {
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(saleChanceId);
            //数据放入请求域
            model.addAttribute("saleChance", saleChance);
        }
        return "saleChance/add_update";
    }

    /**
     * 进入营销机会管理的页面 1010
     */
    @RequestMapping("index")
    @RequiredPermission(code = "1010")
    public String index() {
        return "saleChance/sale_chance";
    }

    /**
     * 更新营销计划的开发状态
     */
    @PostMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id, Integer devResult) {
        saleChanceService.updateSaleChanceDevResult(id, devResult);
        return success("开发状态更新成功");
    }
}
