package cn.sust.crm.query;

import cn.sust.crm.base.BaseQuery;

public class CustomerQuery extends BaseQuery {
    private String customerName; //客户名
    private String customerNo; //客户编号
    private String level;  //客户级别

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
