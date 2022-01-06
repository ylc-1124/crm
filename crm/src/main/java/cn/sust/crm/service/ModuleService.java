package cn.sust.crm.service;

import cn.sust.crm.base.BaseService;
import cn.sust.crm.dao.ModuleMapper;
import cn.sust.crm.vo.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModuleService extends BaseService<Module, Integer> {
    @Autowired
    private ModuleMapper moduleMapper;
}
