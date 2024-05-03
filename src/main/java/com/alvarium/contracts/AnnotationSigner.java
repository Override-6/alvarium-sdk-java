package com.alvarium.contracts;

import com.alvarium.annotators.AnnotatorException;
import com.alvarium.sign.KeyInfo;
import com.alvarium.sign.SignException;
import com.alvarium.sign.SignProvider;
import com.alvarium.sign.SignProviderFactory;
import com.alvarium.utils.Encoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AnnotationSigner {

    /**
     * returns the signature of the given annotation object after converting it to its json
     * representation
     *
     * @param keyInfo
     * @return
     * @throws AnnotatorException
     */
    public static SignedAnnotationBundle signBundle(KeyInfo keyInfo, AnnotationBundle bundle) throws
        AnnotatorException {
        final SignProviderFactory signFactory = new SignProviderFactory();

        try {
            final SignProvider provider = signFactory.getProvider(keyInfo.getType());
            final String key = Files.readString(Paths.get(keyInfo.getPath()),
                StandardCharsets.US_ASCII);
            byte[] signed = getBundleMinimizedString(bundle).getBytes();
            String signature = provider.sign(Encoder.hexToBytes(key), signed);
            return new SignedAnnotationBundle(signature, bundle);
        } catch (SignException e) {
            throw new AnnotatorException("cannot sign annotation.", e);
        } catch (IOException e) {
            throw new AnnotatorException("cannot read key.", e);
        }
    }

    private static String getBundleMinimizedString(AnnotationBundle bundle) {
        var sb = new StringBuilder();
        sb.append(bundle.getKey())
            .append(bundle.getHash())
            .append(bundle.getLayer())
            .append(bundle.getTimestamp());

        for (Annotation annotation : bundle.getAnnotations()) {
            sb.append(annotation.identityString());
        }

        return sb.toString();
    }
}
