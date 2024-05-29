package com.alvarium.serializers;

import com.alvarium.contracts.AnnotationBundle;
import com.alvarium.contracts.SignedAnnotationBundle;
import com.google.gson.*;

import java.lang.reflect.Type;

public class SignedAnnotationBundleConverter implements JsonSerializer<SignedAnnotationBundle> {
    @Override
    public JsonElement serialize(SignedAnnotationBundle src, Type typeOfSrc, JsonSerializationContext context) {
        var json = new JsonObject();

        AnnotationBundle bundle = src.bundle();

        json.add("signature", new JsonPrimitive(src.signature()));
        json.add("key",new JsonPrimitive(bundle.key()));
        json.add("hash", new JsonPrimitive(bundle.hash().name()));
        json.add("host", new JsonPrimitive(bundle.host()));
        json.add("layer", new JsonPrimitive(bundle.layer().name()));
        json.add("timestamp", context.serialize(bundle.timestamp()));

        json.add("annotations", context.serialize(bundle.annotations()));

        return json;
    }
}
