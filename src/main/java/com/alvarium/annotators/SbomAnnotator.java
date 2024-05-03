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

import com.alvarium.annotators.sbom.SbomAnnotatorConfig;
import com.alvarium.annotators.sbom.SbomException;
import com.alvarium.annotators.sbom.SbomProvider;
import com.alvarium.annotators.sbom.SbomProviderFactory;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.utils.PropertyBag;
import org.apache.logging.log4j.Logger;

public class SbomAnnotator extends AbstractAnnotator implements EnvironmentChecker {

    private final SbomProvider sbom;

    protected SbomAnnotator(SbomAnnotatorConfig cfg, Logger logger) throws AnnotatorException {
        super(logger);
        try {
            this.sbom = new SbomProviderFactory().getProvider(cfg, this.logger);
        } catch (SbomException e) {
            throw new AnnotatorException("Could not initialize SBOM provider", e);
        }
    }

    @Override
    public boolean isSatisfied(PropertyBag ctx, byte[] data) throws AnnotatorException {
        try {
            final String filePath = ctx.getProperty(AnnotationType.SBOM.name(), String.class);

            boolean isValid = sbom.validate(filePath);
            boolean exists = sbom.exists(filePath);
            boolean matchesBuild = sbom.matchesBuild(filePath, ".");

            return isValid && exists && matchesBuild;
        } catch (SbomException e) {
            this.logger.error("Error during SbomAnnotator execution: ", e);
            return false;
        }
    }
}
