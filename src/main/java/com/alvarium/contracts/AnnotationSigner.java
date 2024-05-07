package com.alvarium.contracts;

import com.alvarium.annotators.AnnotatorException;
import com.alvarium.sign.SignException;
import com.alvarium.sign.SignProvider;

public class AnnotationSigner {

    /**
     * returns the signature of the given annotation object after converting it to its json
     * representation
     *
     * @return
     * @throws AnnotatorException
     */
    public static SignedAnnotationBundle signBundle(SignProvider provider,
                                                    AnnotationBundle bundle) throws
            AnnotatorException {
        try {
            byte[] signed = getBundleMinimizedString(bundle).getBytes();
            String signature = provider.sign(signed);
            return new SignedAnnotationBundle(signature, bundle);
        } catch (SignException e) {
            throw new AnnotatorException("cannot sign annotation.", e);
        }
    }

    //TODO ask: is it possible to sign a hash instead ?
    private static String getBundleMinimizedString(AnnotationBundle bundle) {
        var sb = new StringBuilder();
        sb.append(bundle.key())
                .append(bundle.hash())
                .append(bundle.layer())
                .append(bundle.timestamp());

        for (Annotation annotation : bundle.annotations()) {
            sb.append(annotation.identityString());
        }

        return sb.toString();
    }
}
