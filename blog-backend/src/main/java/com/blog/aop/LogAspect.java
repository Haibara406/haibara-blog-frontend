package com.blog.aop;

import com.alibaba.fastjson.JSON;
import com.blog.annotation.LogAnnotation;
import com.blog.constants.FunctionConst;
import com.blog.domain.entity.Log;
import com.blog.domain.entity.User;
import com.blog.domain.response.ResponseResult;
import com.blog.mapper.UserMapper;
import com.blog.utils.IpUtils;
import com.blog.utils.SecurityUtils;
import com.blog.utils.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author haibara
 * @description 操作日志aop
 * @since 2025/7/27 16:19
 */
@Component
@Slf4j
@Aspect // 切面
public class LogAspect {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.routingKey.log-system}")
    private String routingKey;

    @Value("${spring.rabbitmq.exchange.log}")
    private String exchange;

    /**
     * 切点，注解加在哪，哪就是切点
     */
    @Pointcut("@annotation(com.blog.annotation.LogAnnotation)")
    public void pt() {
    }

    // 环绕通知，在方法执行前后执行
    @Around("pt()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        long beginTime = System.currentTimeMillis();
        try {
            // 执行方法
            Object result = joinPoint.proceed();
            // 执行时长
            long time = System.currentTimeMillis() - beginTime;
            recordLog(joinPoint, time,result);
            // 打印日志
            log.info("【{}】执行方法【{}】，耗时【{}】毫秒", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), time);
            return result;
        } catch (Throwable e) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
            long time = System.currentTimeMillis() - beginTime;
            // 获取 request 设置IP地址
            HttpServletRequest request = SecurityUtils.getCurrentHttpRequest();
            // 请求的方法名
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = signature.getName();
            assert request != null;
            // 是否前台
            String ipAddr = IpUtils.getIpAddr(request);
            User user = userMapper.selectById(SecurityUtils.getUserId());

            Object[] args = joinPoint.getArgs();
            List<Object> filteredArgs = filterServletObjects(args);

            Log logEntity = Log.builder()
                    .module(logAnnotation.module())
                    .operation(logAnnotation.operation())
                    .ip(ipAddr)
                    .exception(e.getMessage())
                    .reqMapping(request.getMethod())
                    .userName(StringUtils.isNull(user) ? FunctionConst.UNKNOWN_USER : user.getUsername())
                    .state(2)
                    .exception(e.getMessage())
                    .method(className + "." + methodName + "()")
                    .reqParameter(safeToJSONString(filteredArgs))
                    .reqAddress(request.getRequestURI())
                    .time(time)
                    .build();
            rabbitTemplate.convertAndSend(exchange,routingKey,logEntity);
            log.error("【{}】执行方法【{}】异常", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), e);
            // 这里一定要重新抛出去，不然全局异常处理器会失效
            throw e;
        }

    }

    /**
     * 过滤参数中的 Servlet 对象，避免序列化时出现问题
     *
     * @param args 原始参数数组
     * @return 过滤后的参数列表
     */
    private List<Object> filterServletObjects(Object[] args) {
        List<Object> filteredArgs = new ArrayList<>();
        for (Object arg : args) {
            if (arg instanceof HttpServletRequest ||
                arg instanceof HttpServletResponse ||
                arg instanceof ServletContext) {
                // 对于 Servlet 对象，只记录类型名
                filteredArgs.add(arg.getClass().getSimpleName());
            } else if (arg instanceof MultipartFile) {
                // 对于文件上传对象，记录文件信息而不是对象本身
                MultipartFile file = (MultipartFile) arg;
                filteredArgs.add("MultipartFile{name=" + file.getOriginalFilename() +
                               ", size=" + file.getSize() + "}");
            } else {
                filteredArgs.add(arg);
            }
        }
        return filteredArgs;
    }

    /**
     * 安全地序列化对象为 JSON 字符串
     *
     * @param obj 要序列化的对象
     * @return JSON 字符串，如果序列化失败则返回错误信息
     */
    private String safeToJSONString(Object obj) {
        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            log.warn("JSON序列化失败: {}", e.getMessage());
            return "序列化失败: " + e.getMessage();
        }
    }

    private void recordLog(ProceedingJoinPoint joinPoint, long time,Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
        // 操作描述
        Operation operation = method.getAnnotation(Operation.class);

        // 获取 request 设置IP地址
        HttpServletRequest request = SecurityUtils.getCurrentHttpRequest();
        // 请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        assert request != null;
        String ipAddr = IpUtils.getIpAddr(request);
        User user = userMapper.selectById(SecurityUtils.getUserId());

        Object[] args = joinPoint.getArgs();
        List<Object> filteredArgs = filterServletObjects(args);

        Log log = Log.builder()
                .module(logAnnotation.module())
                .operation(logAnnotation.operation())
                .ip(ipAddr)
                .description(operation.summary())
                .reqMapping(request.getMethod())
                .userName(StringUtils.isNull(user) ? FunctionConst.UNKNOWN_USER : user.getUsername())
                .method(className + "." + methodName + "()")
                .reqParameter(safeToJSONString(filteredArgs))
                .returnParameter(safeToJSONString(result))
                .reqAddress(request.getRequestURI())
                .time(time)
                .build();
        // 解决result可能为null而导致空指针异常的问题
        log.setState(result instanceof ResponseResult response && response.getCode() == 200 ? 0 : 1);

        rabbitTemplate.convertAndSend(exchange,routingKey,log);
        LogAspect.log.info("耗时：{}毫秒", time);
        LogAspect.log.info("操作时间：{}", new Date());
        LogAspect.log.info("================日志结束=========================");

    }

}
