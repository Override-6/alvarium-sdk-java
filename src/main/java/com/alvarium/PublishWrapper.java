
/*******************************************************************************
 * Copyright 2021 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package com.alvarium;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.SignedAnnotationBundle;
import com.alvarium.serializers.AnnotationConverter;
import com.alvarium.serializers.PublishWrapperConverter;
import com.alvarium.serializers.SignedAnnotationBundleConverter;
import com.alvarium.serializers.ZonedDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A java bean that encapsulates the content sent through the stream providers
 * and appends the required metadata
 */
public class PublishWrapper<C> implements Serializable {
    private final SdkAction action;
    private final String messageType;
    private final C content;

    public PublishWrapper(SdkAction action, String messageType, C content) {
        this.action = action;
        this.messageType = messageType;
        this.content = content;
    }

    public SdkAction getAction() {
        return action;
    }

    public String getMessageType() {
        return messageType;
    }

    public C getContent() {
        return content;
    }

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(PublishWrapper.class, new PublishWrapperConverter())
            .registerTypeAdapter(SignedAnnotationBundle.class, new SignedAnnotationBundleConverter())
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeConverter())
            .registerTypeAdapter(Annotation.class, new AnnotationConverter())
            .disableHtmlEscaping()
            .create();

    public String toJson() {
        return GSON.toJson(this);
    }
}
