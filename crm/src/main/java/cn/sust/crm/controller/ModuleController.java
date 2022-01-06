package cn.sust.crm.controller;

import cn.sust.crm.base.BaseController;
import cn.sust.crm.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ModuleController extends BaseController {
    @Autowired
    private ModuleService moduleService;

}
