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

import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignException;
import com.alvarium.sign.Signer;
import org.apache.http.client.methods.HttpUriRequest;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Date;

public class Ed2551RequestHandler implements RequestHandler {

	private final HttpUriRequest request;

	public Ed2551RequestHandler(HttpUriRequest request) {
		this.request = request;
	}

	public void addSignatureHeaders(Date ticks, String[] fields, Signer signer, KeyInfo publicKey)
			throws RequestHandlerException {

		if (fields.length == 0) {
			throw new RequestHandlerException("No fields found to be used in generating the seed");
		}

		// This will be the value returned for populating the Signature-Input header
		StringBuilder headerValue = new StringBuilder();
		for (int i = 0; i < fields.length - 1; i++) {
			headerValue.append(String.format("\"%s\" ", fields[i]));
		}

		headerValue.append(String.format("\"%s\"", fields[fields.length - 1]));

		String timeInSeconds = Long.toString((ticks.getTime() / 1000));
		String tail = String.format(
				";created=%s;keyid=\"%s\";alg=\"%s\";",
				timeInSeconds,
				Paths.get(publicKey.getPath()).getFileName(),
				publicKey.getType().getValue());

		headerValue.append(tail);

		request.setHeader("Signature-Input", headerValue.toString());

		ParseResult parseResult;
		try {
			parseResult = new ParseResult(request);
		} catch (URISyntaxException e) {
			throw new RequestHandlerException("Invalid request URI", e);

		} catch (ParseResultException e1) {
			throw new RequestHandlerException("Error parsing the request", e1);
		}

		// This will be the value used as input for the signature
		String inputValue = parseResult.getSeed();


		String signature;
		try {
			signature = signer.sign(inputValue.getBytes());
		} catch (SignException e) {
			throw new RequestHandlerException("Cannot sign annotation.", e);
		}
		request.setHeader("Signature", signature);
	}
}
