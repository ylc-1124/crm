package cn.sust.crm.service;

import cn.sust.crm.base.BaseService;
import cn.sust.crm.dao.UserMapper;
import cn.sust.crm.dao.UserRoleMapper;
import cn.sust.crm.model.UserModel;
import cn.sust.crm.utils.AssertUtil;
import cn.sust.crm.utils.Md5Util;
import cn.sust.crm.utils.PhoneUtil;
import cn.sust.crm.utils.UserIDBase64;
import cn.sust.crm.vo.User;
import cn.sust.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User, Integer> {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    /**
     * 查询所有销售人员
     */
    public List<Map<String, Object>> queryAllSales() {
        return userMapper.queryAllSales();
    }

    /**
     * 修改密码
     */
    @Transactional
    public void updatePassword(Integer userId, String oldPwd,
                               String newPwd, String repeatPwd) {
        User user = userMapper.selectByPrimaryKey(userId);
        //判断用户是否存在
        AssertUtil.isTrue(user == null, "待更新记录不存在");
        //参数校验
        checkPasswordParams(user, oldPwd, newPwd, repeatPwd);
        //设置新密码
        user.setUserPwd(Md5Util.encode(newPwd));
        //执行更新操作
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1,
                "修改密码失败");
    }

    /**
     * 修改密码的参数校验
     */
    private void checkPasswordParams(User user, String oldPwd,
                                     String newPwd, String repeatPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd), "原始密码不能为空");
        //判断输入的原始密码是否正确
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPwd)),//加密后比较
                "原始密码不正确");
        //判断新密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(newPwd), "新密码不能为空");
        //判断新密码和旧密码是否相同，不允许相同
        AssertUtil.isTrue(oldPwd.equals(newPwd), "新密码和旧密码不能相同");
        //判断确认密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(repeatPwd), "确认密码不能为空");
        //判断确认密码是否与新密码相同
        AssertUtil.isTrue(!newPwd.equals(repeatPwd), "确认密码与新密码不一致");
    }

    /**
     * 用户登录操作
     */
    public UserModel userLogin(String userName, String userPwd) {
        //1、参数校验
        checkLoginParams(userName, userPwd);
        //2、数据库查询
        User user = userMapper.queryUserByName(userName);
        AssertUtil.isTrue(user == null, "用户不存在!");
        //3、比较密码：
        checkUserPwd(userPwd, user.getUserPwd());
        //4、登录成功,返回构建用户对象
        return buildUserModel(user);

    }

    private UserModel buildUserModel(User user) {
        UserModel userModel = new UserModel();
        //设置加密的userId
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    /**
     * 密码判断：
     * 将浏览器传入的密码先加密，再和数据库中密码进行比较
     */
    private void checkUserPwd(String userPwd, String pwd) {
        //浏览器传入的密码进行加密
        userPwd = Md5Util.encode(userPwd);
        //判断密码是否相等
        AssertUtil.isTrue(!pwd.equals(userPwd), "密码错误!");
    }

    /**
     * 参数校验
     */
    private void checkLoginParams(String userName, String userPwd) {
        //验证用户姓名
        AssertUtil.isTrue(userName == null, "用户姓名不能为空!");
        //验证密码
        AssertUtil.isTrue(userPwd == null, "用户密码不能为空!");
    }

    /**
     * 添加用户
     */
    @Transactional
    public void addUser(User user) {
        //参数校验
        checkUserParams(user.getUserName(), user.getEmail(), user.getPhone(), null);
        //设置参数默认值
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        //执行添加操作  ，判断受影响行数
        AssertUtil.isTrue(userMapper.insertSelective(user) != 1, "用户添加失败!");

        /**用户角色关联
         * 用户id：用户对象中获取
         * 角色id：roleIds
         */
        relationUserRole(user.getId(), user.getRoleIds());
    }

    /**
     * 用户-角色关联
     */
    private void relationUserRole(Integer userId, String roleIds) {
        //通过用户id查询角色记录
        Integer count = userRoleMapper.countUserRoleByUserId(userId);
        //判断角色记录是否存在
        if (count > 0) {
            //如果角色记录存在，则删除该用户对应的角色记录
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "用户角色分配失败");
        }
        //判断角色id是否存在，如果存在，则添加该用户对应的角色记录
        if (StringUtils.isNotBlank(roleIds)) {
            //将用户角色数据设置到集合中，批量添加
            List<UserRole> userRoleList = new ArrayList<>();
            //将角色id字符串转换成数组
            String[] roleIdsArr = roleIds.split(",");
            //遍历数组
            for (String roleId : roleIdsArr) {
                UserRole userRole = new UserRole();
                userRole.setRoleId(Integer.valueOf(roleId));
                userRole.setUserId(userId);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                //对象设置到集合中
                userRoleList.add(userRole);
            }
            //批量添加，判断受影响行数
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList) != userRoleList.size(), "用户角色分配失败!");
        }

    }

    /**
     * 参数校验
     *
     * @param userName
     * @param email
     * @param phone
     */
    private void checkUserParams(String userName, String email, String phone, Integer userId) {
        AssertUtil.isTrue(StringUtils.isBlank(userName), "用户名不能为空!");
        //判断用户名的唯一性
        //通过用户名查询用户对象
        //1、对于添加操作，如果查询到了用户说明用户名被占用了
        //2、对于修改用户，如果查询到的用户就是你正在修改的用户，这是合理的
        User temp = userMapper.queryUserByName(userName);
        AssertUtil.isTrue(temp != null && !(temp.getId().equals(userId)),
                "用户名已存在,请重新输入!");

        //邮箱非空
        AssertUtil.isTrue(StringUtils.isBlank(email), "邮箱不能为空");
        //手机号非空
        AssertUtil.isTrue(StringUtils.isBlank(phone), "手机号不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone), "手机号格式错误");
    }

    /**
     * 更新用户
     */
    @Transactional
    public void updateUser(User user) {
        AssertUtil.isTrue(user.getId() == null, "待更新记录不存在");
        //通过id查询数据
        User temp = userMapper.selectByPrimaryKey(user.getId());
        AssertUtil.isTrue(temp == null, "待更新记录不存在");
        //参数校验
        checkUserParams(user.getUserName(), user.getEmail(), user.getPhone(), temp.getId());

        //设置默认值
        user.setUpdateDate(new Date());
        //执行更新操作，返回受影响行数
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) != 1, "用户更新失败!");

        //更新用户-角色表
        relationUserRole(user.getId(), user.getRoleIds());
    }

    /**
     * 删除用户数据
     */
    @Transactional
    public void deleteByIds(Integer[] ids) {
        //判断ids是否为空，长度是否 > 0
        AssertUtil.isTrue(ids == null || ids.length == 0, "待删除记录不存在");
        //执行删除操作，判断受影响行数
        AssertUtil.isTrue(userMapper.deleteBatch(ids) != ids.length, "用户删除失败!");

        //遍历用户id数组，循环删除
        for (Integer userId : ids) {
            //通过用户id查询对应的用户角色记录
            Integer count = userRoleMapper.countUserRoleByUserId(userId);
            if (count > 0) {
                //通过用户id 删除用户角色记录
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count,
                        "删除用户失败!");
            }
        }
    }
}
