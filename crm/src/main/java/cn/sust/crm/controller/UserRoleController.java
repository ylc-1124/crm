package cn.sust.crm.controller;

import cn.sust.crm.base.BaseController;
import cn.sust.crm.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserRoleController extends BaseController {
    @Autowired
    private UserRoleService userRoleService;

}
