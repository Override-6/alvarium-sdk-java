package com.alvarium.utils;

import com.alvarium.PublishWrapper;
import com.alvarium.contracts.Annotation;
import com.alvarium.serializers.AnnotationConverter;
import com.alvarium.serializers.PublishWrapperConverter;
import com.alvarium.serializers.ZonedDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.ZonedDateTime;

public class Constants {


    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(PublishWrapper.class, new PublishWrapperConverter())
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeConverter())
            .registerTypeAdapter(Annotation.class, new AnnotationConverter())
            .disableHtmlEscaping()
            .create();

}
