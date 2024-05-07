package com.alvarium.contracts;

import com.alvarium.PublishWrapper;
import com.alvarium.annotators.AnnotatorException;
import com.alvarium.serializers.AnnotationConverter;
import com.alvarium.serializers.PublishWrapperConverter;
import com.alvarium.serializers.SignedAnnotationBundleConverter;
import com.alvarium.serializers.ZonedDateTimeConverter;
import com.alvarium.sign.SignException;
import com.alvarium.sign.SignProvider;
import com.google.gson.GsonBuilder;

import java.time.ZonedDateTime;

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
            byte[] signed = getBundleIdentityString(bundle).getBytes();
//            byte[] signed = getBundleJsonString(bundle).getBytes();
            String signature = provider.sign(signed);
            return new SignedAnnotationBundle(signature, bundle);
        } catch (SignException e) {
            throw new AnnotatorException("cannot sign annotation.", e);
        }
    }

    private static String getBundleIdentityString(AnnotationBundle bundle) {
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
//
//    private static String getBundleJsonString(AnnotationBundle bundle) {
//        var gson = new GsonBuilder()
//                .registerTypeAdapter(PublishWrapper.class, new PublishWrapperConverter())
//                .registerTypeAdapter(SignedAnnotationBundle.class, new SignedAnnotationBundleConverter())
//                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeConverter())
//                .registerTypeAdapter(Annotation.class, new AnnotationConverter())
//                .disableHtmlEscaping()
//                .create();
//
//        return gson.toJson(bundle);
//    }
}
