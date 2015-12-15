/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Antonio Mateus
 */
public class CriacaoScriptAleatorio {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            FileWriter arquivoSaidaCypher = new FileWriter("C:\\Users\\Antonio Mateus\\Desktop\\Iniciação Cientifica\\Iniciação - Fátima\\Sistema da Leila\\script_criacao_cypher_aleatorio.txt"); 
            PrintWriter scriptCriacaoCypher = new PrintWriter(arquivoSaidaCypher);
            FileWriter arquivoSaidaSql = new FileWriter("C:\\Users\\Antonio Mateus\\Desktop\\Iniciação Cientifica\\Iniciação - Fátima\\Sistema da Leila\\script_criacao_sql_aleatorio.txt");
            PrintWriter scriptCriacaoSql = new PrintWriter(arquivoSaidaSql);
            int quantidadeImagens = 1001; 
            
            for (int i = 1; i <= quantidadeImagens; i++) {
                scriptCriacaoCypher.println("CREATE (img"+i+":image {idImage:"+i+", pathImage:'imagem/"+i+".jpg'})");
                scriptCriacaoSql.println("INSERT INTO mydb.image (idImage,pathImage) VALUES ("+i+",'imagem/"+i+".jpg');");
            }
            
            scriptCriacaoCypher.println();
            scriptCriacaoSql.println();
            
            scriptCriacaoCypher.println("CREATE (feat3:feature {idFeature:3, featureName:'Media Histograma RGB', featureType:1})");
            scriptCriacaoCypher.println("CREATE (feat4:feature {idFeature:4, featureName:'Desvio Histograma RGB', featureType:1})");
            scriptCriacaoCypher.println("CREATE (feat5:feature {idFeature:5, featureName:'Inclinacao Histograma RGB', featureType:1})");
            scriptCriacaoCypher.println("CREATE (feat6:feature {idFeature:6, featureName:'Media Histograma NC', featureType:1})");
            scriptCriacaoCypher.println("CREATE (feat7:feature {idFeature:7, featureName:'Desvio Histograma NC', featureType:1})");
            scriptCriacaoCypher.println("CREATE (feat8:feature {idFeature:8, featureName:'Inclinacao Histograma NC', featureType:1})");
            scriptCriacaoCypher.println("CREATE (feat9:feature {idFeature:9, featureName:'Media Pixel RGB', featureType:1})");
            scriptCriacaoCypher.println("CREATE (feat10:feature {idFeature:10, featureName:'Desvio Pixel RGB', featureType:1})");
            scriptCriacaoCypher.println("CREATE (feat11:feature {idFeature:11, featureName:'Inclinacao Pixel RGB', featureType:1})");
            scriptCriacaoCypher.println("CREATE (feat12:feature {idFeature:12, featureName:'Media Pixel NC', featureType:1})");
            scriptCriacaoCypher.println("CREATE (feat13:feature {idFeature:13, featureName:'Desvio Pixel NC', featureType:1})");
            scriptCriacaoCypher.println("CREATE (feat14:feature {idFeature:14, featureName:'Inclinacao Pixel NC', featureType:1})");
            scriptCriacaoCypher.println("CREATE (feat15:feature {idFeature:15, featureName:'Contraste', featureType:2})");
            scriptCriacaoCypher.println("CREATE (feat16:feature {idFeature:16, featureName:'Homogeneidade', featureType:2})");
            scriptCriacaoCypher.println("CREATE (feat17:feature {idFeature:17, featureName:'Momento1', featureType:3})");
            scriptCriacaoCypher.println("CREATE (feat18:feature {idFeature:18, featureName:'Momento2', featureType:3})");
            scriptCriacaoCypher.println("CREATE (feat19:feature {idFeature:19, featureName:'Momento3', featureType:3})");
            
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (3,'Média Histograma RGB',1);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (4,'Desvio Histograma RGB',1);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (5,'Inclinação Histograma RGB',1);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (6,'Média Histograma NC',1);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (7,'DesvioHistograma NC',1);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (8,'Inclinação Histograma NC',1);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (9,'Média Pixel RGB',1);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (10,'Desvio Pixel RGB',1);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (11,'Inclinação Pixel RGB',1);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (12,'Média Pixel NC',1);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (13,'Desvio Pixel NC',1);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (14,'Inclinação Pixel NC',1);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (15,'Contraste',2);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (16,'Homogeneidade',2);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (17,'Momento1',3);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (18,'Momento2',3);");
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (19,'Momento3',3);");
            
            scriptCriacaoCypher.println();
            scriptCriacaoSql.println();
            
            Random geradorValores; 
            DecimalFormat df = new DecimalFormat("###.#######");
            for (int i = 1; i <= quantidadeImagens; i++) {
                geradorValores = new Random(1000); 
                for (int caracteristica = 3; caracteristica <= 19; caracteristica++) {
                    String value = df.format(Math.abs(geradorValores.nextDouble()));
                    scriptCriacaoCypher.println("MATCH (img"+i+":image) MATCH (feat"+caracteristica+":feature) CREATE (img"+i+")-[:image_has_feature {value: "+value+"}]->(feat"+caracteristica+")"); 
                    scriptCriacaoSql.println("INSERT INTO mydb.image_has_feature (image_idFeature,feature_idFeature,value) VALUES ("+i+","+caracteristica+","+value+");");
                }
            }
            
            scriptCriacaoCypher.close();
            arquivoSaidaCypher.close();
        }
        catch (IOException i) {
            System.out.println("Erro ao criar o arquivo de saida");
        }
        catch (Exception e) {
            System.out.println("Algo inesperado aconteceu");
            System.out.println(e.getStackTrace());
        }
    }
}
