
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
package com.alvarium.streams;

import com.alvarium.PublishWrapper;

/**
 * A dummy stream provider that does not throw any errors
 * Mainly used for unit tests
 */
class MockStreamProvider implements StreamProvider {
  public void connect() {
    //System.out.println("stream connected");
  }
  
  public void close() {
    //System.out.println("stream closed");
  }
  
  public void publish(PublishWrapper wrapper) {
    //System.out.println(String.format("%s publish", wrapper.toJson()));
  }
}
