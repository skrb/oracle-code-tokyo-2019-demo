#!/bin/sh

java -cp target/ImageEffectorAP-1.0-SNAPSHOT.jar --add-modules jdk.incubator.vector -Djava.awt.headless=true net.javainthebox.imageeffector.service.ImageEffectorService 8000
