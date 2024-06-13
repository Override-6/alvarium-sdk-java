/*******************************************************************************
* Copyright 2024 Dell Inc.
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
package com.alvarium.annotators.sbom;

import com.alvarium.annotators.AnnotatorConfig;
import com.alvarium.contracts.AnnotationType;

public class SbomAnnotatorConfig extends AnnotatorConfig {
  private final SbomType type;
  private final String version;

  SbomAnnotatorConfig(SbomType type, String version) {
    super(AnnotationType.SBOM);
    this.type = type;
    this.version = version;
  }

  public SbomType getSbomType() {
    return this.type;
  }

  public String getSbomVersion() {
    return this.version;
  }
}
