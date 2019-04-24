package net.javainthebox.imageeffector.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

public class ImageEffectorService implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        System.out.println("HTTP Context: " + t.getHttpContext());
        System.out.println("Method: " + t.getRequestMethod());
        System.out.println("Headers: ");
        t.getRequestHeaders().entrySet().stream().forEach(System.out::println);
	
        InputStream is = t.getRequestBody();
	OutputStream os = t.getResponseBody();
	long length = is.transferTo(os);
	System.out.println("Length: " + length);

        t.sendResponseHeaders(200, length);
        os.close();
    }

    public static void main(String... args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new ImageEffectorService());
        server.start();
    }
    
}
