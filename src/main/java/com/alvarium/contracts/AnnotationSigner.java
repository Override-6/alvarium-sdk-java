package com.alvarium.contracts;

import com.alvarium.annotators.AnnotatorException;
import com.alvarium.sign.SignException;
import com.alvarium.sign.Signer;

public class AnnotationSigner {

    /**
     * returns the signature of the given annotation object after converting it to its json
     * representation
     *
     * @return
     * @throws AnnotatorException
     */
    public static SignedAnnotationBundle signBundle(Signer signer, AnnotationBundle bundle) throws
        AnnotatorException {

        try {
            byte[] signed = getBundleMinimizedString(bundle).getBytes();
            String signature = signer.sign(signed);
            return new SignedAnnotationBundle(signature, bundle);
        } catch (SignException e) {
            throw new AnnotatorException("cannot sign annotation.", e);
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
