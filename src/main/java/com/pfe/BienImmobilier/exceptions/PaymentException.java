// PaymentException.java
package com.pfe.BienImmobilier.exceptions;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}