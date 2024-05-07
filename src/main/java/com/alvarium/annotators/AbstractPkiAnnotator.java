package com.alvarium.annotators;

import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignException;
import com.alvarium.sign.SignProvider;
import com.alvarium.sign.SignProviderFactory;
import com.alvarium.utils.Encoder;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

abstract class AbstractPkiAnnotator extends AbstractAnnotator {
    AbstractPkiAnnotator(Logger logger) {
        super(logger);
    }

    /**
     * Responsible for verifying the signature, returns true if the verification
     * passed, false otherwise.
     *
     * @param key      The public key used to verify the signature
     * @param signable Contains the data (seed) and signature
     * @return True if signature valid, false otherwise
     * @throws AnnotatorException
     */
    protected Boolean verifySignature(KeyInfo key, Signable signable) throws AnnotatorException {
        final SignProviderFactory signFactory = new SignProviderFactory();
        final SignProvider signProvider;


        try {
            String privateKey = Files.readString(Paths.get(key.getPath()), StandardCharsets.US_ASCII);
            signProvider = signFactory.getProvider(Encoder.hexToBytes(privateKey), key.getType());
        } catch (SignException | IOException e) {
            throw new AnnotatorException("Could not instantiate signing provider", e);
        }

        try {
            // Load public key
            final String publicKeyPath = key.getPath();
            final String publicKey = Files.readString(
                    Paths.get(publicKeyPath),
                    StandardCharsets.US_ASCII);

            // Verify signature
            signProvider.verify(
                    Encoder.hexToBytes(publicKey),
                    signable.getSeed().getBytes(),
                    Encoder.hexToBytes(signable.getSignature()));

            return true;
        } catch (SignException e) {
            return false;
        } catch (IOException e) {
            throw new AnnotatorException("Failed to load public key", e);
        }

    }
}
