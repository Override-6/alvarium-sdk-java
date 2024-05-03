
/*******************************************************************************
 * Copyright 2023 Dell Inc.
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
package com.alvarium.annotators;

import com.alvarium.SdkInfo;
import com.alvarium.contracts.LayerType;
import com.alvarium.hash.HashInfo;
import com.alvarium.hash.HashType;
import com.alvarium.serializers.AnnotatorConfigConverter;
import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.ImmutablePropertyBag;
import com.alvarium.utils.PropertyBag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

public class TpmAnnotatorTest {

    @Test
    public void executeShouldCreateAnnotation() throws AnnotatorException {
        // init logger
        final Logger logger = LogManager.getRootLogger();
        Configurator.setRootLevel(Level.DEBUG);
        EnvironmentCheckerFactory factory = new EnvironmentCheckerFactory();
        KeyInfo privateKey = new KeyInfo(
                "./src/test/java/com/alvarium/annotators/public.key",
                SignType.Ed25519);
        KeyInfo publicKey = new KeyInfo(
                "./src/test/java/com/alvarium/annotators/public.key",
                SignType.Ed25519);

        SignatureInfo sign = new SignatureInfo(publicKey, privateKey);
        final Gson gson = new GsonBuilder()
                .registerTypeAdapter(AnnotatorConfig.class, new AnnotatorConfigConverter())
                .create();

        final String json = "{\"kind\": \"tpm\"}";
        final AnnotatorConfig annotatorInfo = gson.fromJson(
                json,
                AnnotatorConfig.class

        );
        final AnnotatorConfig[] annotators = {annotatorInfo};
        final SdkInfo config = new SdkInfo(annotators, new HashInfo(HashType.MD5Hash), sign, null, LayerType.Application);
        EnvironmentChecker tpm = factory.getChecker(annotatorInfo, config, logger);

        PropertyBag ctx = new ImmutablePropertyBag(new HashMap<>());

        byte[] data = {0x1, 0x2};
        System.out.println("tpm.isSatisfied(ctx, data) = " + tpm.isSatisfied(ctx, data));
    }

}
