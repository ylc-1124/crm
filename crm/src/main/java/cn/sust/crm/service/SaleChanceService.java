package cn.sust.crm.service;

import cn.sust.crm.base.BaseService;
import cn.sust.crm.dao.SaleChanceMapper;
import cn.sust.crm.query.SaleChanceQuery;
import cn.sust.crm.vo.SaleChance;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance, Integer> {
    @Autowired
    private SaleChanceMapper saleChanceMapper;

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
        
    }
}
