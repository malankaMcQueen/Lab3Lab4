package com.example.gameinfoservice.aspect;

import com.example.gameinfoservice.counter.RequestCounter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CounterAspect {
    RequestCounter requestCounter = new RequestCounter();
    private static final Logger LOGGER = LoggerFactory.getLogger(CounterAspect.class);

    @Pointcut("@annotation(RequestCounterAnnotation)")
    public void callAtMyServiceAnnotation() { }
    @Before(value = "callAtMyServiceAnnotation()")
    public void logBefore(final JoinPoint joinPoint) {
        requestCounter.increment();
        String methodName = joinPoint + " "
                + joinPoint.getSignature().getName();
        LOGGER.info("Request Counter: {} - {}\n", requestCounter.getCount(), methodName);
    }
}
