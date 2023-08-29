package br.com.alura.commerce;

import br.com.alura.ecommerce.KafkaDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
public class NewOrderServelt extends HttpServlet {
    private final KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>();
    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher<>();
    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //we are not caring about any security issue, we are only
            //showing how to use http as a staring point
            var email = req.getParameter("email");
            var amount = new BigDecimal(req.getParameter("amount"));

            var orderId = UUID.randomUUID().toString();

            var order = new Order(orderId, amount, email);
            orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

            var emailCode = "Thank you for your order! We are processing your order!";
            emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);

            System.out.println("New order sent successfully.");
            resp.setStatus(HttpServletResponse.SC_ACCEPTED);
            resp.getWriter().println("New order sent!");
        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }
}