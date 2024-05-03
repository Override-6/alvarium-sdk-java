
/*******************************************************************************
 * Copyright 2022 Dell Inc.
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

import com.alvarium.annotators.http.ParseResult;
import com.alvarium.annotators.http.ParseResultException;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.PropertyBag;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

class PkiHttpAnnotator extends AbstractPkiAnnotator implements EnvironmentChecker {
    private final SignatureInfo signature;

    protected PkiHttpAnnotator(SignatureInfo signature, Logger logger) {
        super(logger);
        this.signature = signature;
    }

    public boolean isSatisfied(PropertyBag ctx, byte[] data) throws AnnotatorException {

        HttpUriRequest request;
        try {
            request = ctx.getProperty(AnnotationType.PKIHttp.name(), HttpUriRequest.class);
        } catch (IllegalArgumentException e) {
            throw new AnnotatorException(String.format("Property %s not found", AnnotationType.PKIHttp.name()));
        }
        ParseResult parsed;
        try {
            parsed = new ParseResult(request);
        } catch (URISyntaxException e) {
            throw new AnnotatorException("Invalid request URI", e);
        } catch (ParseResultException e) {
            throw new AnnotatorException("Error parsing the request", e);
        }
        final Signable signable = new Signable(parsed.getSeed(), parsed.getSignature());

        // Use the parsed request to obtain the key name and type we should use to
        // validate the signature
        Path path = Paths.get(signature.getPublicKey().getPath());
        Path directory = path.getParent();
        String publicKeyPath = String.join("/", directory.toString(), parsed.getKeyid());

        SignType alg;
        try {
            alg = SignType.fromString(parsed.getAlgorithm());
        } catch (EnumConstantNotPresentException e) {
            throw new AnnotatorException("Invalid key type " + parsed.getAlgorithm());
        }
        KeyInfo publicKey = new KeyInfo(publicKeyPath, alg);
        SignatureInfo sig = new SignatureInfo(publicKey, signature.getPrivateKey());

        return verifySignature(sig.getPublicKey(), signable);
    }

}
