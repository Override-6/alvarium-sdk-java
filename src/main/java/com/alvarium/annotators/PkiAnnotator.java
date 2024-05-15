
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
package com.alvarium.annotators;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;

import org.apache.logging.log4j.Logger;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.contracts.LayerType;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.PropertyBag;

class PkiAnnotator extends AbstractPkiAnnotator implements EnvironmentChecker {
    private final HashType hash;
    private final SignatureInfo signature;
    private final AnnotationType kind;
    private final LayerType layer;

    protected PkiAnnotator(HashType hash, SignatureInfo signature, Logger logger, LayerType layer) {
        super(logger);
        this.hash = hash;
        this.signature = signature;
        this.kind = AnnotationType.PKI;
        this.layer = layer;
    }

    public boolean isSatisfied(PropertyBag ctx, byte[] data) throws AnnotatorException {
        final Signable signable = Signable.fromJson(new String(data));

        return verifySignature(signature.getPublicKey(), signable);
    }

}
