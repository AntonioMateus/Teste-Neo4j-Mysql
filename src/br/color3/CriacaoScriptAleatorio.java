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
            //******** Parte mais importante do código ******************
            int quantidadeImagens = 501; 
            //***********************************************************
            String criacaoImagensCypher = "CREATE ";
            String criacaoImagensSql = "INSERT INTO mydb.image (idImage,pathImage) VALUES ";
            int i;
            for (i = 1; i < quantidadeImagens; i++) {
                criacaoImagensCypher = criacaoImagensCypher + "(img"+i+":image {idImage:"+i+", pathImage:'imagem/"+i+".jpg'}), ";
                criacaoImagensSql = criacaoImagensSql + "("+i+",'imagem/"+i+".jpg'), ";
            }
            criacaoImagensCypher = criacaoImagensCypher + "(img"+quantidadeImagens+":image {idImage:"+quantidadeImagens+", pathImage:'imagem/"+quantidadeImagens+".jpg'})";
            criacaoImagensSql = criacaoImagensSql + "("+quantidadeImagens+",'imagem/"+quantidadeImagens+".jpg');";
            scriptCriacaoSql.println(criacaoImagensSql);
            scriptCriacaoCypher.println(criacaoImagensCypher);
            
            scriptCriacaoCypher.println();
            scriptCriacaoSql.println();
            
            scriptCriacaoSql.println("INSERT INTO mydb.feature (idFeature,featureName,featureType) VALUES (3,'Média Histograma RGB',1), "+
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
            
            scriptCriacaoCypher.println("CREATE (feat3:feature {idFeature:3, featureName:'Media Histograma RGB', featureType:1}), "+
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
            
            scriptCriacaoCypher.println();
            scriptCriacaoSql.println();
            
            Random geradorValores = new Random(); 
            DecimalFormat df = new DecimalFormat("###.#######");
            int j;
            double[] valores = new double[quantidadeImagens*19];
            for (int k = 0; k < valores.length; k++) {
                valores[k] = Math.abs(geradorValores.nextDouble());
                //valores[k] = df.format(Math.abs(geradorValores.nextDouble())).replace(',', '.'); 
            }
            int cont = 0;
            for (j = 1; j <= quantidadeImagens; j++) {
                String consultas = "MATCH (img"+j+":image {idImage: "+j+"}) ";
                String criacoesCypher = "";
                String criacoesSql = "INSERT INTO mydb.image_has_feature (image_idFeature,feature_idFeature,value) VALUES ";
                for (int caracteristica = 3; caracteristica < 19; caracteristica++) {
                    //value = String.valueOf(Math.abs(geradorValores.nextDouble()));
                    consultas = consultas + "MATCH (feat"+caracteristica+":feature {idFeature: "+caracteristica+"}) ";
                    criacoesCypher = criacoesCypher + "CREATE (img"+j+")-[:image_has_feature {value: "+valores[cont]+"}]->(feat"+caracteristica+") ";
                    criacoesSql = criacoesSql + "("+j+","+caracteristica+","+valores[cont]+"), ";
                    cont++; 
                }
                //value = String.valueOf(Math.abs(geradorValores.nextDouble()));
                consultas = consultas + "MATCH (feat19:feature {idFeature: 19}) ";
                criacoesCypher = criacoesCypher + "CREATE (img"+j+")-[:image_has_feature {value: "+valores[cont]+"}]->(feat19)";
                criacoesSql = criacoesSql + "("+j+",19,"+valores[cont]+");"; 
                scriptCriacaoSql.println(criacoesSql);
                scriptCriacaoCypher.println(consultas + criacoesCypher);
                cont++; 
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
