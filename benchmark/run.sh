#!/usr/bin/env bash

cd ..
./gradlew clean jar
cd benchmark

./gradlew clean shadowJar

java -jar build/libs/omg-benchmark.jar