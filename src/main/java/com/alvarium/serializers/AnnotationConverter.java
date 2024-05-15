package com.alvarium.serializers;

import com.alvarium.contracts.Annotation;
import com.google.gson.*;

import java.lang.reflect.Type;

public class AnnotationConverter implements JsonSerializer<Annotation> {

    @Override
    public JsonElement serialize(Annotation src, Type typeOfSrc, JsonSerializationContext context) {
        var json = new JsonObject();

        json.add("id", new JsonPrimitive(src.getId()));
        json.add("type", new JsonPrimitive(src.getKind().name()));
        json.add("tag", new JsonPrimitive(src.getTag()));
        json.add("isSatisfied", new JsonPrimitive(src.getIsSatisfied()));

        return json;
    }
}
