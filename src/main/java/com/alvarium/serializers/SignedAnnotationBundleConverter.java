package com.alvarium.serializers;

import com.alvarium.contracts.AnnotationBundle;
import com.alvarium.contracts.SignedAnnotationBundle;
import com.google.gson.*;

import java.lang.reflect.Type;

public class SignedAnnotationBundleConverter implements JsonSerializer<SignedAnnotationBundle> {
    @Override
    public JsonElement serialize(SignedAnnotationBundle src, Type typeOfSrc, JsonSerializationContext context) {
        var json = new JsonObject();

        AnnotationBundle bundle = src.getBundle();

        json.add("signature", new JsonPrimitive(src.getSignature()));
        json.add("key", new JsonPrimitive(bundle.getKey()));
        json.add("hash", new JsonPrimitive(bundle.getHash().name()));
        json.add("host", new JsonPrimitive(bundle.getHost()));
        json.add("layer", new JsonPrimitive(bundle.getLayer().name()));
        json.add("timestamp", context.serialize(bundle.getTimestamp()));

        json.add("annotations", context.serialize(bundle.getAnnotations()));

        return json;
    }
}