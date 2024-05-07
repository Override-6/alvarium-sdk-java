
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
package com.alvarium.contracts;


import com.alvarium.serializers.AnnotationConverter;
import com.alvarium.serializers.InstantConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * A java bean that encapsulates all of the data related to a specific annotation.
 * this will be generated by the annotators.
 */
public class Annotation implements Serializable {
    private final String id;
    //  private final String key;
//  private final HashType hash;
//  private final String host;
    private final String tag;
    private final AnnotationType kind;
    //  private String signature;
    private final boolean isSatisfied;
    //  private final Instant timestamp;


    public Annotation(AnnotationType kind, boolean isSatisfied, String tag) {
//        ULID ulid = new ULID();
//        this.id = ulid.nextULID();
        this.id = UUID.randomUUID().toString();
        this.tag = tag;
        this.kind = kind;
        this.isSatisfied = isSatisfied;
    }

    //setters

    // getters

    public String getId() {
        return this.id;
    }

    public String getTag() {
        return this.tag;
    }


    public AnnotationType getKind() {
        return this.kind;
    }

    public Boolean getIsSatisfied() {
        return this.isSatisfied;
    }


    /**
     * returns the JSON representation of the Annotation object
     *
     * @return json string representation
     */
    public String toJson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Annotation.class, new AnnotationConverter())
                .create();
        return gson.toJson(this, Annotation.class);
    }

    /**
     * instantiates an Annotation object from a json representation
     *
     * @param json input JSON string
     * @return Annotation Object
     */
    public static Annotation fromJson(String json) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantConverter())
                .create();
        return gson.fromJson(json, Annotation.class);
    }

    /**
     * Returns an identity string, two annotations that are equals should also return the same identityString.
     * two annotations that have the same identityString should also be equals.
     * <p>
     *     a.identityString().equals(b.identityString()) <=> a.equals(b)
     * </p>
     */
    public String identityString() {
        return id +
                tag +
                kind +
                isSatisfied;
    }

}
