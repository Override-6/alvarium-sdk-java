package com.alvarium.annotators;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;

import com.alvarium.contracts.Annotation;
import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashProviderFactory;
import com.alvarium.hash.HashType;
import com.alvarium.hash.HashTypeException;
import com.alvarium.sign.SignatureInfo;
import com.alvarium.utils.PropertyBag;

/**
 * a dummy annotator to be used in unit tests
 */
class MockAnnotator implements Annotator {
  private final HashType hash;
  private final AnnotationType kind;
  private final SignatureInfo signature;

  protected MockAnnotator(HashType hash, AnnotationType kind, SignatureInfo signature) {
    this.hash = hash;
    this.kind = kind;
    this.signature = signature;
  }

  public Annotation execute(PropertyBag ctx, byte[] data) throws AnnotatorException {
    final HashProviderFactory hashFactory = new HashProviderFactory();
    try {
      final String key = hashFactory.getProvider(hash).derive(data);
      final String host = InetAddress.getLocalHost().getHostName();
      final String sig = signature.getPublicKey().getType().toString();

      final Annotation annotation = new Annotation(key, hash, host, kind, sig, true, Instant.now());
      return annotation;
    } catch (HashTypeException e) {
      throw new AnnotatorException("failed to hash data", e);
    } catch (UnknownHostException e) {
      throw new AnnotatorException("Could not get hostname", e);
    }
  } 
}