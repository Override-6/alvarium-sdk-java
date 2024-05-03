
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

import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.PropertyBag;
import org.apache.logging.log4j.Logger;

class PkiAnnotator extends AbstractPkiAnnotator implements EnvironmentChecker {
    private final SignatureInfo signature;

    protected PkiAnnotator(SignatureInfo signature, Logger logger) {
        super(logger);
        this.signature = signature;
    }

    public boolean isSatisfied(PropertyBag ctx, byte[] data) throws AnnotatorException {
        final Signable signable = Signable.fromJson(new String(data));

        return verifySignature(signature.getPublicKey(), signable);
    }

}
