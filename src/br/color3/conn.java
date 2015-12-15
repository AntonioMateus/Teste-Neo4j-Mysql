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
    	
    public static IDBAccess connect(){
	/* LOGICA ANTIGA 
        GraphDatabaseService conn = new GraphDatabaseFactory().newEmbeddedDatabase("C:/Users/Antonio Mateus/Desktop/Iniciação Cientifica/Iniciação - Fátima/Neo4J/neo4j-enterprise-2.3.0-M02/data/graphTest");
        //System.out.println("Conexao com o BD iniciada"); 
        return conn;*/
        Properties p = new Properties();
        p.setProperty(DBProperties.SERVER_ROOT_URI, "http://localhost:7476/");
        IDBAccess remote = DBAccessFactory.createDBAccess(DBType.REMOTE, p, "neo4j", "antonio");
        return remote;
    }
		
    public static void disconnect(IDBAccess conn){
	/* LOGICA ANTIGA
        conn.shutdown();
        //System.out.println("Conexao com o BD encerrada"); */
        conn.close();
    }
    
    public static void main(String[] args) {
	/* LOGICA ANTIGA
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
        }*/
        IDBAccess conn = connect(); 
        JcNode imagem = new JcNode("image");
        JcQuery consulta = new JcQuery(); 
        consulta.setClauses(new IClause[] {
            MATCH.node(imagem).label("image"),
            RETURN.value(imagem)
        });
        List<GrNode> imagens = conn.execute(consulta).resultOf(imagem);
        if (imagens.size() > 0) {
            for (GrNode img: imagens) {
                long id = (long) img.getProperty("idImage").getValue();
                String path = (String) img.getProperty("pathImage").getValue();
                System.out.println(id +"    " +path);
            }
        }
        else {
            System.out.println("Nao existe ainda nenhuma imagem no banco de dados");
        }
        disconnect(conn);
    }
}