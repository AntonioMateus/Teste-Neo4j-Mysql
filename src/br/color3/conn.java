/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;
//import java.sql.*; Mysql
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class conn{
    //Connection conn = null; Mysql
    	
    public static Connection connect() {
	/* LOGICA ANTIGA 
        GraphDatabaseService conn = new GraphDatabaseFactory().newEmbeddedDatabase("C:/Users/Antonio Mateus/Desktop/Iniciação Cientifica/Iniciação - Fátima/Neo4J/neo4j-enterprise-2.3.0-M02/data/graphTest");
        //System.out.println("Conexao com o BD iniciada"); 
        return conn;*/
        /* LOGICA MAIS NOVA
        Properties p = new Properties();
        p.setProperty(DBProperties.SERVER_ROOT_URI, "http://localhost:7476/");
        IDBAccess remote = DBAccessFactory.createDBAccess(DBType.REMOTE, p, "neo4j", "admin");
        return remote;*/
        Connection conn = null;
        try {
            Class.forName("org.neo4j.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:neo4j://localhost:7476/","neo4j","admin"); 
        }
        catch (ClassNotFoundException c) {
            System.out.println("Driver do Neo4j nao encontrado");
        }
        catch (SQLException s) {
            System.out.println("Erro ao abrir a conexao");
        }
        return conn; 
    }
		
    public static void disconnect(Connection conn){
	/* LOGICA ANTIGA
        conn.shutdown();
        //System.out.println("Conexao com o BD encerrada"); */
        try {
            conn.close();
        }
        catch (SQLException s) {
            System.out.println("Erro ao fechar a conexao");
        }
    }
    private static String getProperty(String no, String propriedade) {
        String[] paresPropriedadeValor = no.substring(1, no.length()-1).split(",");
        for (String par: paresPropriedadeValor) {
            String[] chaveValor = par.split(":"); 
            if (chaveValor[0].substring(1, chaveValor[0].length()-1).equals(propriedade)) {
                if (chaveValor[1].contains("\"")) {
                    return chaveValor[1].substring(1, chaveValor[1].length()-1);
                }
                else {
                    return chaveValor[1];
                }
            }
        }
        return null; 
    }
    public static void main(String[] args) {
	Connection conn = null;
        try {
            conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("match (n:image) return n order by n.idImage limit 10");
            rs.next(); 
            String first = rs.getString("n");
            if (first == null) {
                System.out.println("Nao ha nenhuma imagem no banco de dados");
            }
            else {
                //System.out.println(first);
                System.out.println(getProperty(first,"idImage")+" = "+getProperty(first,"pathImage")+" ; ");
                while (rs.next()) {
                    String imagem = rs.getString("n");
                    System.out.println(getProperty(imagem,"idImage")+" = "+getProperty(imagem,"pathImage")+" ; ");
                }
            }
        }
        catch (SQLException s) {
            System.out.println("Houve um problema durante a execucao de uma das consultas");
        }
        finally {
            if (conn != null) {
                disconnect(conn);
            }
        }        
    }
}