package br.com.alura.commerce.database;

import java.sql.*;
public class LocalDatabase {
    private final Connection connection;

    //Criando conexão com o banco e criando tabela
    public LocalDatabase(String name) throws SQLException {
        String url = "jdbc:sqlite:target/" + name + ".db";
        connection = DriverManager.getConnection(url);
    }

    //yes, this is way too generic.
    //according to your database tool, avoid injection.
    public void createIfNotExists(String sql) {
        try {
            connection.createStatement().execute(sql);
        } catch (SQLException ex) {
            //be carefull, the sql could be wrong, be really carefull
            ex.printStackTrace();
        }
    }
    private PreparedStatement prepare(String statement, String[] params) throws SQLException {
        var preparedStatement = connection.prepareStatement(statement);
        for(int i = 0; i < params.length; i++) {
            preparedStatement.setString(i + 1, params[i]);
        }
        return preparedStatement;
    }
    public void update(String statement, String... params) throws SQLException {
        prepare(statement, params).execute();
    }

    public ResultSet query(String query, String... params) throws SQLException {
        return prepare(query, params).executeQuery();
    }

    public void close() throws SQLException {
        connection.close();
    }
}
