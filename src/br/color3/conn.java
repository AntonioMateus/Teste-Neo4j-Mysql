/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;
//import java.sql.*; Mysql
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.neo4j.jdbc.Driver;


public class conn{
    //Connection conn = null; Mysql
    	
    public static Connection connect() throws SQLException{
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
        /* IDEIA MAIS NOVA 
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
        disconnect(conn);*/
        String query = "optional match (n:image) return n";
        Connection conn = null;
        try {
            conn = connect();
            Statement stmt = conn.createStatement();
            stmt.execute("match (n) optional match (n)-[r]-() delete n,r");
            stmt.execute("create (teste:image {idImage:0, pathImage:'imagem/teste.jpg'})");
            ResultSet rs = stmt.executeQuery(query);
            rs.next(); 
            String first = rs.getString("n");
            if (first == null) {
                System.out.println("Nao ha nenhuma imagem no banco de dados");
            }
            else {
                //System.out.println(first);
                String[] paresPropriedadeValor = first.substring(1, first.length()-1).split(",");
                for (String par: paresPropriedadeValor) {
                    System.out.println(par.split(":")[0]+" = "+par.split(":")[1]);
                }
                while (rs.next()) {
                    System.out.println(rs.getString("n"));
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