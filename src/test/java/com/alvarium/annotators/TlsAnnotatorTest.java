
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
import com.alvarium.utils.ImmutablePropertyBag;
import com.alvarium.utils.PropertyBag;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Test;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

public class TlsAnnotatorTest {
    @Test
    public void executeShouldReturnAnnotation() throws AnnotatorException, IOException,
        UnknownHostException {
        // init logger
        final Logger logger = LogManager.getRootLogger();
        Configurator.setRootLevel(Level.DEBUG);

        final EnvironmentChecker annotator = new TlsChecker(logger);

        // dummy data
        final byte[] data = "test data".getBytes();

        // create a connect with google servers
        // and provide the SSLSocket to the annotator
        SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket socket = (SSLSocket) sslFactory.createSocket("www.google.com", 443);
        HashMap<String, Object> map = new HashMap<>();
        map.put(AnnotationType.TLS.name(), socket);
        final PropertyBag bag = new ImmutablePropertyBag(map);

        assert annotator.isSatisfied(bag, data);
    }
}
