package cn.sust.crm.service;

import cn.sust.crm.base.BaseService;
import cn.sust.crm.dao.SaleChanceMapper;
import cn.sust.crm.enums.DevResult;
import cn.sust.crm.enums.StateStatus;
import cn.sust.crm.query.SaleChanceQuery;
import cn.sust.crm.utils.AssertUtil;
import cn.sust.crm.utils.PhoneUtil;
import cn.sust.crm.vo.SaleChance;
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
public class SaleChanceService extends BaseService<SaleChance, Integer> {
    @Autowired
    private SaleChanceMapper saleChanceMapper;

    /**
     * 删除营销机会（逻辑删除）
     */
    @Transactional
    public void deleteSaleChance(Integer[] ids) {
        //判断参数
        AssertUtil.isTrue(ids == null || ids.length < 1, "待删除记录不存在");
        //执行删除操作,判断受影响的行数
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids) != ids.length,
                "删除失败");
    }

    /**
     * 多条件分页查询 (返回的数据格式要满足layUI中表格要求的格式)
     */
    public Map<String, Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery) {

        Map<String, Object> map = new HashMap<>();
        //开启分页
        PageHelper.startPage(saleChanceQuery.getPage(), saleChanceQuery.getLimit());
        //得到对应的分页对象
        PageInfo<SaleChance> pageInfo =
                new PageInfo<>(saleChanceMapper.selectByParams(saleChanceQuery));
        //设置参数
        map.put("code", 0);
        map.put("msg", "success");
        map.put("count", pageInfo.getTotal());
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加营销机会
     */
    @Transactional
    public void addSaleChance(SaleChance saleChance) {
        //1、校验参数
        checkSaleChance(saleChance.getCustomerName(),
                saleChance.getLinkMan(),
                saleChance.getLinkPhone());
        //2、设置相关默认值
        //isValid字段 1-有效 0-无效
        saleChance.setIsValid(1);
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        //判断是否设置了指派人
        if (StringUtils.isBlank(saleChance.getAssignMan())) {
            //如果为空，则表示未设置指派人
            saleChance.setState(StateStatus.UNSTATE.getType());
            //没有指派时间
            saleChance.setAssignTime(null);
            //开发状态,未开发
            saleChance.setDevResult(DevResult.UNDEV.getStatus());

        } else {
            //设置了指派人
            //设置分配状态
            saleChance.setState(StateStatus.STATED.getType());
            //设置指派时间
            saleChance.setAssignTime(new Date());
            //设置开发状态
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        }
        //3、执行添加操作，判断影响行数
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance) != 1, "添加营销机会失败！");
    }

    /**
     * 更新营销机会
     */
    @Transactional
    public void updateSaleChance(SaleChance saleChance) {
        //1、参数检验
        AssertUtil.isTrue(saleChance.getId() == null, "待更新记录不存在");
        SaleChance tmp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(tmp == null, "待更新记录不存在");
        checkSaleChance(saleChance.getCustomerName(),
                saleChance.getLinkMan(),
                saleChance.getLinkPhone());
        //2、设置相关参数
        saleChance.setUpdateDate(new Date());
        if (StringUtils.isBlank(tmp.getAssignMan())) { //原始数据未设置
            if (!StringUtils.isBlank(saleChance.getAssignMan())) {
                //修改前为空，修改后有值
                saleChance.setAssignTime(new Date());
                //设置分配状态 1-已分配
                saleChance.setState(StateStatus.STATED.getType());
                //设置开发状态
                saleChance.setDevResult(DevResult.DEVING.getStatus());
            }
        } else {
            if (StringUtils.isBlank(tmp.getAssignMan())) {
                //修改前有值，修改后无值

                saleChance.setUpdateDate(new Date());
                saleChance.setState(StateStatus.UNSTATE.getType());
                saleChance.setDevResult(DevResult.UNDEV.getStatus());
            } else {
                //修改前有值，修改后也有值

                //判断是否是同一个指派人
                // 1、如果是则不需要操作
                // 2、不是则修改更新时间
                if (!tmp.getAssignMan().equals(saleChance.getAssignMan())) {

                    saleChance.setAssignTime(new Date());
                } else {
                    //设置指派时间为修改时间
                    saleChance.setAssignTime(tmp.getAssignTime());
                }
            }
        }
        //3、执行更新操作，判断影响行数
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) < 1,
                "营销机会修改失败");
    }

    private void checkSaleChance(String customerName, String linkMan, String linkPhone) {
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "客户名称不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan), "联系人不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone), "联系号码不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone), "联系号码格式不正确");

    }

    /**
     * 更新开发状态
     */
    @Transactional
    public void updateSaleChanceDevResult(Integer id, Integer devResult) {
        //判断id是否为空
        AssertUtil.isTrue(id == null, "待更新记录不存在");
        //通过id查询营销机会的数据
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        //判断对象是否为空
        AssertUtil.isTrue(saleChance == null, "待更新记录不存在");

        //设置对应的开发状态
        saleChance.setDevResult(devResult);
        //执行更新操作,判断受影响行数
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) != 1, "开发状态更新失败");
    }
}
