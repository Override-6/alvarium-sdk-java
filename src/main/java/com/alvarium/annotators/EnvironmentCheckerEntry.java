package com.alvarium.annotators;

import com.alvarium.contracts.AnnotationType;

public record EnvironmentCheckerEntry(AnnotationType type, EnvironmentChecker checker) {
}
