package br.com.alura.commerce;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HttpEcommerceService {
    public static void main(String[] args) throws Exception {
        var server = new Server(8080);

        var context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(new ServletHolder(new NewOrderServelt()), "/new");

        server.setHandler(context);

        server.start();
        //Aguardar o servidor parar para então finalizar a aplicação
        server.join();
    }

}