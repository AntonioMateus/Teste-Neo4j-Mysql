/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;
//Leitura de arquivos txt (scripts cypher)
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
//Conexao com o banco de dados Mysql
import java.sql.Connection;
import java.sql.SQLException;
//Conexao com o banco de dados Neo4j
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
//Escrita de arquivos csv
import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileWriter;
import java.sql.ResultSet;
//Busca por imagens no banco de dados Mysql
import java.sql.Statement;
import java.text.DecimalFormat;
//Busca por imagens no banco de dados Neo4j
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.TransactionFailureException;

/**
 *
 * @author Antonio Mateus
 */
public class TesteDesempenho {
    private static double criaEstruturasNeo4j() {
        long instanteInicio = 0;
        long instanteFim = instanteInicio;
        GraphDatabaseService x = conn.connect();
        BufferedReader br = null;
        try (Transaction tx = x.beginTx()) {            
            br = new BufferedReader(new FileReader("C:\\Users\\Antonio Mateus\\Desktop\\Iniciação Cientifica\\Iniciação - Fátima\\Sistema da Leila\\script_criacao_cypher_aleatorio.txt"));
            String linhaAtual = null;
            instanteInicio = System.currentTimeMillis();
            ExecutionEngine stm = new ExecutionEngine(x);
            stm.execute ("optional match (n) optional match (n)-[r]-() delete n,r");
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
            String cypher = "match (a:image)-[r:image_has_feature]->(b:feature) with r.value as value, a.idImage as idImage, b.idFeature as idFeature return value, idImage, idFeature order by idImage, idFeature";
            Iterator<Map<String,Object>> rs = stm.execute(cypher).iterator();
            Map<String,Object> rel = rs.next(); 
            for (int i = 0; i < 1001; i++) {
                for (int j = 0; j < 17; j++) {
                    matriz[i][j] = (Double) rel.get("value"); 
                    rel = rs.next(); 
                }
            }
            EuclideanDistance d = new EuclideanDistance();
            for (int id = 1; id <= 1001; id++) {
                double[] dif = new double [1001];
                dif = d.calculaDistancia(matriz, id);
                Iterator<Node> rl = stm.execute("match (n:image) where n.idImage = "+id+" return n").columnAs("n");
                Node inicio = rl.next();
                for (int i = 0; i < 1001; i++) {
                    //System.out.println(dif[i]);
                    if (id != i+1) {
                        rl = stm.execute("match (n:image) where n.idImage = "+(i+1)+" return n").columnAs("n");
                        Node fim = rl.next(); 
                        Relationship r = inicio.createRelationshipTo(fim, TipoRel.imgToImg); 
                        r.setProperty("dist", dif[i]);
                    }
                }
            }
            //--------------------------------------------------------
            tx.success();             
            instanteFim = System.currentTimeMillis();
            //System.out.println("Transacao ocorrida com sucesso");
        }
        catch (IOException e) {
            //System.out.println("Erro ao abrir o script para leitura");
        }
        finally {
            try {
                if (br != null) br.close();
                conn.disconnect(x);                
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
            /*criacao das estruturas*/ 
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
            /*insercao das imagens*/
            /*Statement insercao_imagens = con.createStatement();
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (1,'imagem/1.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (2,'imagem/2.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (3,'imagem/3.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (4,'imagem/4.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (5,'imagem/5.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (6,'imagem/6.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (7,'imagem/7.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (8,'imagem/8.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (9,'imagem/9.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (10,'imagem/10.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (11,'imagem/11.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (12,'imagem/12.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (13,'imagem/13.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (14,'imagem/14.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (15,'imagem/15.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (16,'imagem/16.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (17,'imagem/17.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (18,'imagem/18.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (19,'imagem/19.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (20,'imagem/20.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (21,'imagem/21.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (22,'imagem/22.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (23,'imagem/23.jpg');");
            insercao_imagens.execute("INSERT INTO mydb.image (idImage,pathImage) VALUES (24,'imagem/24.jpg');");
            /*insercao das caracteristicas
            Statement insercao_caracteristicas = con.createStatement();
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (3,'Média Histograma RGB',1);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (4,'Desvio Histograma RGB',1);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (5,'Inclinação Histograma RGB',1);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (6,'Média Histograma NC',1);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (7,'DesvioHistograma NC',1);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (8,'Inclinação Histograma NC',1);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (9,'Média Pixel RGB',1);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (10,'Desvio Pixel RGB',1);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (11,'Inclinação Pixel RGB',1);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (12,'Média Pixel NC',1);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (13,'Desvio Pixel NC',1);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (14,'Inclinação Pixel NC',1);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (15,'Contraste',2);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (16,'Homogeneidade',2);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (17,'Momento1',3);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (18,'Momento2',3);");
            insercao_caracteristicas.execute("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (19,'Momento3',3);");
            /*insercao dos relacionamentos "imagem tem caracteristica" 
            Statement insercao_relacionamentos = con.createStatement();
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (1,3,0.4311922);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (1,4,30.1384151);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (1,6,0.6319380);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (1,7,375.1533993);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (1,9,0.4311922);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (1,10,88.9033759);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (1,12,0.6319205);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (1,13,124.3698069);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (1,15,0.0692350);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (1,16,0.9631052);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (1,17,0.6145174);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (1,18,0.3776316);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (1,19,0.6678720);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (2,3,0.4298470);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (2,4,29.6550131);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (2,6,0.6332702);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (2,7,367.3143515);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (2,9,0.4298470);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (2,10,91.2380869);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (2,12,0.6332526);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (2,13,127.3685164);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (2,15,0.0700620);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (2,16,0.9626769);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (2,17,0.6225626);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (2,18,0.3875842);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (2,19,0.6937968);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (3,3,0.4303763);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (3,4,29.5870549);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (3,6,0.6349922);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (3,7,366.0390379);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (3,9,0.4303763);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (3,10,92.5709094);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (3,12,0.6349746);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (3,13,127.3667944);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (3,15,0.0665945);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (3,16,0.9645577);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (3,17,0.6356807);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (3,18,0.4040900);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (3,19,0.7345260);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,3,0.3922256);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,4,31.2358757);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,5,80.7996748);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,6,0.5705665);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,7,353.8125086);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,8,485.9952636);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,9,0.3922256);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,10,67.6087134);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,11,58.2762208);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,12,0.5705507);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,13,95.4307748);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,14,646.7400759);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,15,0.0706742);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,16,0.9627793);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,17,0.5838484);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,18,0.3408790);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (4,19,0.5701175);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (5,3,0.3909855);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (5,4,30.9515681);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (5,6,0.5703494);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (5,7,357.9096327);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (5,9,0.3909855);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (5,10,69.6099813);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (5,12,0.5703336);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (5,13,98.4310336);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (5,15,0.0681280);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (5,16,0.9640736);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (5,17,0.5866224);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (5,18,0.3441259);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (5,19,0.5709948);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (6,3,0.3927271);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (6,4,31.0426032);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (6,6,0.5698305);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (6,7,365.3480218);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (6,9,0.3927271);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (6,10,67.9415499);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (6,12,0.5698147);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (6,13,97.4315386);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (6,15,0.0680442);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (6,16,0.9637989);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (6,17,0.5867081);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (6,18,0.3442264);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (6,19,0.5717498);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (7,3,0.4630120);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (7,4,31.8543030);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (7,6,0.6997843);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (7,7,310.1945640);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (7,9,0.4630120);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (7,10,97.5383427);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (7,12,0.6997649);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (7,13,135.3021143);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (7,15,0.0759512);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (7,16,0.9583939);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (7,17,0.5678486);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (7,18,0.3224520);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (7,19,0.5681715);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,3,0.4629473);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,4,31.8137129);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,5,83.1712050);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,6,0.7004588);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,7,315.2294463);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,8,485.9952636);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,9,0.4629473);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,10,96.5383935);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,11,76.2375256);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,12,0.7004393);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,13,132.3013982);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,14,147.8308125);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,15,0.0774539);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,16,0.9576490);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,17,0.5703883);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,18,0.3253428);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (8,19,0.5763300);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (9,3,0.4505117);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (9,4,32.4295763);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (9,6,0.6916402);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (9,7,266.7990990);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (9,9,0.4505117);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (9,10,99.8842089);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (9,12,0.6916210);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (9,13,137.3102861);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (9,15,0.0716941);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (9,16,0.9610102);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (9,17,0.5805285);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (9,18,0.3370134);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (9,19,0.5956243);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,3,0.3861427);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,4,31.2333098);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,5,78.2068372);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,6,0.5721591);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,7,341.3071859);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,8,485.9952636);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,9,0.3861427);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,10,22.2808334);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,11,93.3088050);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,12,0.5721432);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,13,28.4282516);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,14,179.2164002);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,15,0.0601556);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,16,0.9675838);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,17,0.6358272);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,18,0.4042762);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (10,19,0.6743959);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (11,3,0.3876258);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (11,4,30.6532045);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (11,6,0.5759675);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (11,7,331.3416929);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (11,9,0.3876258);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (11,10,21.6126744);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (11,12,0.5759515);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (11,13,29.4244572);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (11,15,0.0610184);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (11,16,0.9672067);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (11,17,0.6268839);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (11,18,0.3929834);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (11,19,0.6481353);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (12,3,0.3873944);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (12,4,30.4598455);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (12,6,0.5744155);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (12,7,333.6092783);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (12,9,0.3873944);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (12,10,18.6128641);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (12,12,0.5743995);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (12,13,24.4259397);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (12,15,0.0623154);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (12,16,0.9665503);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (12,17,0.6267039);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (12,18,0.3927577);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (12,19,0.6453864);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (13,3,0.3587257);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (13,4,37.7067044);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (13,6,0.5444279);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (13,7,286.1410756);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (13,9,0.3587257);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (13,10,110.3094731);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (13,12,0.5444128);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (13,13,154.4577325);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (13,15,0.0885199);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (13,16,0.9539618);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (13,17,0.6308553);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (13,18,0.3979784);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (13,19,0.6121259);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,3,0.3536837);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,4,37.6529080);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,5,81.6632366);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,6,0.5404021);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,7,275.7899720);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,8,628.8762090);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,9,0.3536837);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,10,107.6478114);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,11,17.2794323);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,12,0.5403870);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,13,149.4616888);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,14,506.6573810);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,15,0.0865379);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,16,0.9549302);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,17,0.6440549);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,18,0.4148067);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (14,19,0.6533088);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (15,3,0.3516890);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (15,4,37.9000611);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (15,6,0.5371912);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (15,7,281.8802529);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (15,9,0.3516890);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (15,10,106.6497923);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (15,12,0.5371763);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (15,13,148.4648857);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (15,15,0.0813939);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (15,16,0.9575140);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (15,17,0.6410584);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (15,18,0.4109558);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (15,19,0.6414663);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (16,3,0.3069650);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (16,4,30.6777209);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (16,6,0.4467692);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (16,7,277.0898131);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (16,9,0.3069650);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (16,10,26.0267298);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (16,12,0.4467568);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (16,13,33.5537092);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (16,15,0.0529725);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (16,16,0.9719460);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (16,17,0.8793007);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (16,18,0.7731696);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (16,19,1.1806250);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (17,3,0.3097156);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (17,4,30.9701380);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (17,6,0.4475871);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (17,7,284.5315298);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (17,9,0.3097156);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (17,10,17.3571921);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (17,12,0.4475747);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (17,13,24.5527664);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (17,15,0.0526629);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (17,16,0.9722440);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (17,17,0.8238131);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (17,18,0.6786681);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (17,19,1.0029059);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (18,3,0.3007965);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (18,4,31.5757011);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (18,6,0.4397399);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (18,7,284.4035579);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (18,9,0.3007965);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (18,10,22.0328428);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (18,12,0.4397276);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (18,13,27.5606551);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (18,15,0.0545070);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (18,16,0.9712896);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (18,17,0.8258291);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (18,18,0.6819937);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (18,19,0.9942350);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (19,3,0.3519039);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (19,4,31.2371067);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (19,6,0.5190931);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (19,7,258.7170592);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (19,9,0.3519039);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (19,10,111.6496468);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (19,12,0.5190787);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (19,13,106.4824003);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (19,15,0.0539306);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (19,16,0.9707821);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (19,17,0.7070180);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (19,18,0.4998745);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (19,19,0.9060990);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (20,3,0.3427150);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (20,4,31.2082067);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (20,6,0.5126918);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (20,7,223.8789911);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (20,9,0.3427150);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (20,10,119.6589470);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (20,12,0.5126775);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (20,13,109.4888432);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (20,15,0.0491980);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (20,16,0.9741062);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (20,17,0.7333988);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (20,18,0.5378737);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (20,19,1.0031917);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (21,3,0.3433551);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (21,4,31.8311351);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (21,6,0.5128826);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (21,7,225.1094854);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (21,9,0.3433551);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (21,10,166.9922976);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (21,12,0.5128684);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (21,13,171.4895134);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (21,15,0.0484149);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (21,16,0.9745112);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (21,17,0.7342535);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (21,18,0.5391282);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (21,19,1.0108598);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (22,3,0.3696967);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (22,4,27.4314893);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (22,6,0.5434399);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (22,7,264.4758077);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (22,9,0.3696967);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (22,10,24.6306454);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (22,12,0.5434248);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (22,13,36.4570815);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (22,15,0.0753818);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (22,16,0.9587523);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (22,17,0.6006461);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (22,18,0.3607758);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (22,19,0.5106935);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,3,0.3561050);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,4,27.7024307);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,5,83.1712050);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,6,0.5261353);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,7,235.0002587);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,8,523.9874852);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,9,0.3561050);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,10,24.6442373);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,11,76.2375256);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,12,0.5261207);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,13,36.4743859);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,14,496.2531345);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,15,0.0781661);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,16,0.9574441);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,17,0.6463239);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,18,0.4177346);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (23,19,0.6146094);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (24,3,0.3630632);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (24,4,27.5007578);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (24,6,0.5367114);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (24,7,247.3763937);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (24,9,0.3630632);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (24,10,23.6372651);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (24,12,0.5366965);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (24,13,34.4637822);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (24,15,0.0778449);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (24,16,0.9577328);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (24,17,0.6101729);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (24,18,0.3723110);");
            insercao_relacionamentos.execute("INSERT INTO mydb.image_has_feature (image_idImage,feature_idFeature,value) VALUES (24,19,0.5251356);");
            */ 
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
    
    private enum TipoRel implements RelationshipType {
        imgToImg; 
    }
    
    private static double buscaImagemNeo4j(String caminhoImagem, int quantidadeSemelhantes) {
        long instanteInicial = 0;
        long instanteFim = instanteInicial;
        int id =0;
        String path = null;
        
        /*double[] dif = new double [24];
        double matriz [][] = new double [24][20];*/
        
        String cypherRandom = "match (n:image) where n.pathImage = '"+caminhoImagem+"' return n";
        //String cypher = "match (a:image)-[r:image_has_feature]->(b:feature) with r.value as value, a.idImage as idImage, b.idFeature as idFeature return value, idImage, idFeature order by idImage";
        
        GraphDatabaseService x = conn.connect();
        
        ExecutionEngine stm = new ExecutionEngine(x);
        ExecutionResult result, result2;
        instanteInicial = System.currentTimeMillis();    
        try (Transaction tx = x.beginTx()){
            Iterator<Node> rs = stm.execute(cypherRandom).columnAs("n");
            //result2 = stm.execute(cypher);
            //Iterator<Map<String,Object>> rs2 = result2.iterator();
            if (rs.hasNext()) {
                Node image = rs.next(); 
                id = ((Long) image.getProperty("idImage")).intValue();
                path = (String) image.getProperty("pathImage");         
            }
            
            /*Map<String,Object> rel = rs2.next(); 
            for (int i = 0; i < 18; i++) {
                for (int j = 0; j < 16; j++) {
                    int id2 = ((Long) rel.get("idImage")).intValue();
                    int feat = ((Long) rel.get("idFeature")).intValue();
                    double value = (Double) rel.get("value");
                    matriz[i][j] = value; 
                    rel = rs2.next(); 
                }
            }
            EuclideanDistance d = new EuclideanDistance();
            dif = d.calculaDistancia(matriz, id);*/
            //criacao.execute("optional match (:image)-[r:imgToImg]->(:image) delete r");
            //ExecutionResult resultLocal = criacao.execute("match (n:image) where n.idImage = "+id+" return n");
            //Iterator<Node> rl = resultLocal.columnAs("n");
            //Node inicio = rl.next();
            /*for (int i = 0; i < 18; i++) {
                //System.out.println(dif[i]);
                resultLocal = criacao.execute("match (n:image) where n.idImage = "+(i+1)+" return n");
                rl = resultLocal.columnAs("n");
                Node fim = rl.next(); 
                Relationship r = inicio.createRelationshipTo(fim, TipoRel.imgToImg); 
                r.setProperty("dist", dif[i]);
            }*/
            
            Iterator<Node> rs2 = stm.execute("match (a:image)-[r:imgToImg]->(b:image) where a.idImage = "+id+" with b, r.dist as dist return b order by dist limit " +String.valueOf(quantidadeSemelhantes)).columnAs("b");
            //String[] top = new String[quantidadeSemelhantes];
            //int i =0;
            //Node imagemAtual = null; 
            while (rs2.hasNext()) {
                rs2.next();
                //String path2 = (String) imagemAtual.getProperty("pathImage");
                //top[i] = path2;
                //i++;                     
            }  
            /*for(int i1= 0; i1<top.length; i1++){
                System.out.println(top[i1]);
            }*/
            tx.success();
        }
        catch (TransactionFailureException t) {
            //System.out.println("Transacao mal sucedida mas tudo bem...");
            return 0;
        }
        catch (NoSuchElementException n) {
            //n.printStackTrace(); 
            return 0; 
        }
        finally {
            instanteFim = System.currentTimeMillis();
            //********************* Conexão com o banco de dados (imagens e caracteristicas) *************************
            conn.disconnect(x);
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
            //String caminhoArquivoSaida = "C:\\Users\\Antonio Mateus\\Desktop\\Iniciação Cientifica\\Iniciação - Fátima\\teste_comparativo_apos_modificacao ("+numeroIteracoes+" iteracoes).csv";
            //writer = new CSVWriter(new FileWriter(caminhoArquivoSaida),';');
            //writer.writeNext(new String[]{"Operacao","quantidade de imagens retornadas","Mysql (em s)","Neo4j (em s)"});
            double tempoEstruturasMysql = criaEstruturasMysql();
            resultados[0] = tempoEstruturasMysql;
            //System.out.println("Estruturas do mysql criadas");
            double tempoEstruturasNeo4j = criaEstruturasNeo4j();
            resultados[1] = tempoEstruturasNeo4j; 
            //System.out.println("Estruturas do neo4j criadas");
            //writer.writeNext(new String[]{"Criacao das estrutruras","nao se aplica",mediaEstruturasMysql,mediaEstruturasNeo4j});            
            resultados[2] = buscaImagemMysql("imagem/1.jpg", 100);
            resultados[3] = buscaImagemNeo4j("imagem/1.jpg", 100);
            int pos = 4; 
            for (int i = 10; i <= 90; i=i+10) {
                //System.out.println("******** Imagem "+i+" ****************");
                double tempoBuscaMysql = buscaImagemMysql("imagem/"+i+".jpg", 100);
                resultados[pos] = tempoBuscaMysql;
                pos++;
                //System.out.println("Quantidade de imagens buscadas com sucesso (Mysql): "+j);
                double tempoBuscaNeo4j = buscaImagemNeo4j("imagem/"+i+".jpg", 100); 
                resultados[pos] = tempoBuscaNeo4j;
                //System.out.println("Quantidade de imagens buscadas com sucesso (Neo4j): "+j);
                //writer.writeNext(new String[]{"Busca pela imagem "+i+"", ""+j, mediaBuscaMysql, mediaBuscaNeo4j});
                pos++; 
            }
            
            for (int k = 0; k < resultados.length; k++) {
                System.out.print(resultados[k] +" ");
            }
            System.out.println();
        }
        /*catch (IOException i) {
            //System.out.println("Problemas ao escrever o arquivo \"csv\"");
        }*/
        catch (RuntimeException r) {
            r.printStackTrace();
            System.exit(1);
        }
        /*finally {
            try {
                if (writer != null) writer.close();
            }
            catch (IOException i) {
                //System.out.println("Problemas ao fechar o escritor");
            }
        } */        
    }    
}