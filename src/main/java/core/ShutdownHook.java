package core;

import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.http.server.HttpServer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ShutdownHook extends Thread {
    public static final String THREAD_NAME = "Shutdown Hook";
    public static final int GRACE_PERIOD = 60;
    public static final TimeUnit GRACE_PERIOD_TIME_UNIT = TimeUnit.SECONDS;

    private final HttpServer server;

    public ShutdownHook(HttpServer server) {
        this.server = server;
        setName(THREAD_NAME);
    }

    @Override
    public void run() {
        System.out.println("Shutting down dtu pay service...");
        GrizzlyFuture<HttpServer> future = server.shutdown(GRACE_PERIOD, GRACE_PERIOD_TIME_UNIT);

        try {
            System.out.println(String.format("Waiting for connections to terminate... Please wait up to %s %s", GRACE_PERIOD, GRACE_PERIOD_TIME_UNIT));
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Error while shutting down dtu pay service.");
            System.out.println(e.toString());
        }

        System.out.println("Dtu pay service successfully shut down.");
    }
}