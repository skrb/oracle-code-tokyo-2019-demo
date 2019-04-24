package net.javainthebox.imageeffector.service;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageEffectorClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(2_000))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000"))
                .header("Content-Type", "image/jpg")
                .POST(BodyPublishers.ofFile(Paths.get("test.jpg")))
                .build();

        HttpResponse<Path> response = client.send(request, BodyHandlers.ofFile(Paths.get("result.jpg")));
        System.out.println(response.statusCode());
    }
}
