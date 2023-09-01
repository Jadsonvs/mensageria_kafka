package br.com.alura.commerce;

import br.com.alura.ecommerce.KafkaService;
import br.com.alura.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class CreateUserService {
    private final Connection connection;

    //Criando conexão com o banco e criando tabela
    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("create table Users (" +
                    "uuid varchar(200) primary key," +
                    "email varchar(200))");
        } catch (SQLException ex) {
            //be carefull, the sql could be wrong, be really carefull
            ex.printStackTrace();
        }
    }
    public static void main(String[] args) throws SQLException {
        var createUserService = new CreateUserService();
        try (var service = new KafkaService<>(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                createUserService::parse,
                Map.of())) {
            service.run();
        }
    }
    private void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());
        var order = record.value().getPayload();
        if(isNewEmail(order.getEmail())) {
            insertNewUser(order.getEmail());
        }
    }

    //Inserindo valores nos atributos da tabela do banco
    private void insertNewUser(String email) throws SQLException {
        var insert = connection.prepareStatement("insert into Users (uuid, email) " +
                "values (?,?)");
        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2, email);
        insert.execute();
        System.out.println("Usuário uuid e " + email + " adicionado");
    }
    //Verificando se usuário existe dentro do banco com uma consulta
    private boolean isNewEmail(String email) throws SQLException {
        var exists = connection.prepareStatement("select * from Users where email = ? limit 1");
        exists.setString(1, email);
        var results = exists.executeQuery();
        return !results.next();
    }
}
