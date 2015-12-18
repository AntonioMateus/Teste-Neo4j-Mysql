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
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Antonio Mateus
 */
public class TesteDesempenho {
    private static String[] valoresAleatorios; 
    
    private static void geraValoresAleatorios(int quantidadeImagens) {
        Random geradorValores = new Random(); 
            DecimalFormat df = new DecimalFormat("###.#######");
        valoresAleatorios = new String[quantidadeImagens*17];
        for (int k = 0; k < valoresAleatorios.length; k++) {
            //valores[k] = Math.abs(geradorValores.nextDouble());
            valoresAleatorios[k] = "\'"+df.format(Math.abs(geradorValores.nextDouble())).replace(',', '.')+"\'"; 
        }
    }
    
    private static double criaEstruturasNeo4j() {
        long instanteInicio = 0;
        long instanteFim = instanteInicio;
        int quantidadeImagens = valoresAleatorios.length/17;
        Connection x = conn.connect();
        BufferedReader br = null;
        try {
            //String linhaAtual = null;
            instanteInicio = System.currentTimeMillis();
            Statement stm = x.createStatement(); 
            stm.execute("match (n) optional match (n)-[r]-() delete n,r");
            /*while (!((linhaAtual = br.readLine()).equals(""))) {
                stm.execute(linhaAtual);
            }
            while (!((linhaAtual = br.readLine()).equals(""))) {
                stm.execute(linhaAtual);
            } 
            while ((linhaAtual = br.readLine()) != null) {
                stm.execute(linhaAtual);
            }*/ 
            
            String criacaoImagensCypher = "CREATE ";
            for (int i = 1; i < quantidadeImagens; i++) {
                criacaoImagensCypher = criacaoImagensCypher + "(img"+i+":image {idImage:"+i+", pathImage:'imagem/"+i+".jpg'}), ";
            }
            criacaoImagensCypher = criacaoImagensCypher + "(img"+quantidadeImagens+":image {idImage:"+quantidadeImagens+", pathImage:'imagem/"+quantidadeImagens+".jpg'})";
            stm.execute(criacaoImagensCypher);
            
            stm.execute("CREATE (feat3:feature {idFeature:3, featureName:'Media Histograma RGB', featureType:1}), "+
                               "(feat4:feature {idFeature:4, featureName:'Desvio Histograma RGB', featureType:1}), "+
                               "(feat5:feature {idFeature:5, featureName:'Inclinacao Histograma RGB', featureType:1}), "+
                               "(feat6:feature {idFeature:6, featureName:'Media Histograma NC', featureType:1}), "+
                               "(feat7:feature {idFeature:7, featureName:'Desvio Histograma NC', featureType:1}), "+
                               "(feat8:feature {idFeature:8, featureName:'Inclinacao Histograma NC', featureType:1}), "+
                               "(feat9:feature {idFeature:9, featureName:'Media Pixel RGB', featureType:1}), "+
                               "(feat10:feature {idFeature:10, featureName:'Desvio Pixel RGB', featureType:1}), "+
                               "(feat11:feature {idFeature:11, featureName:'Inclinacao Pixel RGB', featureType:1}), "+
                               "(feat12:feature {idFeature:12, featureName:'Media Pixel NC', featureType:1}), "+
                               "(feat13:feature {idFeature:13, featureName:'Desvio Pixel NC', featureType:1}), "+
                               "(feat14:feature {idFeature:14, featureName:'Inclinacao Pixel NC', featureType:1}), "+
                               "(feat15:feature {idFeature:15, featureName:'Contraste', featureType:2}), "+
                               "(feat16:feature {idFeature:16, featureName:'Homogeneidade', featureType:2}), "+
                               "(feat17:feature {idFeature:17, featureName:'Momento1', featureType:3}), "+
                               "(feat18:feature {idFeature:18, featureName:'Momento2', featureType:3}), "+
                               "(feat19:feature {idFeature:19, featureName:'Momento3', featureType:3})");
            
            int cont = 0;
            for (int j = 1; j <= quantidadeImagens; j++) {
                String consultas = "MATCH (img"+j+":image {idImage: "+j+"}) ";
                String criacoesCypher = "";
                for (int caracteristica = 3; caracteristica < 19; caracteristica++) {
                    consultas = consultas + "MATCH (feat"+caracteristica+":feature {idFeature: "+caracteristica+"}) ";
                    criacoesCypher = criacoesCypher + "CREATE (img"+j+")-[:image_has_feature {value: "+valoresAleatorios[cont]+"}]->(feat"+caracteristica+") ";
                    cont++; 
                }
                consultas = consultas + "MATCH (feat19:feature {idFeature: 19}) ";
                criacoesCypher = criacoesCypher + "CREATE (img"+j+")-[:image_has_feature {value: "+valoresAleatorios[cont]+"}]->(feat19)";
                stm.execute(consultas + criacoesCypher);
                cont++; 
            }
            
            //Comentar abaixo quando precisar voltar para a versão antiga
            double matriz [][] = new double [quantidadeImagens][17];
            //String cypher = "match (a:image)-[r:image_has_feature]->(b:feature) with r.value as value, a.idImage as idImage, b.idFeature as idFeature return value order by idImage, idFeature";
            String cypher = "match (a:image)-[r:image_has_feature]->(b:feature) return r.value as value order by a.idImage, b.idFeature";
            ResultSet rs = stm.executeQuery(cypher);
            for (int i = 0; i < quantidadeImagens; i++) {
                for (int j = 0; j < 17; j++) {
                    rs.next();
                    matriz[i][j] = Double.valueOf(rs.getString("value"));
                }
            }
            EuclideanDistance d = new EuclideanDistance(); 
            for (int id = 1; id <= quantidadeImagens; id++) {
                double[] dif = d.calculaDistancia(matriz, id);
                for (int i = 0; i < quantidadeImagens; i++) {
                    if (id != i+1) {
                        stm.execute("match (inicio:image {idImage: "+id+"}), (fim:image {idImage: "+(i+1)+"}) create (inicio)-[:imgToImg {dist: "+dif[i]+"}]->(fim)");
                    }
                }
            }
            instanteFim = System.currentTimeMillis();
        }       
        catch (Exception s) {
            //System.out.println("Erro ao realizar uma das consultas");
            s.printStackTrace();
        }
        finally {
            if (x != null) conn.disconnect(x);                 
        }
        double duracao = (double) (instanteFim - instanteInicio)/1000;
        return duracao; 
    }
    
