#!/bin/bash

rm -rf run2
mkdir run2
cd run2

unzip -q ../../target/universal/reactive-board*
reactive-board*/bin/reactive-board -Dremote.port=2553 -Dhttp.port=9001
