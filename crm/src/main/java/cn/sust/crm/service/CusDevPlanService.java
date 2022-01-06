package cn.sust.crm.service;

import cn.sust.crm.base.BaseService;
import cn.sust.crm.dao.CusDevPlanMapper;
import cn.sust.crm.dao.SaleChanceMapper;
import cn.sust.crm.query.CusDevPlanQuery;
import cn.sust.crm.utils.AssertUtil;
import cn.sust.crm.vo.CusDevPlan;
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
public class CusDevPlanService extends BaseService<CusDevPlan, Integer> {
    @Autowired
    private CusDevPlanMapper cusDevPlanMapper;

    @Autowired
    private SaleChanceMapper saleChanceMapper;

    /**
     * 多条件分页查询客户开发计划
     */
    public Map<String, Object> queryCusDevPlanByParams(CusDevPlanQuery cusDevPlanQuery) {

        Map<String, Object> map = new HashMap<>();
        //开启分页
        PageHelper.startPage(cusDevPlanQuery.getPage(), cusDevPlanQuery.getLimit());
        //得到对应的分页对象
        PageInfo<CusDevPlan> pageInfo =
                new PageInfo<>(cusDevPlanMapper.selectByParams(cusDevPlanQuery));
        //设置参数
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加客户开发项
     */
    @Transactional
    public void addCusDevPlan(CusDevPlan cusDevPlan) {
        //参数校验
        checkCusDevPlanParams(cusDevPlan);
        //设置默认值
        cusDevPlan.setIsValid(1);
        cusDevPlan.setCreateDate(new Date());
        cusDevPlan.setUpdateDate(new Date());
        //执行更新操作，判断受影响行数
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan) != 1,
                "计划项数据添加失败");
    }

    /**
     * 更新客户开发计划
     */
    @Transactional
    public void updateCusDevPlan(CusDevPlan cusDevPlan) {
        //参数校验
        AssertUtil.isTrue(cusDevPlan.getId() == null
                || cusDevPlanMapper.selectByPrimaryKey(cusDevPlan.getId()) == null, "数据异常,请重试!");
        checkCusDevPlanParams(cusDevPlan);
        //设置默认值
        cusDevPlan.setUpdateDate(new Date());
        //执行更新操作
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan) != 1, "计划项更新失败!");
    }

    private void checkCusDevPlanParams(CusDevPlan cusDevPlan) {
        Integer sId = cusDevPlan.getSaleChanceId();
        AssertUtil.isTrue(sId == null
                        || saleChanceMapper.selectByPrimaryKey(sId) == null,
                "数据异常,重试");
        AssertUtil.isTrue(StringUtils.isBlank(cusDevPlan.getPlanItem()),
                "计划项内容不能为空");
        AssertUtil.isTrue(cusDevPlan.getPlanDate() == null,
                "计划时间不能为空");
    }

    /**
     * 删除客户开发计划（逻辑删除）
     */
    @Transactional
    public void deleteCusDevPlan(Integer id) {
        AssertUtil.isTrue(id == null, "带删除记录不存在!");
        //通过id查询计划项对象
        CusDevPlan cusDevPlan = cusDevPlanMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(cusDevPlan == null, "带删除记录不存在!");
        //设置记录无效
        cusDevPlan.setIsValid(0);
        cusDevPlan.setUpdateDate(new Date());
        //执行更新操作
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan) != 1, "计划项数据删除失败");
    }
}
