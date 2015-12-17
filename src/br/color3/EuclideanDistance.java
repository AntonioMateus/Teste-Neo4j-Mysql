/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;

import javax.media.jai.Histogram;


public class EuclideanDistance {
    public double [] calculaDistancia(double[][] matriz, int id){
	double[] diferenca = new double [matriz.length];
	id = id-1;
	for(int i=0;i <matriz.length;i++) {
            double sum = 0;
            double raiz = 0;
            for(int j=0;j<matriz[0].length;j++)
            {
                sum = sum + (Math.pow(((matriz[id][j]) - (matriz[i][j])),2));
            }
		
            raiz = Math.sqrt(sum);
            diferenca[i] = raiz;	
	}
	//System.out.println("Diferen�a!");
	/*for(int k=0;k <24;k++){
            System.out.println(diferenca[k]);
	}*/
	return diferenca;
    }
}