/*******************************************************************************
 * Copyright 2024 Dell Inc.
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

import com.alvarium.annotators.sbom.SbomAnnotatorConfig;
import com.alvarium.contracts.AnnotationType;
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
import org.junit.Test;

import java.util.HashMap;

public class SbomAnnotatorTest {

    @Test
    public void executeShouldReturnSatisfiedAnnotation() throws Exception {
        final String json = "{\"kind\":\"sbom\",\"type\":\"spdx\",\"version\":\"SPDX-2.2\"}";
        final EnvironmentChecker annotator = this.getAnnotator(json);

        // dummy data and empty prop bag
        final byte[] data = "test data".getBytes();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(
            AnnotationType.SBOM.name(),
            "./src/test/java/com/alvarium/annotators/sbom/spdx-valid.json"
        );
        final PropertyBag ctx = new ImmutablePropertyBag(map);
        assert annotator.isSatisfied(ctx, data);
    }

    @Test
    public void executeShouldReturnUnsatisfiedAnnotation() throws Exception {
        // provided spdx is version SPDX-2.3, config provides SPDX-2.2 so annotation should fail
        final String json = "{\"kind\":\"sbom\",\"type\":\"spdx\",\"version\":\"SPDX-2.3\"}";
        final EnvironmentChecker annotator = this.getAnnotator(json);
        final byte[] data = "test data".getBytes();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put(
            AnnotationType.SBOM.name(),
            "./src/test/java/com/alvarium/annotators/sbom/spdx-valid.json"
        );
        final PropertyBag ctx = new ImmutablePropertyBag(map);
        assert !annotator.isSatisfied(ctx, data);
    }

    private EnvironmentChecker getAnnotator(String cfg) throws AnnotatorException {
        final KeyInfo pubKey = new KeyInfo("./src/test/java/com/alvarium/annotators/public.key",
            SignType.Ed25519);
        final KeyInfo privKey = new KeyInfo("./src/test/java/com/alvarium/annotators/private.key",
            SignType.Ed25519);
        final SignatureInfo sigInfo = new SignatureInfo(pubKey, privKey);

        final Gson gson = new GsonBuilder()
            .registerTypeAdapter(SbomAnnotatorConfig.class, new AnnotatorConfigConverter())
            .create();


        final SbomAnnotatorConfig annotatorInfo = gson.fromJson(
            cfg,
            SbomAnnotatorConfig.class
        );

        // init logger
        final Logger logger = LogManager.getRootLogger();
        Configurator.setRootLevel(Level.DEBUG);

        return new SbomChecker(annotatorInfo, logger);
    }

}