/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.ParameterBlock;

import javax.imageio.ImageIO;
import javax.media.jai.*;
import javax.swing.*;

import com.sun.media.jai.widget.DisplayJAI;

public class MatrizOcorrencia {
    public double contraste, hom;
    PlanarImage nivelCinza(PlanarImage im) {
        /*Transformando em n�veis de cinza
        * Valores do bandCombine de acor*/
        double[][] bandCombine = {{0.3f,0.59f,0.11f,0.0f}};
        int[] pixel = new int[256];
        int[] cores = {32,0,0};
        int[] cores1 = {96,0,0};
        int[] cores2 = {160,0,0};
        int[] cores3 = {232,0,0};
        int [][] matrizPrimeira = new int[im.getHeight()][im.getWidth()];
        int [][] matrizSegunda = new int [im.getHeight()][im.getWidth()];
        int [][] matrizSegundaFim = new int [4][4];
        /* Matriz de Teste para verificar algoritmo.
        * Resposta:       4312
        *                 3420
        *                 1282
        *                 2024
        * Resposta Entropia : -11,8656
        */
        int [][] matrizTeste = { {2,1,0,1}, {1,4,3,0}, {0,3,6,0}, {1,0,0,2} };
        PlanarImage imageBand = JAI.create("BandCombine",im, bandCombine);
        BufferedImage image = imageBand.getAsBufferedImage();
        WritableRaster rs = image.getRaster();
        for(int i = 0; i< rs.getHeight(); i++){
            for(int j = 0; j< rs.getWidth(); j++){
                rs.getPixel(j,i, pixel);
                if(pixel[0]<64){
                    rs.setPixel(j,i,cores);
                    matrizPrimeira[i][j] = 0;
                }else if(pixel[0]<128){
                    rs.setPixel(j,i,cores1);
                    matrizPrimeira[i][j] = 1;
                }else if(pixel[0]<192){
                    rs.setPixel(j,i,cores2);
                    matrizPrimeira[i][j] = 2;
                }else if(pixel[0]<256){
                    rs.setPixel(j,i,cores3);
                    matrizPrimeira[i][j] = 3;
                }
            }
        }

        MatrizOcorrencia m = new MatrizOcorrencia();
        matrizSegunda = m.calculaMatriz(matrizPrimeira, im.getHeight(), im.getWidth());

        /*Transforma para matriz 4x4*/
        for( int i = 0; i<4; i++){
            for(int j = 0; j<4; j++){
                matrizSegundaFim[i][j] = matrizSegunda[i][j];
                //System.out.print(matrizSegundaFim[i][j]+"  ");
            }
            //System.out.println(" ");
        }
        contraste = m.calculaContraste(matrizSegundaFim);
        //System.out.println("Contraste= "+ contraste);

        hom = m.calculaHomogeneo(matrizSegundaFim);
        //System.out.println("Homogeneidade= "+ hom);

        /*Mostrando em um frame simples*/
        /*JFrame frame = new JFrame("Nivel de Cinza");
        DisplayJAI display = new DisplayJAI(image);
        frame.getContentPane().add(new JScrollPane(display));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200,300);
        frame.setVisible(true);*/

        return imageBand;
    }
    
    int[][] calculaMatriz(int[][] matriz, int largura, int alt){
        int count;
        int [][] matrizFim = new int[largura][alt];
        /*Valores de dist�ncia e angulo s�o definidos manualmente por enquanto.
         * Aqui distancia = 1 e angulo = O�*/
        
        for(int i = 0; i< largura; i++){
            for(int j = 0; j<alt; j++){
            	count = 0;
               	for(int aux= 0; aux<largura; aux++){
                    for(int k= 0; k<alt; k++){
            		if(k>0){
                            if(matriz[aux][k] == i && matriz[aux][k-1] == j){
                                count++;
                            }
                            if(k<alt-1){
                                if (matriz[aux][k] == i && matriz[aux][k+1]==j){
                                    count++;
                		}
                            }
                	}else{
                            if (matriz[aux][k] == i && matriz[aux][k+1]==j){
                            	count++;
                            }
            		}
                    }
               	}
                matrizFim[i][j]=count;	
            }
       }
       return matrizFim;
    }
    
    public double calculaContraste(int[][] matriz){
    	double sumComponentes = 0.0;
    	double sumSub = 0;
    	
    	double[][] normalizada = new double[4][4];
    	double[][] normal = new double[4][4];
    	for(int i=0; i<matriz.length; i++){
            for(int j=0; j<matriz.length; j++){
    		sumComponentes += matriz[i][j];
            }
    	}
    	for(int i=0; i<matriz.length; i++){
            for(int j=0; j<matriz.length; j++){
            	normalizada[i][j] = (matriz[i][j]/sumComponentes);
            }
    	}    	
    	for(int i=0; i<matriz.length; i++){
            for(int j=0; j<matriz.length; j++){
    		if(j<matriz.length-1){
                    sumSub += Math.pow((i - j),2)*normalizada[i][j];
                }else{
                    sumSub += Math.pow((normalizada[i][j]-0),2)*normalizada[i][j];
    		}
            }
    	}
        return sumSub;
    }
    
    public double calculaHomogeneo(int[][] matriz){
    	double sumComponentes = 0.0;
    	double[][] normalizada = new double[4][4];
    	double sum = 0.0;
    	
    	for(int i=0; i<matriz.length; i++){
            for(int j=0; j<matriz.length; j++){
    		sumComponentes += matriz[i][j];
            }
    	}
        for(int i=0; i<matriz.length; i++){
            for(int j=0; j<matriz.length; j++){
    		normalizada[i][j] = (matriz[i][j]/sumComponentes);
            }
    	}
    	for(int i=0; i<matriz.length; i++){
            for(int j=0; j<matriz.length; j++){
    		sum += (normalizada[i][j]/(1 + Math.abs(i-j)));
            }
    	}
    	return sum;
    }
    
    public static void main(String[] args){
        MatrizOcorrencia m = new MatrizOcorrencia();
        PlanarImage image = JAI.create("fileload","imagem/negra.jpg");
        m.nivelCinza(image);
    }   
}