    private static double criaEstruturasMysql() {
        Connection con = null;
        long instanteInicio = 0;
        long instanteFim = instanteInicio; 
        int quantidadeImagens = valoresAleatorios.length/17; 
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
            criacao.execute("CREATE TABLE IF NOT EXISTS mydb.image_has_feature (image_idImage INT NOT NULL , feature_idFeature INT NOT NULL , value VARCHAR(20) NOT NULL , PRIMARY KEY (image_idImage, feature_idFeature) , INDEX fk_image_has_feature_feature1 (feature_idFeature ASC) , CONSTRAINT fk_image_has_feature_image  FOREIGN KEY (image_idImage ) REFERENCES mydb.image (idImage ) ON DELETE NO ACTION  ON UPDATE NO ACTION, CONSTRAINT fk_image_has_feature_feature1 FOREIGN KEY (feature_idFeature ) REFERENCES mydb.feature (idFeature )  ON DELETE NO ACTION ON UPDATE NO ACTION) ENGINE = InnoDB;");
            criacao.execute("SET SQL_MODE=@OLD_SQL_MODE;");
            criacao.execute("SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;");
            criacao.execute("SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;");
            /*//insercao das imagens
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
            while (((linhaAtual = br.readLine()) != null) || !((linhaAtual = br.readLine()).equals(""))) {
                insercao_relacionamentos.execute(linhaAtual);
            } */
            //insercao das imagens
            Statement insercaoImagens = con.createStatement();
            String criacaoImagensSql = "INSERT INTO mydb.image (idImage,pathImage) VALUES ";
            int i;
            for (i = 1; i < quantidadeImagens; i++) {
                criacaoImagensSql = criacaoImagensSql + "("+i+",'imagem/"+i+".jpg'), ";
            }
            criacaoImagensSql = criacaoImagensSql + "("+quantidadeImagens+",'imagem/"+quantidadeImagens+".jpg');";
            insercaoImagens.execute(criacaoImagensSql);            
            //insercao das caracteristicas
            Statement insercaoCaracteristicas = con.createStatement();
            insercaoCaracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (3,'Média Histograma RGB',1), "+
                                                                                                                "(4,'Desvio Histograma RGB',1), "+
                                                                                                                "(5,'Inclinação Histograma RGB',1), "+
                                                                                                                "(6,'Média Histograma NC',1), "+
                                                                                                                "(7,'DesvioHistograma NC',1), "+
                                                                                                                "(8,'Inclinação Histograma NC',1), "+
                                                                                                                "(9,'Média Pixel RGB',1), "+
                                                                                                                "(10,'Desvio Pixel RGB',1), "+
                                                                                                                "(11,'Inclinação Pixel RGB',1), "+
                                                                                                                "(12,'Média Pixel NC',1), "+
                                                                                                                "(13,'Desvio Pixel NC',1), "+
                                                                                                                "(14,'Inclinação Pixel NC',1), "+
                                                                                                                "(15,'Contraste',2), "+
                                                                                                                "(16,'Homogeneidade',2), "+
                                                                                                                "(17,'Momento1',3), "+
                                                                                                                "(18,'Momento2',3), "+
                                                                                                                "(19,'Momento3',3);"); 
            //insercao dos relacionamentos "imagem tem caracteristica"
            Statement insercaoRelacionamentos = con.createStatement();
            int cont = 0;
            for (int j = 1; j <= quantidadeImagens; j++) {
                for (int caracteristica = 3; caracteristica <= 19; caracteristica++) {
                    insercaoRelacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES "+"("+j+","+caracteristica+","+valoresAleatorios[cont]+");");
                    cont++; 
                }                 
            }
            instanteFim = System.currentTimeMillis();
        }
        catch (Exception e) {
            e.printStackTrace();
            instanteFim = System.currentTimeMillis();
            return (double) (instanteFim - instanteInicio)/1000;
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
        int quantidadeImagens = valoresAleatorios.length/17 - 1;
	double[] dif = new double [quantidadeImagens];
	double matriz [][] = new double [quantidadeImagens][17];
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
            for(int i =0; i<quantidadeImagens;i++){
	    	for(int j = 0; j<17; j++){
                    rs2.next();
                    matriz[i][j]=Double.valueOf(rs2.getString("value"));                    
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
            for(int i = 0; i<quantidadeImagens; i++){
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
            e.printStackTrace(); 
            instanteFim = System.currentTimeMillis();
            return ((double) instanteFim - instanteInicio)/1000;
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
        catch (Exception s) {
            s.printStackTrace();
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
            int quantidadeImagens = 601; 
            geraValoresAleatorios(quantidadeImagens);
            double[] resultados = new double[22];
            double tempoEstruturasMysql = criaEstruturasMysql();
            resultados[0] = tempoEstruturasMysql;
            double tempoEstruturasNeo4j = criaEstruturasNeo4j();
            resultados[1] = tempoEstruturasNeo4j; 
            resultados[2] = buscaImagemMysql("imagem/1.jpg", 100);
            resultados[3] = buscaImagemNeo4j("imagem/1.jpg", 100);
            int pos = 4; 
            for (int i = 10; i <= 90; i=i+10) {
                double tempoBuscaMysql = buscaImagemMysql("imagem/"+i+".jpg",100);
                resultados[pos] = tempoBuscaMysql;
                pos++;
                double tempoBuscaNeo4j = buscaImagemNeo4j("imagem/"+i+".jpg",100); 
                resultados[pos] = tempoBuscaNeo4j;
                pos++; 
            }            
            for (int k = 0; k < resultados.length; k++) {
                System.out.print(resultados[k] +" ");
            }
            System.out.println();
        }
        catch (Exception r) {
            r.printStackTrace();
            System.exit(1);
        }               
    }    
}