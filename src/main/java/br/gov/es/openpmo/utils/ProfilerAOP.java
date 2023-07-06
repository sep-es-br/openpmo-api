package br.gov.es.openpmo.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProfilerAOP {

    @Around("@annotation(br.gov.es.openpmo.configuration.Profile)")
    public Object measureMethodExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        final Profiler profiler = Profiler.of(pjp.getTarget().getClass().getName());
        profiler.start(pjp.getSignature().getName());
        Object retval = pjp.proceed();
        profiler.end();
        return retval;
    }

}
