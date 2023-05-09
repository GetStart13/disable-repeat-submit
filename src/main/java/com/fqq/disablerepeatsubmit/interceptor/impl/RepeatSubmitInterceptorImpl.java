package com.fqq.disablerepeatsubmit.interceptor.impl;

import com.alibaba.fastjson2.JSONObject;
import com.fqq.disablerepeatsubmit.annotations.RepeatSubmit;
import com.fqq.disablerepeatsubmit.interceptor.RepeatSubmitInterceptor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Component
public class RepeatSubmitInterceptorImpl extends RepeatSubmitInterceptor {

    // 重复提交参数
    private static final String REPEAT_PARAMS = "repeatParams";
    // 重复提交限制时间
    private static final String REPEAT_TIME = "repeatTime";
    //
    private static final String SESSION_REPEAT_KEY = "repeatData";

    @Override
    public boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit repeatSubmit) {

        // 获取请求信息，将 json 数据转化为字符串，request.getParameterMap() 只拿到路径参数，对于请求体中的参数并没拿到
        String nowParams = JSONObject.toJSONString(request.getParameterMap());

        // 用 Map 存放当前请求信息和当前请求时间
        Map<String, Object> nowDataMap = new HashMap<>();
        nowDataMap.put(REPEAT_PARAMS, nowParams);
        nowDataMap.put(REPEAT_TIME, System.currentTimeMillis());

        // 获取请求路径 URL
        String url = request.getRequestURI();
        // 获取 session
        HttpSession session = request.getSession();
        // 获取 session 的 SESSION_REPEAT_KEY 属性
        Object attribute = session.getAttribute(SESSION_REPEAT_KEY);

        // 首次提交 attribute 为空
        // 不为空，把 attribute 转回 map（双重 map）
        if (attribute instanceof Map<?, ?>) {
            // 设置定时任务，删除过期的表单数据信息，减少不必要的内存空间使用
            // 获取之前的表单数据
            Object tempMap = ((Map<?, ?>) attribute).get(url);
            if (tempMap instanceof Map<?, ?>) {
                Object paramsData = ((Map<?, ?>) tempMap).get(REPEAT_PARAMS);
                Object timeData = ((Map<?, ?>) tempMap).get(REPEAT_TIME);
                // 如果没找到参数或者时间，直接返回 false，即：不是重复提交
                if (paramsData == null || timeData == null) {
                    return false;
                }
                HashMap<String, Object> preDataMap = new HashMap<>();
                preDataMap.put(REPEAT_PARAMS, paramsData);
                preDataMap.put(REPEAT_TIME, timeData);
                if (compareParams(nowDataMap, preDataMap)
                        // 如果之前的表单数据和当前的表单数据一样，且没过时间，认定重复提交
                        && compareTime(nowDataMap, preDataMap, repeatSubmit.interval())) {
                    return true;
                }
            }
        }
        // 双重 map
        Map<String, Object> sessionMap = new HashMap<>();
        sessionMap.put(url, nowDataMap);
        // 不是重复提交，没有走 if，把当前表单数据存入 session
        session.setAttribute(SESSION_REPEAT_KEY, sessionMap);

        return false;
    }

    /**
     * 判断参数是否相同
     */
    private boolean compareParams(Map<String, Object> nowMap, Map<String, Object> preMap) {
        String nowParams = (String) nowMap.get(REPEAT_PARAMS);
        String preParams = (String) preMap.get(REPEAT_PARAMS);
        return nowParams.equals(preParams);
    }

    /**
     * 判断两次间隔时间
     */
    private boolean compareTime(Map<String, Object> nowMap, Map<String, Object> preMap, int intervalTime) {
        long time1 = (Long) nowMap.get(REPEAT_TIME);
        long time2 = (Long) preMap.get(REPEAT_TIME);
        return (time1 - time2) < intervalTime;
    }
}
