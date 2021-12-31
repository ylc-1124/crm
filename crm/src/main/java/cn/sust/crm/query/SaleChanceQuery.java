package cn.sust.crm.query;

import cn.sust.crm.base.BaseQuery;

public class SaleChanceQuery extends BaseQuery {
    //分页参数

    //条件查询
    private String customerName; //客户名
    private String createMan;   //创建人
    private Integer state;  //分配状态    0=未分配  1=已分配

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCreateMan() {
        return createMan;
    }

    public void setCreateMan(String createMan) {
        this.createMan = createMan;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
