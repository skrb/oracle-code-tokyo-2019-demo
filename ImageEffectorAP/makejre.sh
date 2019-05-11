#!/bin/sh

jlink --add-modules java.base,java.desktop,java.net.http,jdk.httpserver,jdk.incubator.vector --compress=2 --output jre
cp target/ImageEffectorAP-1.0-SNAPSHOT.jar jre
cp startServiceJRE.sh jre

