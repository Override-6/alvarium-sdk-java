
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SignProviderFactory {

  public SignProvider getProvider(KeyInfo privateKey) throws SignException, IOException {
    final String key = Files.readString(Paths.get(privateKey.getPath()), StandardCharsets.US_ASCII);
    switch(privateKey.getType()) {
      case Ed25519:
        return new Ed25519Provider(Encoder.hexToBytes(key));
      default:
        throw new SignException("Concrete type not found: " + privateKey.getType(), null);
    }
  }

}