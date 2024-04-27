package com.example.gameinfoservice.aspect;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);


    @Pointcut("@annotation(AspectAnnotation)")
    public void callAtMyServiceAnnotation() { }

    @Pointcut("@annotation(com.example.gameinfoservice.aspect.ExceptionLoggerAnnotation)")
    public void error() { }

    @Before(value = "error()")
    public void logException(final JoinPoint joinPoint) {
        String methodName = joinPoint.toString();
        LOGGER.error("<< {}() - ", methodName);
    }

    @Before(value = "callAtMyServiceAnnotation()")
    public void logBefore(final JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String methodName = joinPoint + " "
                + joinPoint.getSignature().getName();
        LOGGER.info(">> {}() - {}\n", methodName, Arrays.toString(args));
    }

    @AfterReturning(value = "callAtMyServiceAnnotation()", returning = "result")
    public void logAfter(final JoinPoint joinPoint, final Object result) {
        String methodName = joinPoint.toString();
        LOGGER.info("<< {}() - {}", methodName, result);
    }

    @AfterThrowing(pointcut = "callAtMyServiceAnnotation()", throwing = "exception")
    public void logException(final JoinPoint joinPoint, final Throwable exception) {
        String methodName = joinPoint.toString();
        LOGGER.error("<< {}() - {}", methodName, exception.getMessage());
    }
    @PostConstruct
    public void initAspect() {
        LOGGER.info("Aspect is initialized");
    }

    @PreDestroy
    public void destroyAspect() {
        LOGGER.info("Aspect is destroyed");
    }
}
