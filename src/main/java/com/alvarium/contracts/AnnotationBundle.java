package com.alvarium.contracts;

import com.alvarium.hash.HashType;

import java.time.Instant;

public record AnnotationBundle(Iterable<Annotation> annotations, String key, HashType hash, LayerType layer, Instant timestamp) {
}
