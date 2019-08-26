#!/bin/sh

java --module-path /opt/java/javafx-sdk-12/lib --add-modules javafx.fxml,javafx.controls,jdk.incubator.vector -cp target/ImageEffectorDT-1.0-SNAPSHOT.jar net.javainthebox.imageeffector.ImageEffector
