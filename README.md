# Vector API samples for Oracle Code Tokyo 2019

There are two samples using Vector API of Project Panama.

* ImageEffectorDT: Image processing desktop application for comparing Vector API with conventional way.
* ImageEffectorAP: Web service for image processing using Vector API

There are no early access of Vector API at the present (Aug. 2019), so you should build Vector API.

    > hg clone http://hg.openjdk.java.net/panama/dev
    > cd dev
    > hg update -r vectorIntrinsics
    > sh configure  
    > make images

## ImageEffectorDT

ImageEffectorDT is a desktop application for comparing Vector API with sequential loop. It uses JavaFX, and shows blured images in two ways.

It needs JavaFX SDK https://gluonhq.com/products/javafx/ . 

Building ImageEffectorDT is following.

    > mvn install

run.sh is a shell script for running ImageEffectorDT. run.sh describes the location of JavaFX SDK is /opt/java/javafx-sdk-12. If you uses the different directory or SDK version, you edit it.

    > sh run.sh

Then, ImageEffectorAPI shows two panes: left is by sequential calculate, and right is by Vector API. Push "Start" button, it starts blur processing.

## ImageEffectorAP

ImageEffectorAP is a web service of blur image processing.

If you send POST message (Content-Type image/jpg or image/png, and body is image), ImageEffectorAP returns blured image.

Building ImageEffectorAP by Maven

    > mvn install
    
startService.sh starts web service. Default port number is 8000. You can edit as needed.

    > sh startService.sh

For testing, client application (ImageEffectorClient) is included. ImageEffectorClient uses test.png (sorry, it's hard coded), and result is result.png.

    > java -cp target/ImageEffectorAP-1.0-SNAPSHOT.jar net.javainthebox.imageeffector.service.ImageEffectorClient


