package com.alvarium.sign;

public interface SignatureVerifier {
    /**
     * Verifies a signature against content using a public key
     *
     * @param content
     * @param signed
     * @throws SignException when verification does not pass
     */
    void verify(byte[] content, byte[] signed) throws SignException;
}
