/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;

import au.com.bytecode.opencsv.CSVWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 *
 * @author Antonio Mateus
 */
public class ClasseTransformadora {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String caminhoArquivoEntrada = "C:\\Users\\Antonio Mateus\\Documents\\NetBeansProjects\\Color3\\dist\\relatorio_auxiliar.txt";
        double[][] matrizEntrada = new double[30][22];
        String linhaAtual = null; 
        try {
            BufferedReader br = new BufferedReader(new FileReader(caminhoArquivoEntrada));
            linhaAtual = br.readLine(); 
            int k = 0; 
            while (linhaAtual != null && !linhaAtual.equals("")) {
                String[] valoresLinhaAtual = linhaAtual.split(" "); 
                for (int j = 0; j < 22; j++) {
                    matrizEntrada[k][j] = Double.valueOf(valoresLinhaAtual[j]);
                }
                linhaAtual = br.readLine();
                k++;
            }
            String[] resultados = new String[22]; 
            DecimalFormat df = new DecimalFormat("####.##");
            for (int j = 0; j < 22; j++) {
                double soma = 0; 
                for (int i = 0; i < 30; i++) {
                    soma = soma + matrizEntrada[i][j]; 
                }
                resultados[j] = df.format(soma/30);
            }
            String caminhoArquivoSaida = "C:\\Users\\Antonio Mateus\\Desktop\\Iniciação Cientifica\\Iniciação - Fátima\\teste_comparativo_apos_modificacao(30 iteracoes e 100 imagens semelhantes).csv";
            CSVWriter writer = new CSVWriter(new FileWriter(caminhoArquivoSaida),';');
            writer.writeNext(new String[]{"Operacao","quantidade de imagens retornadas","Mysql (em s)","Neo4j (em s)"});
            writer.writeNext(new String[]{"Criacao das estrutruras","nao se aplica",resultados[0],resultados[1]});
            writer.writeNext(new String[]{"Busca pela imagem 1", "100", resultados[2], resultados[3]});
            int pos = 4; 
            //writer.writeNext(new String[]{"Busca pela imagem 1", "1", resultados[2], resultados[3]});
            for (int i = 10; i <= 90; i=i+10) {
                String mediaBuscaMysql = resultados[pos]; 
                pos++; 
                String mediaBuscaNeo4j = resultados[pos]; 
                writer.writeNext(new String[]{"Busca pela imagem "+i+"", "100", mediaBuscaMysql, mediaBuscaNeo4j});
                pos++; 
            }
            writer.close();
        }
        catch (IOException i) {
            System.out.println("Houve problemas ao ler o arquivo de entrada ou criar o arquivo de saida");
            //i.printStackTrace();
        }
    }   
}