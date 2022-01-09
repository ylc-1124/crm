package cn.sust.crm.controller;

import cn.sust.crm.base.BaseController;
import cn.sust.crm.base.ResultInfo;
import cn.sust.crm.query.CustomerQuery;
import cn.sust.crm.service.CustomerService;
import cn.sust.crm.vo.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("customer")
public class CustomerController extends BaseController {
    @Autowired
    private CustomerService customerService;

    /**
     * 分页条件查询客户列表
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryCustomerByParams(CustomerQuery customerQuery) {
        return customerService.queryCustomerByParams(customerQuery);
    }

    /**
     * 进入客户信息管理页面
     */
    @RequestMapping("index")
    public String index() {
        return "customer/customer";
    }

    /**
     * 添加客户信息
     */
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addCustomer(Customer customer) {
        customerService.addCustomer(customer);
        return success("添加客户信息成功");
    }

    /**
     * 修改客户信息
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateCustomer(Customer customer) {
        customerService.updateCustomer(customer);
        return success("修改客户信息成功");
    }

    /**
     * 删除客户信息
     */
    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteCustomer(Integer id) {
        customerService.deleteCustomer(id);
        return success("删除客户信息成功");
    }

    @RequestMapping("toAddOrUpdateCustomerPage")
    public String toAddOrUpdateCustomerPage(Integer id, Model model) {
        if (id != null) {
            Customer customer = customerService.selectByPrimaryKey(id);
            model.addAttribute("customer", customer);
        }
        return "customer/add_update";
    }
}
