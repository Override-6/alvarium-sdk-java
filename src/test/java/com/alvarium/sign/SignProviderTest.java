
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
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class SignProviderTest {

    @Test
    public void factoryShouldReturnEd25519() throws SignException, IOException {
        Signer ed25519Provider = SignProviderFactories.getSigner(new KeyInfo(".gitignore", SignType.Ed25519));
        assertEquals(ed25519Provider.getClass(), Ed25519Signer.class);
    }

    @Test
    public void signAndVerifyShouldVerifyTrue() throws Exception {
        final Ed25519Sign.KeyPair keyPair = Ed25519Sign.KeyPair.newKeyPair();
        final byte[] privateKey = keyPair.getPrivateKey();
        final byte[] publicKey = keyPair.getPublicKey();

        Path privatePath = Files.createTempFile("alvarium", "private-key");
        Path publicPath = Files.createTempFile("alvarium", "public-key");

        Files.writeString(privatePath, Encoder.bytesToHex(privateKey));
        Files.writeString(publicPath, Encoder.bytesToHex(publicKey));

        byte[] content = "hello".getBytes();

        Signer signer = SignProviderFactories.getSigner(new KeyInfo(privatePath.toString(), SignType.Ed25519));
        SignatureVerifier verifier = SignProviderFactories.getVerifier(new KeyInfo(publicPath.toString(), SignType.Ed25519));

        final String signedString = signer.sign(content);
        final byte[] signed = Encoder.hexToBytes(signedString);
        verifier.verify(content, signed);
    }

    @Test(expected = SignException.class)
    public void signAndVerifyShouldVerifyFalse() throws Exception {
        final Ed25519Sign.KeyPair keyPair = Ed25519Sign.KeyPair.newKeyPair();
        final Ed25519Sign.KeyPair wrongKeyPair = Ed25519Sign.KeyPair.newKeyPair();
        final byte[] privateKey = keyPair.getPrivateKey();
        final byte[] wrongPublicKey = wrongKeyPair.getPublicKey();

        Path privatePath = Files.createTempFile("alvarium", "private-key");
        Path publicPath = Files.createTempFile("alvarium", "public-key");

        Files.writeString(privatePath, Encoder.bytesToHex(privateKey));
        Files.writeString(publicPath, Encoder.bytesToHex(wrongPublicKey));

        byte[] content = "foo".getBytes();

        Signer signer = SignProviderFactories.getSigner(new KeyInfo(privatePath.toString(), SignType.Ed25519));
        SignatureVerifier verifier = SignProviderFactories.getVerifier(new KeyInfo(publicPath.toString(), SignType.Ed25519));


        final String signedString = signer.sign(content);
        final byte[] signed = Encoder.hexToBytes(signedString);
        verifier.verify(content, signed);
    }

    @Test
    public void signWithProvidedKeyFilesShouldVerifyTrue() throws Exception {
        byte[] content = "foo".getBytes();

        Signer signer = SignProviderFactories.getSigner(new KeyInfo("./src/test/java/com/alvarium/sign/private.key", SignType.Ed25519));
        SignatureVerifier verifier = SignProviderFactories.getVerifier(new KeyInfo("./src/test/java/com/alvarium/sign/public.key", SignType.Ed25519));

        final String signedString = signer.sign(content);
        final byte[] signed = Encoder.hexToBytes(signedString);
        verifier.verify(content, signed);
    }


}
