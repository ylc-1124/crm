package cn.sust.crm.dao;

import cn.sust.crm.base.BaseMapper;
import cn.sust.crm.vo.Module;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ModuleMapper extends BaseMapper<Module, Integer> {

    //查询所有的资源列表


}