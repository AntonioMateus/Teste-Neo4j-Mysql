/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;
//Leitura de arquivos txt (scripts cypher e mysql)
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//Conexao com o banco de dados Mysql e Neo4j
import java.sql.Connection;
import java.sql.SQLException;
//Busca por imagens no banco de dados Mysql e Neo4j
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Antonio Mateus
 */
public class TesteDesempenho {
    private static double criaEstruturasNeo4j() {
        long instanteInicio = 0;
        long instanteFim = instanteInicio;
        Connection x = conn.connect();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("C:\\Users\\Antonio Mateus\\Desktop\\Iniciação Cientifica\\Iniciação - Fátima\\Sistema da Leila\\script_criacao_cypher_aleatorio.txt"));
            String linhaAtual = null;
            instanteInicio = System.currentTimeMillis();
            Statement stm = x.createStatement(); 
            stm.execute("optional match (n) optional match (n)-[r]-() delete n,r");
            while (!((linhaAtual = br.readLine()).equals(""))) {
                stm.execute(linhaAtual);
            }
            while (!((linhaAtual = br.readLine()).equals(""))) {
                stm.execute(linhaAtual);
            } 
            while ((linhaAtual = br.readLine()) != null) {
                stm.execute(linhaAtual);
            }
            //Comentar abaixo quando precisar voltar para a versão antiga
            double matriz [][] = new double [1001][17];
            //String cypher = "match (a:image)-[r:image_has_feature]->(b:feature) with r.value as value, a.idImage as idImage, b.idFeature as idFeature return value order by idImage, idFeature";
            String cypher = "match (a:image)-[r:image_has_feature]->(b:feature) return r.value as value order by a.idImage, b.idFeature";
            ResultSet rs = stm.executeQuery(cypher);
            for (int i = 0; i < 1001; i++) {
                for (int j = 0; j < 17; j++) {
                    rs.next();
                    matriz[i][j] = (Double) rs.getDouble("value");
                }
            }
            EuclideanDistance d = new EuclideanDistance(); 
            for (int id = 1; id <= 1001; id++) {
                double[] dif = d.calculaDistancia(matriz, id);
                for (int i = 0; i < 1001; i++) {
                    if (id != i+1) {
                        stm.execute("match (inicio:image {idImage: "+id+"})");
                        stm.execute("match (fim:image {idImage: "+(i+1)+"})");
                        stm.execute("create (inicio)-[:imgToImg {dist: "+dif[i]+"}]->(fim)");
                    }
                }
            }
            instanteFim = System.currentTimeMillis();
        }
        catch (IOException e) {
            //System.out.println("Erro ao abrir o script para leitura");
        }
        catch (SQLException s) {
            //System.out.println("Erro ao realizar uma das consultas");
        }
        finally {
            try {
                if (br != null) br.close();
                if (x != null) conn.disconnect(x);                 
            }
            catch (IOException ex) {
                //System.out.println("Erro ao fechar a leitura do script");
            }
        }
        double duracao = (double) (instanteFim - instanteInicio)/1000;
        return duracao; 
    }
    
    private static double criaEstruturasMysql() {
        Connection con = null;
        long instanteInicio = 0;
        long instanteFim = instanteInicio; 
        BufferedReader br = null; 
        try {
            con = conn2.connect(null);		    
	    /*delecao do que ja havia no banco de dados*/
            Statement delecao = con.createStatement();
            delecao.execute("DROP DATABASE mydb;");
            br = new BufferedReader(new FileReader("C:\\Users\\Antonio Mateus\\Desktop\\Iniciação Cientifica\\Iniciação - Fátima\\Sistema da Leila\\script_criacao_sql_aleatorio.txt"));
            String linhaAtual = null;
            //criacao das estruturas 
            instanteInicio = System.currentTimeMillis();
            Statement criacao = con.createStatement();
            criacao.execute("SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;");
            criacao.execute("SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;");
            criacao.execute("SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';");
            criacao.execute("CREATE SCHEMA IF NOT EXISTS mydb DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci ;");
            criacao.execute("USE mydb ;");
            criacao.execute("CREATE TABLE IF NOT EXISTS mydb.image ( idImage INT NOT NULL AUTO_INCREMENT, pathImage VARCHAR(500) NOT NULL , PRIMARY KEY (idImage) ) ENGINE = InnoDB;");
            criacao.execute("CREATE TABLE IF NOT EXISTS mydb.feature (  idFeature INT NOT NULL AUTO_INCREMENT , featureName VARCHAR(100) NOT NULL , featureType INT NOT NULL COMMENT '1-Color\\n2-Texture\\n3-Shape' , PRIMARY KEY (idFeature) ) ENGINE = InnoDB;");
            criacao.execute("CREATE TABLE IF NOT EXISTS mydb.image_has_feature (image_idImage INT NOT NULL , feature_idFeature INT NOT NULL , value DECIMAL(10,0)  NOT NULL , PRIMARY KEY (image_idImage, feature_idFeature) , INDEX fk_image_has_feature_feature1 (feature_idFeature ASC) , CONSTRAINT fk_image_has_feature_image  FOREIGN KEY (image_idImage ) REFERENCES mydb.image (idImage ) ON DELETE NO ACTION  ON UPDATE NO ACTION, CONSTRAINT fk_image_has_feature_feature1 FOREIGN KEY (feature_idFeature ) REFERENCES mydb.feature (idFeature )  ON DELETE NO ACTION ON UPDATE NO ACTION) ENGINE = InnoDB;");
            criacao.execute("SET SQL_MODE=@OLD_SQL_MODE;");
            criacao.execute("SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;");
            criacao.execute("SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;");
            //insercao das imagens
            Statement insercao_imagens = con.createStatement();
            while (!((linhaAtual = br.readLine()).equals(""))) {
                insercao_imagens.execute(linhaAtual);
            }
            //insercao das caracteristicas
            Statement insercao_caracteristicas = con.createStatement();
            while (!((linhaAtual = br.readLine()).equals(""))) {
                insercao_caracteristicas.execute(linhaAtual);
            }
            //insercao dos relacionamentos "imagem tem caracteristica"
            Statement insercao_relacionamentos = con.createStatement();
            while ((linhaAtual = br.readLine()) != null) {
                insercao_relacionamentos.execute(linhaAtual);
            }            
            instanteFim = System.currentTimeMillis();
        }
        catch (IOException l) {
            //System.out.println("Problemas ao executar um dos scripts de criacao das estruturas");
        }
        catch (SQLException e) {
            //System.out.println("Houve um erro na sintaxe sql");
        }
        catch (Exception e) {
            //System.out.println("Problemas ao conectar com o banco de dados");
        }
        finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (con != null) {
                    con.close();
                    //System.out.println("Conexao encerrada");
                }
            } catch(SQLException erro) {
                //System.out.println("Erro no fechamento da conexao");
            }
            catch (IOException i) {
                //System.out.println("Erro no fechamento do leitor");
            }
        }
        double duracao = (double) (instanteFim - instanteInicio)/1000; 
        return duracao;
    }
    
    /*private static String retornaMediaEstruturasNeo4j(int numeroIteracoes) {
        double soma = 0;
        for (int i = 0; i < numeroIteracoes; i++) {
            double resultadoVez = criaEstruturasNeo4j();
            soma = soma + resultadoVez;
        }
        DecimalFormat df = new DecimalFormat("####.##");
        return df.format(soma/numeroIteracoes);
    }*/
    
    /*private static String retornaMediaEstruturasMysql(int numeroIteracoes) {
        double soma = 0;
        for (int i = 0; i < numeroIteracoes; i++) {
            double resultadoVez = criaEstruturasMysql();
            soma = soma + resultadoVez; 
        }
        DecimalFormat df = new DecimalFormat("####.##");
        return df.format(soma/numeroIteracoes);
    }*/
    
    private static double buscaImagemMysql(String caminhoImagem, int quantidadeSemelhantes) {
        long instanteInicio = 0;
        long instanteFim = instanteInicio;        
        int id =0;
        String path = null;
	double[] dif = new double [1000];
	double matriz [][] = new double [1000][17];
	java.sql.Connection x = null;
	String sqlRandom = "SELECT * FROM mydb.image WHERE pathImage = '"+caminhoImagem+"';";
	String sql = "SELECT * FROM mydb.image_has_feature ORDER BY image_idImage;";
	try {
            instanteInicio = System.currentTimeMillis();
            x = conn2.connect(sqlRandom);
            Statement stm = x.createStatement();
            ResultSet rs = stm.executeQuery(sqlRandom);
	    x= conn2.connect(sql);
	    Statement stm2 = x.createStatement();
	    ResultSet rs2 = stm2.executeQuery(sql);
	    while(rs.next()) { 
	    	id = rs.getInt("idImage");
	    	path = rs.getString("pathImage");
	    }
	    for(int i =0; i<1000;i++){
	    	for(int j = 0; j<17; j++){
                    rs2.next();
                    matriz[i][j]=rs2.getDouble("value");                    
		}
            }
            EuclideanDistance d = new EuclideanDistance();
	    dif = d.calculaDistancia(matriz, id);
            Statement criacao = x.createStatement(); 
            //boolean bool;
            criacao.execute("DROP TABLE IF EXISTS temp;");
            criacao.execute("CREATE TABLE temp (idImage int not null);");
            criacao.execute("Alter table temp ADD dist decimal(10,3) default 0 not null AFTER idImage;");
            //int bool2;
            for(int i = 0; i<1000; i++){
	    	//System.out.println(dif[i]);
                criacao.executeUpdate("INSERT INTO mydb.temp(idImage,dist) VALUES ("+(i+1)+","+dif[i]+");");
	    }
	    ResultSet rs3= criacao.executeQuery("SELECT pathImage, temp.idImage from mydb.temp, mydb.image where image.idImage = temp.idImage order by dist LIMIT " +quantidadeSemelhantes);
	    //String[] top = new String[quantidadeSemelhantes];
	    //int i =0;
	    while(rs3.next()) { 
	    	id = rs3.getInt("idImage");
	    	//String path2 = rs3.getString("pathImage");
	    	//top[i] = path2;
	    	//i++;
	    }
            conn2.disconnect(x);
            instanteFim = System.currentTimeMillis();
        }
        catch (Exception e) {
            //e.printStackTrace(); 
            return 0;
        }
        return ((double) instanteFim - instanteInicio)/1000; 
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
    
    private static double buscaImagemNeo4j(String caminhoImagem, int quantidadeSemelhantes) {
        long instanteInicial = 0;
        long instanteFim = instanteInicial;
        int id =0;
        String path = null;
        String cypherRandom = "match (n:image) where n.pathImage = '"+caminhoImagem+"' return n";
        Connection x = conn.connect();
        try {
            Statement stm = x.createStatement();
            instanteInicial = System.currentTimeMillis();    
            ResultSet rs = stm.executeQuery(cypherRandom); 
            while (rs.next()) {
                String image = rs.getString("n"); 
                id = Integer.valueOf(getProperty(image,"idImage"));
                path = getProperty(image,"pathImage");
            }
            stm.executeQuery("match (a:image)-[r:imgToImg]->(b:image) where a.idImage = "+id+" return b order by r.dist limit " +String.valueOf(quantidadeSemelhantes));
        }
        catch (SQLException s) {
            //System.out.println("Houve um problema durante uma das consultas");
            return 0; 
        }
        finally {
            instanteFim = System.currentTimeMillis();
            //********************* Conexão com o banco de dados (imagens e caracteristicas) *************************
            if (x != null) conn.disconnect(x);
            //********************************************************************************************************
        }
        return ((double) instanteFim - instanteInicial)/1000;
    }
    
    /*private static String retornaMediaBuscaNeo4j(String caminhoImagem, int quantidadeSemelhantes, int numeroIteracoes) {
        double soma = 0; 
        for (int i = 0; i < numeroIteracoes; i++) {
            double resultadoVez = buscaImagemNeo4j(caminhoImagem, quantidadeSemelhantes);
            soma = soma + resultadoVez;
        }
        DecimalFormat df = new DecimalFormat("####.##");
        return df.format(soma/numeroIteracoes);
    }*/
    
    /*private static String retornaMediaBuscaMysql (String caminhoImagem, int quantidadeSemelhantes, int numeroIteracoes) {
        double soma = 0;
        for (int i = 0; i < numeroIteracoes; i++) {
            double resultadoVez = buscaImagemMysql(caminhoImagem, quantidadeSemelhantes);
            soma = soma + resultadoVez;
        }
        DecimalFormat df = new DecimalFormat("####.##");
        return df.format(soma/numeroIteracoes);
    }*/
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here 
        try {
            double[] resultados = new double[22];
            double tempoEstruturasMysql = criaEstruturasMysql();
            resultados[0] = tempoEstruturasMysql;
            double tempoEstruturasNeo4j = criaEstruturasNeo4j();
            resultados[1] = tempoEstruturasNeo4j; 
            resultados[2] = buscaImagemMysql("imagem/1.jpg", 100);
            resultados[3] = buscaImagemNeo4j("imagem/1.jpg", 100);
            int pos = 4; 
            for (int i = 10; i <= 90; i=i+10) {
                double tempoBuscaMysql = buscaImagemMysql("imagem/"+i+".jpg", 100);
                resultados[pos] = tempoBuscaMysql;
                pos++;
                double tempoBuscaNeo4j = buscaImagemNeo4j("imagem/"+i+".jpg", 100); 
                resultados[pos] = tempoBuscaNeo4j;
                pos++; 
            }            
            for (int k = 0; k < resultados.length; k++) {
                System.out.print(resultados[k] +" ");
            }
            System.out.println();
        }
        catch (RuntimeException r) {
            r.printStackTrace();
            System.exit(1);
        }               
    }    
}