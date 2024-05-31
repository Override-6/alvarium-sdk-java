/*******************************************************************************
 * Copyright 2022 Dell Inc.
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
package com.alvarium.annotators.http;

import java.util.Date;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

import com.alvarium.contracts.DerivedComponent;

import com.alvarium.sign.*;

import org.apache.http.entity.StringEntity;

import org.apache.http.client.methods.HttpPost;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ed2551RequestHandlerTest {
        HttpPost getRequest() {
                HttpPost request = new HttpPost(URI.create("http://example.com/foo?var1=&var2=2"));
                request.setHeader("Date", "Tue, 20 Apr 2021 02:07:55 GMT");
                request.setHeader("Content-Type", "application/json");
                request.setHeader("Content-Length", "10");
                return request;
        }

        @Test
        public void addSignatureHeadersShouldExecuteCorrectly() throws IOException, RequestHandlerException, SignException {

                final KeyInfo pubKey = new KeyInfo("./src/test/java/com/alvarium/annotators/public.key",
                                SignType.Ed25519);

                final KeyInfo privKey = new KeyInfo("./src/test/java/com/alvarium/annotators/private.key",
                                SignType.Ed25519);

                final SignatureInfo sigInfo = new SignatureInfo(pubKey, privKey);

                Date date = new Date();
                HttpPost request = getRequest();
                request.setEntity(new StringEntity("{key: \"test\"}"));

                String[] fields = { DerivedComponent.METHOD.getValue(),
                                DerivedComponent.PATH.getValue(),
                                DerivedComponent.AUTHORITY.getValue(),
                                "Content-Type", "Content-Length" };

                Ed2551RequestHandler requestHandler = new Ed2551RequestHandler(request);
                requestHandler.addSignatureHeaders(date, fields, SignProviderFactories.getSigner(sigInfo.getPrivateKey()), sigInfo.getPublicKey());
                assertEquals(request.getHeaders("Signature-Input")[0].getValue(),
                                String.format(
                                                "\"@method\" \"@path\" \"@authority\" \"Content-Type\" \"Content-Length\";created=%s;keyid=\"%s\";alg=\"%s\";",
                                                date.getTime() / 1000,
                                                Paths.get(sigInfo.getPublicKey().getPath()).getFileName(),
                                                sigInfo.getPublicKey().getType().getValue()));

        }

        @Test(expected = RequestHandlerException.class)
        public void addSignatureHeadersShouldThrowException() throws IOException, RequestHandlerException, SignException {

                final KeyInfo pubKey = new KeyInfo("./src/test/java/com/alvarium/annotators/public.key",
                                SignType.Ed25519);

                final KeyInfo privKey = new KeyInfo("./src/test/java/com/alvarium/annotators/private.key",
                                SignType.Ed25519);

                final SignatureInfo sigInfo = new SignatureInfo(pubKey, privKey);

                Date date = new Date();
                HttpPost request = getRequest();
                request.setEntity(new StringEntity("{key: \"test\"}"));

                String[] fields = {};

                Ed2551RequestHandler requestHandler = new Ed2551RequestHandler(request);
                requestHandler.addSignatureHeaders(date, fields, SignProviderFactories.getSigner(sigInfo.getPrivateKey()), sigInfo.getPublicKey());
        }
}
