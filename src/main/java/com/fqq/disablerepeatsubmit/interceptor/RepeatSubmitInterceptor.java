package com.fqq.disablerepeatsubmit.interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.fqq.disablerepeatsubmit.annotations.RepeatSubmit;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义一个拦截器
 */
public abstract class RepeatSubmitInterceptor implements HandlerInterceptor {
    /**
     * 前置拦截
     *
     * @param request  请求参数
     * @param response 响应参数
     * @param handler  控制器
     * @return 是否阻止提交
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 拦截器会拦截指定的访问路径（这里指定所有），并获得该路径的类或方法（控制器 handler），
        // 用 HandlerMethod 判断接收的控制器是不是一个方法；HandlerMethod 控制器方法类型。
        // 模式变量：在判断里直接声明：HandlerMethod handlerMethod
        if (handler instanceof HandlerMethod handlerMethod) {
            // 获取方法
            Method method = handlerMethod.getMethod();
            // 获取注解
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);

            // 如果获取的注解不为空，对这个方法进行处理
            // 判断是不是重复提交的表单
            if (annotation != null && isRepeatSubmit(request, annotation)) {
                // 如果是，设置返回信息给页面
                Map<Object, Object> hashMap = new HashMap<>();
                // 设置响应状态码
                hashMap.put("code", HttpStatus.FORBIDDEN.value());
                // 设置响应信息
                hashMap.put("msg", annotation.massage());
                // 渲染信息，转为 json 数据，返回页面
                renderString(response, JSONObject.toJSONString(hashMap));
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否是重复提交的内容
     *
     * @param request      接收的请求
     * @param repeatSubmit 传入注解
     * @return 是：重复提交；否：不是重复提交
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit repeatSubmit);

    /**
     * 将字符串渲染到客户端
     *
     * @param response 响应
     * @param str      渲染回去的字符串
     */
    public void renderString(HttpServletResponse response, String str) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
