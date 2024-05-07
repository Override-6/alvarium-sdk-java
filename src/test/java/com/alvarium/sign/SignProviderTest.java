
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
import com.google.crypto.tink.subtle.Ed25519Sign;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class SignProviderTest {

    @Test(expected = SignException.class)
    public void factoryShouldReturnNoConcreteTypesError() throws SignException, IOException {
        final SignProviderFactory factory = new SignProviderFactory();
        String key = Files.readString(Paths.get("./src/test/java/com/alvarium/sign/private.key"), StandardCharsets.US_ASCII);
        factory.getProvider(Encoder.hexToBytes(key), SignType.none);
    }

    @Test
    public void factoryShouldReturnEd25519() throws SignException, IOException {
        final SignProviderFactory factory = new SignProviderFactory();
        String key = Files.readString(Paths.get("./src/test/java/com/alvarium/sign/private.key"), StandardCharsets.US_ASCII);
        final SignProvider ed25519Provider = factory.getProvider(Encoder.hexToBytes(key), SignType.Ed25519);
        assertEquals(ed25519Provider.getClass(), Ed25519Provider.class);
    }

    @Test
    public void signAndVerifyShouldVerifyTrue() throws Exception {
        final Ed25519Sign.KeyPair keyPair = Ed25519Sign.KeyPair.newKeyPair();
        final byte[] privateKey = keyPair.getPrivateKey();
        final byte[] publicKey = keyPair.getPublicKey();

        byte[] content = "hello".getBytes();

        final SignProviderFactory factory = new SignProviderFactory();

        final SignProvider signProvider = factory.getProvider(privateKey, SignType.Ed25519);

        final String signedString = signProvider.sign(content);
        final byte[] signed = Encoder.hexToBytes(signedString);
        signProvider.verify(publicKey, content, signed);
    }

    @Test(expected = SignException.class)
    public void signAndVerifyShouldVerifyFalse() throws Exception {
        final Ed25519Sign.KeyPair keyPair = Ed25519Sign.KeyPair.newKeyPair();
        final Ed25519Sign.KeyPair wrongKeyPair = Ed25519Sign.KeyPair.newKeyPair();
        final byte[] privateKey = keyPair.getPrivateKey();
        final byte[] wrongPublicKey = wrongKeyPair.getPublicKey();

        byte[] content = "foo".getBytes();

        final SignProviderFactory factory = new SignProviderFactory();
        final SignProvider signProvider = factory.getProvider(privateKey, SignType.Ed25519);

        final String signedString = signProvider.sign(content);
        final byte[] signed = Encoder.hexToBytes(signedString);
        signProvider.verify(wrongPublicKey, content, signed);
    }

    @Test
    public void signWithProvidedKeyFilesShouldVerifyTrue() throws Exception {

        // Load keys from files
        String pirvateKeyPath = "./src/test/java/com/alvarium/sign/private.key";
        String publicKeyPath = "./src/test/java/com/alvarium/sign/public.key";
        final String privateKey = Files.readString(Paths.get(pirvateKeyPath), StandardCharsets.US_ASCII);
        final String publicKey = Files.readString(Paths.get(publicKeyPath), StandardCharsets.US_ASCII);

        // Decode keys into bytes
        final byte[] privateKeyDecoded = Encoder.hexToBytes(privateKey);
        final byte[] publicKeyDecoded = Encoder.hexToBytes(publicKey);

        byte[] content = "foo".getBytes();

        final SignProviderFactory factory = new SignProviderFactory();
        final SignProvider signProvider = factory.getProvider(privateKeyDecoded, SignType.Ed25519);

        final String signedString = signProvider.sign(content);
        final byte[] signed = Encoder.hexToBytes(signedString);
        signProvider.verify(publicKeyDecoded, content, signed);
    }

}
