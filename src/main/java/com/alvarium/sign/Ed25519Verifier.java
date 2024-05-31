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
package com.alvarium.sign;

import com.alvarium.utils.Encoder;
import com.google.crypto.tink.subtle.Ed25519Verify;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;


public class Ed25519Verifier implements SignatureVerifier {

    private final Ed25519Verify verifier;

    protected Ed25519Verifier(KeyInfo publicKey) throws IOException {
        final String publicKeyString = Files.readString(
            Paths.get(publicKey.getPath()),
            StandardCharsets.US_ASCII
        );

        verifier = new Ed25519Verify(Encoder.hexToBytes(publicKeyString));
    }

    public void verify(byte[] content, byte[] signed) throws SignException {
        try {
            verifier.verify(signed, content);
        } catch (GeneralSecurityException e) {
            throw new SignException("Verification did not pass", e);
        }
    }

}
