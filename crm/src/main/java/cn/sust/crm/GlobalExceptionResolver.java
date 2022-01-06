package cn.sust.crm;

import cn.sust.crm.base.ResultInfo;
import cn.sust.crm.exceptions.NoLoginException;
import cn.sust.crm.exceptions.ParamsException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 定制化全局异常处理器
 */
@Slf4j
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Object handler, //处理请求的处理器方法
                                         Exception ex) { //异常对象
        //如果是非法请求异常，重定向到登录页
        if (ex instanceof NoLoginException) {
            return new ModelAndView("redirect:/index");
        }


        ModelAndView mv = new ModelAndView("error");
        mv.addObject("code", 500);
        mv.addObject("msg", "系统异常,请重试");
        log.error("全局异常处理器捕获到异常{}", ex);

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = ((HandlerMethod) handler);
            //获取处理器方法上的 @ResponseBody对象
            ResponseBody responseBody =
                    handlerMethod.getMethodAnnotation(ResponseBody.class);
            if (responseBody == null) {
                //返回视图
                if (ex instanceof ParamsException) {
                    ParamsException p = (ParamsException) ex;
                    //设置异常信息
                    mv.addObject("code", p.getCode());
                    mv.addObject("msg", p.getMsg());
                    return mv;
                }
            } else { //返回json数据
                //设置默认的异常处理
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(500);
                resultInfo.setMsg("系统异常,请重试");

                if (ex instanceof ParamsException) {
                    ParamsException p = (ParamsException) ex;
                    resultInfo.setCode(p.getCode());
                    resultInfo.setMsg(p.getMsg());
                }
                //设置响应类型和编码格式(响应json)
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    String json = JSON.toJSONString(resultInfo);
                    //输出json数据给浏览器
                    out.write(json);

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (out != null) {
                        out.close();
                    }
                }
            }
        }
        return mv;
    }
}
