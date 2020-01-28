package core;

import dtu.services.rest.resources.DtuPayResource;
import dtu.services.rest.resources.TestResource;
import dtupay.Config;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;

public class AppBase
{
    public static void main(String[] args) throws Exception
    {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(JacksonFeature.class);
        resourceConfig.register(BaseResource.class);
        resourceConfig.register(DtuPayResource.class);
        resourceConfig.register(TestResource.class);

        HttpHandler handler = RuntimeDelegate.getInstance()
                .createEndpoint(resourceConfig, HttpHandler.class);

        HttpServer server = HttpServer.createSimpleServer(null, Config.DTU_PAY_PORT);
        server.getServerConfiguration().addHttpHandler(handler);

        DtuPay.initialize();

        Runtime.getRuntime().addShutdownHook(new ShutdownHook(server));

        try
        {
            server.start();
            System.out.println("Server running.. Press CTRL^C to exit!");
            Thread.currentThread().join();
        }
        catch (IOException e)
        {
            System.out.println("Failed to initialize the server");
            System.out.println(e.toString());
        }
        catch (InterruptedException e)
        {
            System.out.println("Received shutdown signal.");
        }
        finally
        {
            server.shutdown();
        }
    }

    @ApplicationPath("/")
    public class BaseResource extends javax.ws.rs.core.Application
    {

    }
}