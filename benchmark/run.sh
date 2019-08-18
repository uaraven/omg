#!/usr/bin/env bash

cd ..
./gradlew clean jar
cd benchmark || exit

./gradlew clean shadowJar

java -jar build/libs/omg-benchmark.jar