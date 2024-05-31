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
import com.google.crypto.tink.PublicKeySign;
import com.google.crypto.tink.subtle.Ed25519Sign;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;


public class Ed25519Signer implements Signer {

    private final PublicKeySign signer;

    protected Ed25519Signer(KeyInfo privateKey) throws SignException, IOException {
        final String privateKeyString = Files.readString(
            Paths.get(privateKey.getPath()),
            StandardCharsets.US_ASCII
        );
        // Private key passed as private key and public key appended to it
        // so the private key of size 32-bytes is extracted
        final byte[] privateKeyBytes = Arrays.copyOfRange(Encoder.hexToBytes(privateKeyString), 0, 32);

        try {
            signer = new Ed25519Sign(privateKeyBytes);
        } catch (GeneralSecurityException e) {
            throw new SignException("SHA-512 not defined in EngineFactory.MESSAGE_DIGEST", e);
        } catch (IllegalArgumentException e) {
            throw new SignException("Invalid signing key", e);
        } catch (Exception e) {
            throw new SignException("Could not instantiate Ed25519Provider", e);
        }
    }

    public String sign(byte[] content) throws SignException {
        try {
            final byte[] signed = signer.sign(content);
            return Encoder.bytesToHex(signed);
        } catch (Exception e) {
            throw new SignException("Could not sign data", e);
        }
    }

}
