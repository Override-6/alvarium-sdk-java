package com.alvarium.sign;

public interface Signer {

    /**
     * Uses a private key to sign content and returns the signature in hex format stored in a string
     * <p>
     * The key parameter needs to be 64-byte in size where the private key is suffixed by
     * the public key (Some implementations will accept and use only the first 32-bytes of the
     * private key such as Ed25519)
     *
     * @param content
     * @return signature
     * @throws SignException
     */
    String sign(byte[] content) throws SignException;

}
