package spengergasse.at.sj2425scherzerrabar.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MetricsAspect {

    private final MeterRegistry meterRegistry;

    public MetricsAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("execution(* spengergasse.at.sj2425scherzerrabar.presentation.www..*(..))")
    public Object countAndTimeControllerCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();

        meterRegistry.counter("controller.invocations", "method", methodName).increment();

        return meterRegistry.timer("controller.execution.time", "method", methodName)
                .recordCallable(() -> {
                    try {
                        return joinPoint.proceed();
                    } catch (Throwable e) {
                        meterRegistry.counter("controller.errors", "method", methodName).increment();
                        try {
                            throw e;
                        } catch (Throwable ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
    }


}
