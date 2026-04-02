package com.pm.patientservice.aspects;

import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

public class PatientServiceMetrics {
    private final MeterRegistry meterRegistry;

    public PatientServiceMetrics(MeterRegistry meterRegistry){
        this.meterRegistry=meterRegistry;
    }

    @Around("execution(* com.pm.patientservice.Service.PatientService.getPatients(...))")
    public Object monitorGetPatients(ProceedingJoinPoint joinPoint) throws Throwable{
        //increment the [custom.redis.cache.miss{cache="patients"} = 1++]
        meterRegistry.counter("custom.redis.cache.miss","cache","patients").increment();

        return joinPoint.proceed(); //this line calls the getPatient() method
    }
}
