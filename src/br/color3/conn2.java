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
            conn = DriverManager.getConnection("jdbc:mysql://localhost/mydb","root","dbfx@503_t86");		    
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
}