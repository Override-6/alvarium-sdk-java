package com.alvarium.annotators;

import com.alvarium.contracts.AnnotationType;

public class EnvironmentCheckerEntry {

    private final AnnotationType type;
    private final EnvironmentChecker checker;

    public EnvironmentCheckerEntry(AnnotationType type, EnvironmentChecker checker) {
        this.type = type;
        this.checker = checker;
    }


    public AnnotationType getType() {
        return type;
    }

    public EnvironmentChecker getChecker() {
        return checker;
    }
}
