package com.alvarium.contracts;

import com.alvarium.hash.HashType;

import java.time.ZonedDateTime;

public record AnnotationBundle(Iterable<Annotation> annotations,
                               String key,
                               String host,
                               HashType hash,
                               LayerType layer,
                               ZonedDateTime timestamp) {
}
