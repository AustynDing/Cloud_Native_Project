package com.example.cloudnativeproject.controller.limit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.*;


@Aspect
@Order
@Component
public class RequestLimitContract {

    private Map<String, Integer> redisTemplate = new HashMap<>();//记录每个接口的访问次数

    @Pointcut("@annotation(RequestLimit)")
    public void RequestLimit(){

    }

    @Around("RequestLimit()")
    public synchronized Object requestLimit(ProceedingJoinPoint joinPoint) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String url = request.getRequestURI();
        // 获取自定义注解
        RequestLimit rateLimiter = getRequestLimit(joinPoint);

        String key = "req_limit_".concat(url); //hash的key
        if (!redisTemplate.containsKey(key)) { //接口未访问过
            redisTemplate.put(key, 1);
        } else {
            redisTemplate.put(key, redisTemplate.get(key) + 1);
            int count = redisTemplate.get(key);
            if (count > rateLimiter.count()) {
                throw new RequestLimitException();
            }else {
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {    //创建一个新的计时器任务。
                    @Override
                    public synchronized void run() {
                        redisTemplate.remove(key);
                    }
                };
                timer.schedule(task, rateLimiter.time());
                //安排在指定延迟后执行指定的任务。task : 所要安排的任务。time : 执行任务前的延迟时间，单位是毫秒。
            }
        }
        return joinPoint.proceed();
    }

    private RequestLimit getRequestLimit(final JoinPoint joinPoint) {
        Method[] methods = joinPoint.getTarget().getClass().getDeclaredMethods();
        String name = joinPoint.getSignature().getName();
        if (!StringUtils.isEmpty(name)) {
            for (Method method : methods) {
                RequestLimit annotation = method.getAnnotation(RequestLimit.class);
                if (!Objects.isNull(annotation) && name.equals(method.getName())) {
                    return annotation;
                }
            }
        }
        return null;
    }
}