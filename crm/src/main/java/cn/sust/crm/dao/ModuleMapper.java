package cn.sust.crm.dao;

import cn.sust.crm.base.BaseMapper;
import cn.sust.crm.model.TreeModel;
import cn.sust.crm.vo.Module;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ModuleMapper extends BaseMapper<Module, Integer> {

    //查询所有的资源列表
    List<TreeModel> queryAllModules();

    //查询所有的资源数据
    List<Module> queryModuleList();

    //通过层级与模块名 查询资源对象
    Module queryModuleByGradeAndModuleName(@Param("grade") Integer grade, @Param("moduleName") String moduleName);

    //通过层级与url查询资源对象
    Module queryModuleByGradeAndUrl(@Param("grade")Integer grade,@Param("url") String url);

    //通过权限码查询资源对象
    Module queryModuleByOptValue(String optValue);

    //统计资源的子记录个数
    Integer countModuleByParentId(Integer id);
}