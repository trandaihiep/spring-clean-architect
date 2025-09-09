package com.example.demo.domain.port;

public interface UmaDecisionPort {
    boolean isAllowed(String subjectToken, String resource, String scope);
}

