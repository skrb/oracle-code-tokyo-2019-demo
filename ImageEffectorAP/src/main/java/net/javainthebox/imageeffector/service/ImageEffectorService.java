package net.javainthebox.imageeffector.service;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import net.javainthebox.imageeffector.SoftFocusEffector;

public class ImageEffectorService implements HttpHandler {
	private static final int DEFAULT_THREAD_NUM = 8;

    public void handle(HttpExchange t) throws IOException {

	Headers headers = t.getRequestHeaders();
	var contentType = headers.get("Content-type").get(0);
	var formatType = contentType.substring(contentType.lastIndexOf('/')+1);
	System.out.println("ContentType: " + contentType);
	System.out.println("FormatType: " + formatType);
	
        var is = t.getRequestBody();
	BufferedImage image = ImageIO.read(is);
	var width = image.getWidth();
	var height = image.getHeight();
	is.close();

	System.out.println("Image: " + image);

	// Apply Soft Focusing
	int[] pixels = image.getRGB​(0, 0, width, height,
				    null, 0, width);
	System.out.println("Get Pixel: " + pixels.length + " " + pixels[0]);
	
	int[] appliedPixels = SoftFocusEffector.softenVector(pixels, width, height);
	System.out.println("Apply Pixel: " + appliedPixels[0]);
	BufferedImage appliedImage = new BufferedImage(width, height,
						       BufferedImage.TYPE_INT_ARGB);
	appliedImage.setRGB(0, 0, width, height, appliedPixels, 0, width);
	System.out.println("Applied: " + appliedImage);

	var baos = new ByteArrayOutputStream();
	ImageIO.write(appliedImage, formatType, baos);
	var buffer = baos.toByteArray();
	System.out.println("Size: " + buffer.length);
	
	t.getResponseHeaders().add("Content-type", contentType);
	t.sendResponseHeaders(200, buffer.length);
	
	OutputStream os = t.getResponseBody();
	os.write(buffer);
        os.close();
    }

    public static void main(String... args) throws Exception {
	if (args.length > 0) {
	    var port = Integer.parseInt(args[0]);
	    var threadNum = DEFAULT_THREAD_NUM;
	    if (args.length > 1) {
			threadNum = Integer.parseInt(args[1]);
		}
	    var server = HttpServer.create(new InetSocketAddress(port), 0);
	    server.createContext("/", new ImageEffectorService());
	    server.setExecutor(Executors.newFixedThreadPool(threadNum));
	    server.start();
	    System.out.println("Start Service: " + server.getAddress());
	} else {
	    System.err.println("ImageEffectorService [portNumber] ([threadNum:default=" + DEFAULT_THREAD_NUM + "])");
	}
    }
}
