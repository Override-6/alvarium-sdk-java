/*******************************************************************************
 * Copyright 2023 Dell Inc.
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
package com.alvarium.annotators;

import com.alvarium.contracts.AnnotationType;
import com.alvarium.hash.HashProvider;
import com.alvarium.hash.HashProviderFactory;
import com.alvarium.hash.HashType;
import com.alvarium.hash.HashTypeException;
import com.alvarium.utils.PropertyBag;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ChecksumChecker extends AbstractChecker implements EnvironmentChecker {


    private HashProvider hashProvider;

    protected ChecksumChecker(HashType hash, Logger logger) throws AnnotatorException {
        super(logger);
        this.initHashProvider(hash);
    }

    @Override
    public boolean isSatisfied(PropertyBag ctx, byte[] data) throws AnnotatorException {
        final ChecksumAnnotatorProps props = ctx.getProperty(
                AnnotationType.CHECKSUM.name(),
                ChecksumAnnotatorProps.class
        );

        // Get artifact checksum
        final String checksum = this.readFile(props.getChecksumPath());

        // Validate artifact checksum
        final String artifactHash = this.hashFile(props.getArtifactPath());

        return checksum.equals(artifactHash);
    }

    /**
     * Initializes a hash provider
     *
     * @return HashProvider
     * @throws AnnotatorException - If hashing algorithm not found,
     *                            or if an unknown exception was thrown
     */
    private void initHashProvider(HashType hashType) throws AnnotatorException {
        try {
            this.hashProvider = new HashProviderFactory().getProvider(hashType);
        } catch (HashTypeException e) {
            throw new AnnotatorException("Hashing algorithm not found, could not hash data or validate checksum", e);
        } catch (Exception e) {
            throw new AnnotatorException("Could not hash data or validate checksum", e);
        }
    }

    /**
     * Reads a file on the local file system
     *
     * @param filePath
     * @return String content of file
     * @throws AnnotatorException - When bad file path or corrupted file given
     */
    private String readFile(String filePath) throws AnnotatorException {
        final String content;
        try {
            content = Files.readString(
                    Paths.get(filePath),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new AnnotatorException("Failed to read file, could not validate checksum", e);
        }
        return content;
    }

    /**
     * Reads and hashes a file on the local file system in in chunks of 8KB
     *
     * @param filePath
     * @return hash of the file's contents in string format
     * @throws AnnotatorException - When bad file path or corrupted file given
     */
    private String hashFile(String filePath) throws AnnotatorException {
        try {
            FileInputStream fs = new FileInputStream(filePath);
            final byte[] buffer = new byte[8192];

            int bytesRead = 0;
            while (true) {
                bytesRead = fs.read(buffer);
                if (bytesRead == -1) { // indicates EOF
                    break;
                } else {
                    this.hashProvider.update(buffer, 0, bytesRead);
                }
            }

            fs.close();
        } catch (IOException e) {
            throw new AnnotatorException(
                    "Failed to hash artifact, could not validate checksum",
                    e
            );
        }

        return this.hashProvider.getValue();
    }
}
