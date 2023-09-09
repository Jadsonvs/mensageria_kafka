package br.com.alura.commerce;

import br.com.alura.ecommerce.CorrelationId;
import br.com.alura.ecommerce.dispatcher.KafkaDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class NewOrderServelt extends HttpServlet {
    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //we are not caring about any security issue, we are only
            //showing how to use http as a staring point
            var email = req.getParameter("email");
            var amount = new BigDecimal(req.getParameter("amount"));
            var orderId = req.getParameter("uuid");
            var order = new Order(orderId, amount, email);

            try (var database = new OrdersDatabase()) {

                if (database.saveNew(order)) {
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email,
                            new CorrelationId(NewOrderServelt.class.getSimpleName()),
                            order);

                    System.out.println("New order sent successfully.");
                    resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                    resp.getWriter().println("New order sent!");
                } else {
                    System.out.println("Old order received.");
                    resp.setStatus(HttpServletResponse.SC_ACCEPTED);
                    resp.getWriter().println("Old order received");
                }
            }

        } catch (InterruptedException | SQLException | ExecutionException e) {
            throw new ServletException(e);
        }
    }
}