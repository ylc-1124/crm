package cn.sust.crm.service;

import cn.sust.crm.base.BaseService;
import cn.sust.crm.dao.CustomerMapper;
import cn.sust.crm.query.CustomerQuery;
import cn.sust.crm.utils.AssertUtil;
import cn.sust.crm.utils.PhoneUtil;
import cn.sust.crm.vo.Customer;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerService extends BaseService<Customer, Integer> {
    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 多条件分页查询客户信息
     */
    public Map<String, Object> queryCustomerByParams(CustomerQuery customerQuery) {

        Map<String, Object> map = new HashMap<>();
        //开启分页
        PageHelper.startPage(customerQuery.getPage(), customerQuery.getLimit());
        //得到对应的分页对象
        PageInfo<Customer> pageInfo =
                new PageInfo<>(customerMapper.selectByParams(customerQuery));
        //设置参数
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加客户
     * 1、参数校验
     *      客户名称       非空，唯一
     *      法人代表        非空
     *      手机号码        非空，格式正确
     * 2、设置默认值
     *      是否有效         1
     *      创建时间        当前时间
     *      修改时间        当前时间
     *      客户编号        系统生成，唯一（UUID|时间戳|年月日时分秒|雪花算法）
     *                      格式：KH+时间戳
     *      流失状态         0=正常 1=流失
     * 3、执行添加操作，判断受影响行数
     */
    @Transactional
    public void addCustomer(Customer customer) {
        /*1、参数校验*/
        checkCustomerParams(customer.getName(), customer.getFr(), customer.getPhone());
        //判断唯一性
        Customer temp = customerMapper.queryCustomerByName(customer.getName());
        AssertUtil.isTrue(temp != null, "客户名称已存在!");

        /*2、设置默认值*/
        customer.setIsValid(1);
        customer.setState(0);
        customer.setCreateDate(new Date());
        customer.setUpdateDate(new Date());
        String khno = "KH" + System.currentTimeMillis();
        customer.setKhno(khno);

        /*3、执行添加操作，判断受影响行数*/
        AssertUtil.isTrue(customerMapper.insertSelective(customer) != 1, "添加客户信息失败!");
    }

    /**
     * 参数非空校验
     */
    private void checkCustomerParams(String name, String fr, String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(name), "客户名称不能为空!");
        AssertUtil.isTrue(StringUtils.isBlank(fr), "法人代表不能为空!");
        AssertUtil.isTrue(StringUtils.isBlank(phone), "手机号码不能为空!");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "手机号码格式不正确!");
    }

    /**
     * 更新客户信息
     * 1、参数校验
     *      客户ID        非空，数据存在
     *      客户名称       非空，唯一
     *      法人代表        非空
     *      手机号码        非空，格式正确
     * 2、设置默认值
     *      修改时间        当前时间
     * 3、执行添加操作，判断受影响行数
     */
    @Transactional
    public void updateCustomer(Customer customer) {
//        1、参数校验
        AssertUtil.isTrue(customer.getId() == null, "待更新记录不存在!");
        //通过客户ID查询客户记录
        Customer temp = customerMapper.selectByPrimaryKey(customer.getId());
        AssertUtil.isTrue(temp == null, "待更新记录不存在!");
        checkCustomerParams(customer.getName(), customer.getFr(), customer.getPhone());
        //通过客户名查客户记录
        temp = customerMapper.queryCustomerByName(customer.getName());
        if (temp != null) {
            AssertUtil.isTrue(!temp.getId().equals(customer.getId()), "客户名称已存在!");
        }
//        2、设置默认值
        customer.setUpdateDate(new Date());
//        3、执行添加操作，判断受影响行数
        AssertUtil.isTrue(customerMapper.updateByPrimaryKeySelective(customer) != 1,
                "更新客户信息失败！");
    }

    /**
     * 删除客户信息
     * 1、参数校验
     *      id      非空，数据存在
     * 2、设置参数默认值
     *         is_Valid   0
     *          更新时间     当前时间
     * 3、执行删除（更新）操作，判断受影响行数
     */
    @Transactional
    public void deleteCustomer(Integer id) {
        AssertUtil.isTrue(id == null, "待删除数据不存在!");
        Customer customer = customerMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(customer == null, "待删除数据不存在!");
        //设置默认值
        customer.setIsValid(0);
        customer.setUpdateDate(new Date());

        AssertUtil.isTrue(customerMapper.updateByPrimaryKeySelective(customer) != 1,
                "删除客户信息失败!");
    }
}
