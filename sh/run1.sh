#!/bin/bash

rm -rf run1
mkdir run1
cd run1

unzip -q ../../target/universal/reactive-board*
reactive-board*/bin/reactive-board
