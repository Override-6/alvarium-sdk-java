
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
package com.alvarium;

import com.alvarium.annotators.*;
import com.alvarium.contracts.*;
import com.alvarium.hash.HashProvider;
import com.alvarium.hash.HashProviderFactory;
import com.alvarium.hash.HashType;
import com.alvarium.hash.HashTypeException;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.streams.StreamException;
import com.alvarium.streams.StreamProvider;
import com.alvarium.streams.StreamProviderFactory;
import com.alvarium.utils.ImmutablePropertyBag;
import com.alvarium.utils.PropertyBag;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DefaultSdk implements Sdk {
    private final EnvironmentCheckerEntry[] checkers;
    private final SdkInfo config;
    private final StreamProvider stream;
    private final Logger logger;

    private final HashType hash;
    private final SignatureInfo signature;
    private final LayerType layer;
    private final HashProvider hashProvider;

    private static final String HOST_NAME;

    static {
        try {
            HOST_NAME = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }


    // TagEnvKey is an environment key used to associate annotations with specific metadata,
    // aiding in the linkage of scores across different layers of the stack. For instance, in the "app" layer,
    // it is utilized to retrieve the commit SHA of the workload where the application is running,
    // which is instrumental in tracing the impact on the current layer's score from the lower layers.
    public static final String TAG_ENV_KEY = "TAG";

    public DefaultSdk(EnvironmentCheckerEntry[] checkers, SdkInfo config, Logger logger) throws StreamException, HashTypeException {
        this.checkers = checkers;
        this.config = config;
        this.logger = logger;
        this.hash = config.getHash().getType();
        this.signature = config.getSignature();
        this.layer = config.getLayer();
        this.hashProvider = new HashProviderFactory().getProvider(hash);


        // init stream
        final StreamProviderFactory streamFactory = new StreamProviderFactory();
        this.stream = streamFactory.getProvider(this.config.getStream());
        this.stream.connect();
        this.logger.debug("stream provider connected successfully.");
    }

    public void create(PropertyBag properties, byte[] data) throws AnnotatorException,
            StreamException {
        sdkCall(properties, SdkAction.CREATE, data);
    }

    public void create(byte[] data) throws AnnotatorException, StreamException {
        final PropertyBag properties = new ImmutablePropertyBag(new HashMap<String, Object>());
        this.create(properties, data);
    }

    public void mutate(PropertyBag properties, byte[] oldData, byte[] newData) throws
            AnnotatorException, StreamException {
        // source annotate the old data
        final AnnotatorFactory annotatorFactory = new AnnotatorFactory();
        final EnvironmentChecker sourceAnnotator = annotatorFactory.getAnnotator(
                new AnnotatorConfig(AnnotationType.SOURCE),
                this.config,
                this.logger
        );

        String tag = this.getTagValue(layer);

        Annotation sourceOriginAnnotation = createAnnotation(AnnotationType.SOURCE, sourceAnnotator, properties, tag, oldData);

        var checkersNoTls = Arrays.stream(checkers)
                .filter(e -> e.type() != AnnotationType.TLS)
                .toArray(EnvironmentCheckerEntry[]::new);
        AnnotationBundle newDataBundle = this.createAnnotations(checkersNoTls, properties, newData);

        List<Annotation> annotationsMerged = new ArrayList<>(checkersNoTls.length + 1) {{
            add(sourceOriginAnnotation);
            newDataBundle.annotations().forEach(this::add);
        }};
        AnnotationBundle bundle = new AnnotationBundle(annotationsMerged, newDataBundle.key(), newDataBundle.hash(), newDataBundle.layer(), newDataBundle.timestamp());

        // publish to the stream provider
        this.publishAnnotations(SdkAction.MUTATE, bundle);
        this.logger.debug("data annotated and published successfully.");
    }

    public void mutate(byte[] oldData, byte[] newData) throws AnnotatorException, StreamException {
        final PropertyBag properties = new ImmutablePropertyBag(new HashMap<String, Object>());
        this.mutate(properties, oldData, newData);
    }

    public void transit(PropertyBag properties, byte[] data) throws AnnotatorException,
            StreamException {
        sdkCall(properties, SdkAction.TRANSIT, data);
    }

    public void transit(byte[] data) throws AnnotatorException, StreamException {
        final PropertyBag properties = new ImmutablePropertyBag(new HashMap<String, Object>());
        this.transit(properties, data);
    }

    public void publish(PropertyBag properties, byte[] data) throws AnnotatorException,
            StreamException {
        sdkCall(properties, SdkAction.PUBLISH, data);
    }

    public void publish(byte[] data) throws AnnotatorException, StreamException {
        final PropertyBag properties = new ImmutablePropertyBag(new HashMap<String, Object>());
        this.publish(properties, data);
    }

    public void close() throws StreamException {
        this.stream.close();
        this.logger.debug("stream provider connection terminated successfully.");
    }

    private void sdkCall(PropertyBag bag, SdkAction callAction, byte[] data) throws StreamException {
        final AnnotationBundle annotations = this.createAnnotations(checkers, bag, data);
        this.publishAnnotations(callAction, annotations);
        this.logger.debug("data annotated and published successfully.");
    }

    /**
     * Executes all the specified annotators and returns a list of all the created annotations
     *
     * @param properties
     * @param data
     * @return
     */
    private AnnotationBundle createAnnotations(EnvironmentCheckerEntry[] checkers, PropertyBag properties, byte[] data) {
        final List<Annotation> annotations = new ArrayList<>();

        String key = this.hashProvider.derive(data);

        String tag = getTagValue(this.layer);

        // Annotate incoming data
        for (EnvironmentCheckerEntry entry : checkers) {
            annotations.add(createAnnotation(entry.type(), entry.checker(), properties, tag, data));
        }

        return new AnnotationBundle(annotations, key, hash, layer, Instant.now());
    }

    private Annotation createAnnotation(AnnotationType type, EnvironmentChecker checker, PropertyBag bag, String tag, byte[] data) {
        boolean isSatisfied;
        try {
            isSatisfied = checker.isSatisfied(bag, data);
        } catch (AnnotatorException e) {
            logger.error(String.format("Error during %s execution", type), e);
            isSatisfied = false;
        }

        // TODO didnt really understood how the tag could be used so I let it in the annotations
        return new Annotation(type, isSatisfied, tag);
    }

    /**
     * Wraps the annotation list with a publish wrapper that specifies the SDK action and the
     * content type
     *
     * @param action
     * @param bundle
     * @throws StreamException
     */
    private void publishAnnotations(SdkAction action, AnnotationBundle bundle)
            throws StreamException {

        SignedAnnotationBundle signedBundle;
        try {
            signedBundle = AnnotationSigner.signBundle(signature.getPrivateKey(), bundle);
        } catch (AnnotatorException e) {
            throw new RuntimeException(e); //FIXME got lazy to handle it
        }

        // publish list of annotations to the StreamProvider
        final PublishWrapper wrapper = new PublishWrapper(
                action,
                bundle.getClass().getName(),
                signedBundle
        );

        this.stream.publish(wrapper);
    }


    private String getTagValue(LayerType layer) {
        switch (layer) {
            case Application:
                return System.getenv(TAG_ENV_KEY) == null ? "" : System.getenv(TAG_ENV_KEY);
            default:
                break;
        }
        return "";
    }
}
