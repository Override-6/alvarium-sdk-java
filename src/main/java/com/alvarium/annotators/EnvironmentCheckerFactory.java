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
package com.alvarium.annotators;

import com.alvarium.SdkInfo;
import com.alvarium.annotators.sbom.SbomAnnotatorConfig;
import com.alvarium.annotators.vulnerability.VulnerabilityAnnotatorConfig;
import com.alvarium.hash.HashType;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.sign.SignatureVerifier;
import org.apache.logging.log4j.Logger;

public class EnvironmentCheckerFactory {

    public static EnvironmentChecker getChecker(AnnotatorConfig cfg, SdkInfo config, Logger logger, SignatureVerifier verifier) throws AnnotatorException {
        final HashType hash = config.getHash().getType();
        final SignatureInfo signature = config.getSignature();
        switch (cfg.getKind()) {
            case MOCK:
                try {
                    MockAnnotatorConfig mockCfg = (MockAnnotatorConfig) cfg;
                    return new MockAnnotator(mockCfg);
                } catch (ClassCastException e) {
                    throw new AnnotatorException("Invalid annotator config", e);
                }
            case TLS:
                return new TlsChecker(logger);
            case PKI:
                return new PkiChecker(logger, verifier);
            case PKIHttp:
                return new PkiHttpChecker(logger, signature);
            case TPM:
                return new TpmChecker(logger);
            case SourceCode:
                return new SourceCodeChecker(hash, logger);
            case CHECKSUM:
                return new ChecksumChecker(hash, logger);
            case VULNERABILITY:
                VulnerabilityAnnotatorConfig vulnCfg = (VulnerabilityAnnotatorConfig) cfg;
                return new VulnerabilityChecker(vulnCfg, logger);
            case SOURCE:
                return new SourceChecker(logger);
            case SBOM:
                final SbomAnnotatorConfig sbomCfg = (SbomAnnotatorConfig) cfg;
                return new SbomChecker(sbomCfg, logger);
            default:
                throw new AnnotatorException("Annotator type is not supported");
        }
    }
}
