package spengergasse.at.sj2425scherzerrabar.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MetricsAspectTest {

    private MeterRegistry meterRegistry;
    private MetricsAspect metricsAspect;

    @BeforeEach
    void setUp() {
        meterRegistry = mock(MeterRegistry.class);
        metricsAspect = new MetricsAspect(meterRegistry);
    }

    @Test
    void testCountAndTimeControllerCalls_Success() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("TestController.testMethod()");

        Counter counter = mock(Counter.class);
        when(meterRegistry.counter("controller.invocations", "method", "TestController.testMethod()"))
                .thenReturn(counter);

        Timer timer = mock(Timer.class);
        when(meterRegistry.timer("controller.execution.time", "method", "TestController.testMethod()"))
                .thenReturn(timer);

        // Simuliere Ergebnis des Controller-Calls
        when(joinPoint.proceed()).thenReturn("Success");

        // Simuliere Timer-Verhalten
        when(timer.recordCallable(any(Callable.class))).thenAnswer(invocation -> {
            Callable<?> callable = invocation.getArgument(0);
            return callable.call();
        });

        Object result = metricsAspect.countAndTimeControllerCalls(joinPoint);
        assertEquals("Success", result);

        verify(counter).increment();
        verify(timer).recordCallable(any());
    }

    @Test
    void testCountAndTimeControllerCalls_Exception() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Signature signature = mock(Signature.class);

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("TestController.testMethod()");

        Counter invocationCounter = mock(Counter.class);
        when(meterRegistry.counter("controller.invocations", "method", "TestController.testMethod()"))
                .thenReturn(invocationCounter);

        Counter errorCounter = mock(Counter.class);
        when(meterRegistry.counter("controller.errors", "method", "TestController.testMethod()"))
                .thenReturn(errorCounter);

        Timer timer = mock(Timer.class);
        when(meterRegistry.timer("controller.execution.time", "method", "TestController.testMethod()"))
                .thenReturn(timer);

        when(joinPoint.proceed()).thenThrow(new IllegalStateException("Error!"));

        when(timer.recordCallable(any())).thenAnswer(invocation -> {
            Callable<?> callable = invocation.getArgument(0);
            try {
                return callable.call();
            } catch (Exception e) {
                throw e;
            }
        });

        try {
            metricsAspect.countAndTimeControllerCalls(joinPoint);
        } catch (RuntimeException ex) {
            assertEquals("java.lang.IllegalStateException: Error!", ex.getMessage());
        }

        verify(invocationCounter).increment();
        verify(errorCounter).increment();
        verify(timer).recordCallable(any());
    }
}
