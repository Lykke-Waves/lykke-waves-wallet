package ru.tolsi.lykke.waves;

import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;

import java.util.concurrent.ExecutionException;

public class Server extends HttpApp {
    @Override
    protected Route routes() {
        return path("hello", () ->
                get(() ->
                        complete("<h1>Say hello to akka-http</h1>")
                )
        );
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Starting the server
        final Server myServer = new Server();
        myServer.startServer("localhost", 8080);
    }
}
