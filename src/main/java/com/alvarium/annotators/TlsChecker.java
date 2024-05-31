
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

import com.alvarium.contracts.AnnotationType;
import com.alvarium.utils.PropertyBag;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLSocket;

public class TlsChecker extends AbstractChecker implements EnvironmentChecker {
  public TlsChecker(Logger logger) {
    super(logger);
  }

  private Boolean verifyHandshake(SSLSocket socket) {
    // a call to getSession tries to set up a session if there is no currently valid
    // session, and an implicit handshake is done.
    // If handshaking fails for any reason, the SSLSocket is closed, and no futher
    // communications can be done. 
    // from: https://docs.oracle.com/javase/7/docs/api/javax/net/ssl/SSLSocket.html
    socket.getSession();
    return !socket.isClosed();
  }

  public boolean isSatisfied(PropertyBag ctx, byte[] data) throws AnnotatorException {
    // hash incoming data
    // TLS check handshake
    return this.verifyHandshake(ctx.getProperty(AnnotationType.TLS.name(),
        SSLSocket.class));
  }
}
