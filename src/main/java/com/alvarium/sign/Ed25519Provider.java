
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
import com.google.crypto.tink.subtle.Ed25519Verify;

import java.security.GeneralSecurityException;
import java.util.Arrays;


public class Ed25519Provider implements SignProvider {

  private final Ed25519Sign signer;

  protected Ed25519Provider(byte[] key) {
    final byte[] privateKey = Arrays.copyOfRange(key, 0, 32);
    try {
      this.signer = new Ed25519Sign(privateKey);
    } catch (GeneralSecurityException e) {
      throw new RuntimeException("SHA-512 not defined in EngineFactory.MESSAGE_DIGEST", e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Invalid signing key", e);
    } catch (Exception e) {
      throw new RuntimeException("Could not instantiate Ed25519Provider", e);
    }
  }

  public String sign(byte[] content) throws SignException {
    // Private key passed as private key and public key appended to it
    // so the private key of size 32-bytes is extracted
    try {
      final byte[] signed = signer.sign(content);
      return Encoder.bytesToHex(signed);
    } catch (Exception e) {
      throw new SignException("Could not sign data", e);
    }
  }

  public void verify(byte[] key, byte[] content, byte[] signed) throws SignException {
    try {
      final Ed25519Verify verifier = new Ed25519Verify(key);
      verifier.verify(signed, content);
    } catch (GeneralSecurityException e) {
      throw new SignException("Verification did not pass", e);
    } catch (IllegalArgumentException e) {
      throw new SignException("Invalid signing key", e);
    } catch (Exception e) {
      throw new SignException("Could not verify signature", e);
    }
  }

}
