package com.alvarium.contracts;

public class SignedAnnotationBundle {
    private final String signature;
    private final AnnotationBundle bundle;

    public SignedAnnotationBundle(String signature, AnnotationBundle bundle) {
        this.signature = signature;
        this.bundle = bundle;
    }

    public String getSignature() {
        return signature;
    }

    public AnnotationBundle getBundle() {
        return bundle;
    }
}