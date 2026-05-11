package com.pm.paymentservice.enitiy.enums;

public enum PaymentStatus {
    INITIATED,
    PROCESSING,
    SUCCESS,
    FAILED,
    REFUNDED,
    RETRY_SHEDULED,
    DLQ_PUSHED
}
