#!/bin/sh
cwd=$(cd $(dirname $0); pwd)
echo $cwd
$cwd/bin/java -cp $cwd/ImageEffectorAP-1.0-SNAPSHOT.jar --add-modules jdk.incubator.vector -Djava.awt.headless=true net.javainthebox.imageeffector.service.ImageEffectorService 8000
