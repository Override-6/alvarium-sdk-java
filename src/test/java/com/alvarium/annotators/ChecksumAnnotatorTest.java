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

import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashProvider;
import com.alvarium.hash.HashProviderFactory;
import com.alvarium.hash.HashType;
import com.alvarium.hash.HashTypeException;
import com.alvarium.utils.ImmutablePropertyBag;
import com.alvarium.utils.PropertyBag;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class ChecksumAnnotatorTest {
    @Rule
    public TemporaryFolder dir = new TemporaryFolder();

    @Test
    public void executeShouldReturnAnnotation() throws AnnotatorException, HashTypeException, IOException {
        // init logger
        final Logger logger = LogManager.getRootLogger();
        Configurator.setRootLevel(Level.DEBUG);
        EnvironmentChecker annotator = new ChecksumChecker(HashType.MD5Hash, logger);

        // Generate dummy artifact and generate checksum

        final File artifactFile = dir.newFile("artifact");
        Files.write(artifactFile.toPath(), "foo".getBytes());

        final File checksumFile = dir.newFile("checksum");

        final HashProvider hash = new HashProviderFactory().getProvider(HashType.MD5Hash);
        final String checksum = hash.derive(
            Files.readAllBytes(artifactFile.toPath())
        );

        Files.write(checksumFile.toPath(), checksum.getBytes());

        System.out.println(checksum);
        final ChecksumAnnotatorProps props = new ChecksumAnnotatorProps(
            artifactFile.toPath().toString(),
            checksumFile.toPath().toString()
        );

        PropertyBag ctx = new ImmutablePropertyBag(
            Map.of(AnnotationType.CHECKSUM.name(), props)
        );

        byte[] data = "pipeline1/1".getBytes();
        assert annotator.isSatisfied(ctx, data);

        // change artifact checksum
        Files.write(checksumFile.toPath(), "bar".getBytes());


        assert !annotator.isSatisfied(ctx, data);
    }

}
