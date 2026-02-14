package de.ait.g_74_shop.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AspectLogging {

    private final Logger logger = LoggerFactory.getLogger(AspectLogging.class);

    @Pointcut("execution(* de.ait.g_74_shop.service..*(..))") // прописуємо правила до який методів ми чіпляємся
    public void anyServiceMethod() {
    }

    // Прописуємо Advice Before перед логікою
    @Before("anyServiceMethod()")
    public void beforeAnyMethodInProductService(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs(); // Достає всі аргументи

        logger.debug("Method {} of the class {} called with arguments: {}",
                methodName, className, Arrays.toString(args));
    }
    // Выводить все аргументы подряд - опасно!
    // 1. В аргументах могут быть очень большие объекты.
    // 2. В аргументах могут быть секреты.

    @AfterReturning(pointcut = "anyServiceMethod()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();


        logger.debug("Method {} of the class {} returned result: {}",
                methodName, className, result);
    }

    @AfterThrowing(pointcut = "anyServiceMethod()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.warn("Method {} of the class {} threw an exception: {}",
                methodName, className, e.getMessage());
    }

    @AfterReturning(
            pointcut = "anyServiceMethod()",
            returning = "result"
    )
    public void afterReturningAnyMethodInProductService(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        logger.debug("Method {} of the class ProductServiceImpl returned result: {}",
                methodName, result);
    }


    // Адвайс щоб бачити яку помилку видає
    @AfterThrowing(
            pointcut = "anyServiceMethod()",
            throwing = "e"
    )
    public void afterThrowingAnyMethodInProductService(JoinPoint joinPoint, Exception e) {
        String methodName = joinPoint.getSignature().getName();
        logger.warn("Method {} of the class ProductServiceImpl threw an exception",
                methodName, e);
    }

    // @Around
}
