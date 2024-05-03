
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

import com.alvarium.utils.PropertyBag;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A unit used to provide lineage from one version of data to another as a result of
 * change or transformation
 */
class SourceAnnotator extends AbstractAnnotator implements EnvironmentChecker {

    protected SourceAnnotator(Logger logger) {
        super(logger);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean isSatisfied(PropertyBag ctx, byte[] data) throws AnnotatorException {
        // check if we can retrieve the hostname
        try {
            InetAddress.getLocalHost();
            return true;
        } catch (UnknownHostException e) {
            this.logger.error("Error during SourceAnnotator execution: ", e);
            return false;
        }
    }
}
