/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;
//import java.sql.*; Mysql
import iot.jcypher.database.DBAccessFactory;
import iot.jcypher.database.DBProperties;
import iot.jcypher.database.DBType;
import iot.jcypher.database.IDBAccess;
import iot.jcypher.graph.GrNode;
import iot.jcypher.query.JcQuery;
import iot.jcypher.query.JcQueryResult;
import iot.jcypher.query.api.IClause;
import iot.jcypher.query.factories.clause.MATCH;
import iot.jcypher.query.factories.clause.RETURN;
import iot.jcypher.query.values.JcNode;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class conn{
    //Connection conn = null; Mysql
    	
    public static GraphDatabaseService connect(){
	/* Mysql
        Connection conn = null;
	try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://localhost/mydb?user=root&password=dbfx@503_t86";
            conn = DriverManager.getConnection(url);		    
	    System.out.println( "Connection opened");
	}catch(SQLException e){
            e.printStackTrace();
	}
	return conn;*/
        GraphDatabaseService conn = new GraphDatabaseFactory().newEmbeddedDatabase("C:/Users/Antonio Mateus/Desktop/Iniciação Cientifica/Iniciação - Fátima/Neo4J/neo4j-enterprise-2.3.0-M02/data/graphTest");
        //System.out.println("Conexao com o BD iniciada");
        return conn;
    }
		
    public static void disconnect(GraphDatabaseService conn){
	/* Mysql
        try {
            conn.close();
            System.out.println("Fechando a conex�o");
        } catch(SQLException erro) {
            System.out.println("Erro no fechamento");
        }*/
        conn.shutdown();
        //System.out.println("Conexao com o BD encerrada");
    }
    
    public static void main(String[] args) {
	/* Mysql
        String query = "Select * from image";
	connect(query);*/
        String query = "optional match (n:image) return n";
        GraphDatabaseService conn = connect();
        ExecutionEngine engine = new ExecutionEngine(conn);
        ExecutionResult result;
        
        try (Transaction tx = conn.beginTx()){
            result = engine.execute(query);            
            Iterator<Node> columns = result.columnAs("n");
            while (columns.hasNext()) {
                Node image = columns.next();
                long id = (long) image.getProperty("idImage");
                String path = (String) image.getProperty("pathImage");
                System.out.println (id +"   " +path);
            }
            tx.success();
        }
        catch (NullPointerException n) {
            System.out.println("Nao tem nenhuma imagem no BD");
        }
        finally {
            disconnect(conn);
        }
    }
}