package com.burakyapici.library.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);
    private static final Map<String, Long> methodExecutionTimeMap = new HashMap<>();

    @Pointcut("execution(public * com.burakyapici.library.api.controller..*.*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(public * com.burakyapici.library.service.impl..*.*(..))")
    public void serviceImplMethods() {}

    @Pointcut("controllerMethods() || serviceImplMethods()")
    public void applicationExecution() {}

    @Around("applicationExecution()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String shortClassName = className.substring(className.lastIndexOf('.') + 1);
        
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String methodType = determineMethodType(method);
        
        Object[] args = joinPoint.getArgs();
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        if (className.contains("controller")) {
            log.info("[REQ:{}] [CONTROLLER] ==> {}#{} {} - Request started - Parameters: {}",
                    requestId, shortClassName, methodName, methodType, formatArgs(args));
        } else {
            log.info("[REQ:{}] [SERVICE]    ==> {}#{} - Operation started - Parameters: {}",
                    requestId, shortClassName, methodName, formatArgs(args));
        }

        long startTime = System.currentTimeMillis();
        methodExecutionTimeMap.put(requestId, startTime);

        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - startTime;

            if (className.contains("controller")) {
                log.info("[REQ:{}] [CONTROLLER] <== {}#{} {} - Operation successful - Duration: {}ms - Result: {}",
                        requestId, shortClassName, methodName, methodType, elapsedTime, formatResult(result));
            } else {
                log.info("[REQ:{}] [SERVICE]    <== {}#{} - Operation successful - Duration: {}ms - Result: {}",
                        requestId, shortClassName, methodName, elapsedTime, formatResult(result));
            }

            return result;
        } catch (IllegalArgumentException e) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            log.error("[REQ:{}] [ERROR]    {}#{} - Invalid argument - Duration: {}ms - Arguments: {} - Message: {}",
                    requestId, shortClassName, methodName, elapsedTime, formatArgs(args), e.getMessage());
            throw e;
        } catch (Exception e) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            log.error("[REQ:{}] [ERROR]    {}#{} - Operation failed - Duration: {}ms - Type: {} - Message: {}",
                    requestId, shortClassName, methodName, elapsedTime, e.getClass().getSimpleName(), e.getMessage());
            throw e;
        } finally {
            methodExecutionTimeMap.remove(requestId);
        }
    }
    
    private String determineMethodType(Method method) {
        if (method.isAnnotationPresent(GetMapping.class)) {
            return "GET";
        } else if (method.isAnnotationPresent(PostMapping.class)) {
            return "POST";
        } else if (method.isAnnotationPresent(PutMapping.class)) {
            return "PUT";
        } else if (method.isAnnotationPresent(DeleteMapping.class)) {
            return "DELETE";
        } else if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if (requestMapping.method().length > 0) {
                return requestMapping.method()[0].toString();
            }
        }
        return "METHOD";
    }
    
    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof UUID) {
                result.append("id:").append(arg);
            } 
            else {
                result.append(arg);
            }
            
            if (i < args.length - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }
    
    private String formatResult(Object result) {
        if (result == null) {
            return "null";
        }
        
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) result;
            return String.format("Status: %s, Body: %s", 
                responseEntity.getStatusCode(),
                responseEntity.getBody());
        }
        
        return result.toString();
    }
}