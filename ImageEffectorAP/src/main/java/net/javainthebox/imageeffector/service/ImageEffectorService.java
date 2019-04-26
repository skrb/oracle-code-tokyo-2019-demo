package net.javainthebox.imageeffector.service;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import javax.imageio.ImageIO;


public class ImageEffectorService implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
	Headers headers = t.getRequestHeaders();

	int length = Integer.parseInt(headers.get("Content-length").get(0));
	System.out.println("Length: " + length);
	String contentType = headers.get("Content-type").get(0);
	System.out.println("ContentType: " + contentType);
	
        InputStream is = t.getRequestBody();
	//	byte[] buffer = is.readAllBytes();
	//	System.out.println("Buffer Size: " + buffer.length);

	java.util.Arrays.stream(ImageIO.getReaderFormatNames()).forEach(System.out::println);
	var image = ImageIO.read(is);
	System.out.println("W: " + image.getWidth() + " H: " + image.getHeight());
	is.close();

	t.getResponseHeaders().add("Content-type", contentType);
	t.sendResponseHeaders(200, length);
	
	OutputStream os = t.getResponseBody();
	ImageIO.write(image, "jpg", os);
	
	//	os.write(buffer);
        os.close();
    }

    public static void main(String... args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new ImageEffectorService());
        server.start();
    }
}
