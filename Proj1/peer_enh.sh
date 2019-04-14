#!/bin/sh
java -classpath bin service.Peer "$1" "$2" 1 224.0.0.0/8080 225.0.0.0/8080 226.0.0.0/8080
