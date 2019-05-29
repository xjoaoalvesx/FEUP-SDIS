#!/bin/sh
rm -rf bin
mkdir -p bin
javac -Xlint:unchecked -d bin -sourcepath src src/service/*.java
rm -rf peers/*
