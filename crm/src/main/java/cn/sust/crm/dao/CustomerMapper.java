package cn.sust.crm.dao;

import cn.sust.crm.base.BaseMapper;
import cn.sust.crm.vo.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer,Integer> {

    //通过客户名称查询客户对象
    Customer queryCustomerByName(String name);
}