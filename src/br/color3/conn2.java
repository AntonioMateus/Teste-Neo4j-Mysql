/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;
import java.sql.*;

public class conn2{
    Connection conn = null;
	
    public static Connection connect(String sql) throws Exception{
	Connection conn = null;
	try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost/mydb","root","");		    
            //System.out.println("Conexao aberta");                  
	}catch(SQLException e){
            //e.printStackTrace();
        }
	return conn;
    }
		
    public static void disconnect(Connection conn){
	try {
            conn.close();
            //System.out.println("Fechando a conexão");
        } catch(SQLException erro) {
            //System.out.println("Erro no fechamento");
	}
    }
    
    public static void main (String args[]) {
        try {
            Connection conn = connect(null);
            if (conn != null) {
                disconnect(conn);
                System.out.println("Deu certo !");
            }
            else {
                System.out.println("Conexao nao estabelecida");
            }
        }
        catch (Exception e) {
            //System.out.println("Deu ruim. Puta merda !");
            e.printStackTrace(); 
        }
    }
}