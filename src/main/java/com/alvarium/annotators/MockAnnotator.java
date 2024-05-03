
/*******************************************************************************
 * Copyright 2023 Dell Inc.
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

/**
 * a dummy annotator to be used in unit tests
 */
class MockAnnotator implements EnvironmentChecker {
    private final MockAnnotatorConfig cfg;


    protected MockAnnotator(MockAnnotatorConfig cfg) {
        this.cfg = cfg;
    }

    public boolean isSatisfied(PropertyBag ctx, byte[] data) {
        return cfg.getShouldSatisfy();
    }
}
