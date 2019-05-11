#!/bin/sh
cwd=$(cd $(dirname $0); pwd)
echo $cwd
$cwd/bin/java -Xms10g -Xmx10g -Xlog:gc*,safepoint:file=gc.log:time,pid,level,tags:filesize=2,filesize=512m -cp $cwd/ImageEffectorAP-1.0-SNAPSHOT.jar --add-modules jdk.incubator.vector -Djava.awt.headless=true net.javainthebox.imageeffector.service.ImageEffectorService 8000 16
