#!/usr/bin/env bash

set -xeu

mvn package

cp target/alvarium-sdk-1.0-SNAPSHOT-jar-with-dependencies.jar ../alvarium-complete/lib/