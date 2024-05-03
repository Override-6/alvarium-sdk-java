package com.alvarium.contracts;

import com.alvarium.hash.HashType;

import java.time.Instant;
import java.time.ZonedDateTime;


public class AnnotationBundle {
    private final Iterable<Annotation> annotations;
    private final String host;
    private final String key;
    private final HashType hash;
    private final LayerType layer;
    private final ZonedDateTime timestamp;

    public AnnotationBundle(Iterable<Annotation> annotations, String host, String key, HashType hash, LayerType layer, ZonedDateTime timestamp) {
        this.annotations = annotations;
        this.host = host;
        this.key = key;
        this.hash = hash;
        this.layer = layer;
        this.timestamp = timestamp;
    }

    public Iterable<Annotation> getAnnotations() {
        return annotations;
    }

    public String getKey() {
        return key;
    }

    public HashType getHash() {
        return hash;
    }

    public LayerType getLayer() {
        return layer;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public String getHost() {
        return host;
    }
}