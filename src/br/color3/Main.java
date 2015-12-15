/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;

public class Main {
    public static void main (String args[]){
	double[][]matrizMedia = new double[7][8];
	double[][]matrizDesvio = new double[7][8];
	
	String[] path = new String[7];
	path[0] = "imagem/1.jpg";
	path[1] = "imagem/2.jpg";
	path[2] = "imagem/3.jpg";
	path[3] = "imagem/4.jpg";
	path[4] = "imagem/5.jpg";
	path[5] = "imagem/6.jpg";	
	path[6] = "imagem/7.jpg";
		
	double[] vetor1 = new double [2];
	double[] vetor2 = new double [6];
	double[] vetor3 = new double [2];
	double[] vetor4 = new double [6];
	
	CalcHistograma c = new CalcHistograma();
	CalcPixel p = new CalcPixel();

	for(int j = 0; j< path.length; j++){
            vetor1=c.mediaDesvioCinza(path[j]);
            matrizMedia[j][0] = vetor1[0];
            matrizDesvio[j][0] = vetor1[1];
            vetor2 = c.mediaDesvioRGB(path[j]);
            matrizMedia[j][1] = vetor2[0];
            matrizMedia[j][2] = vetor2[1];
            matrizMedia[j][3] = vetor2[2];
            matrizDesvio[j][1] = vetor2[3];
            matrizDesvio[j][2] = vetor2[4];
            matrizDesvio[j][3] = vetor2[5];
			
            vetor3 = p.mediaDesvioCinza(path[j]);
            matrizMedia[j][4] = vetor3[0];
            matrizDesvio[j][4] = vetor3[1];
	
            vetor4 = p.mediaDesvioRGB(path[j]);
            matrizMedia[j][5] = vetor4[0];
            matrizMedia[j][6] = vetor4[1];
            matrizMedia[j][7] = vetor4[2];
            matrizDesvio[j][5] = vetor4[3];
            matrizDesvio[j][6] = vetor4[4];
            matrizDesvio[j][7] = vetor4[5];
	}
	//System.out.println("Imprimindo matriz!!!");
	/*for(int l = 0; l < 7; l++){
		for(int d = 0; d< 8; d++){
			System.out.print(matrizMedia[l][d]+ "  ");
		}
		System.out.println(" ");
	}*/
	
	/*EuclideanDistance e = new EuclideanDistance(matrizMedia);
	e.calculaDistancia(matrizMedia);*/
    }
}