/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;

import java.awt.image.Raster;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

public class CalcPixel {
    public double media, desvio, incl;
    public double mediaR, mediaG, mediaB;
    public double desvioR, desvioG, desvioB;
    public double inclR, inclG, inclB;
	
    int[] cores = new int [3];
	
    public CalcPixel(){
    }
	
    public double[] mediaDesvioCinza(String path){
	double sum = 0;
	double sumDesvio = 0;
	double sumIncl = 0;
	double mediaIn;
		
	PlanarImage image = JAI.create("fileload",path);
	double[] vetorCaracteristicas = new double [2];
	double[][] bandCombine = {{0.5f,0.71f,0.25f,0.0f}};
	PlanarImage bandObj = JAI.create("BandCombine",image, bandCombine);
				
	Raster rs = bandObj.getData();
	for (int x = 0; x <bandObj.getWidth(); x++) {      
	     for (int y = 0; y < bandObj.getHeight(); y++) {      
	         rs.getPixel(x, y, cores); // captura da combina��o de cor do pixel  "Cores � um VETOR" 
	         sum = sum + cores[0];
	     }
	}
	mediaIn = sum/(bandObj.getHeight()*bandObj.getWidth());
	media = normalizeCores(mediaIn);
	for (int x = 0; x < bandObj.getWidth(); x++) {      
	     for (int y = 0; y < bandObj.getHeight(); y++) { 
        	 sumDesvio = sumDesvio+ Math.pow(Math.abs((cores[0]-media)),2);
	     }
	}
		
	desvio = Math.sqrt(sumDesvio/(bandObj.getHeight()*bandObj.getWidth()-1));
	
	for (int x = 0; x < bandObj.getWidth(); x++) {      
	    for (int y = 0; y < bandObj.getHeight(); y++) { 
                sumIncl =sumIncl + Math.pow((Math.abs(cores[0]-media)),3);
            }
	}	
	incl = Math.cbrt(sumIncl/256);
		
	//System.out.println("*****M�dia e Dist�ncia em Pixel em NIVEL DE CINZA da imagem "+ path +"****");
	//System.out.println("Media = "+ media);
	//System.out.println("Desvio = "+ desvio);
	//System.out.println("Inclina��o = "+ incl);
	
	vetorCaracteristicas[0]=media;
	vetorCaracteristicas[1]=desvio;
	
	return vetorCaracteristicas;
    }
	
    public double[] mediaDesvioRGB(String path){
	double sumR=0, sumG=0, sumB =0;
        double mediaInR, mediaInG, mediaInB;
	double sumDesvioR = 0, sumDesvioG = 0, sumDesvioB = 0;
	double sumInclR=0, sumInclG=0, sumInclB=0;
	
	double [] vetor = new double[6];
	PlanarImage image = JAI.create("fileload",path);
	Raster rs = image.getData();
	for (int x = 0; x <image.getWidth(); x++) {      
            for (int y = 0; y < image.getHeight(); y++) {      
	        rs.getPixel(x, y, cores); // captura da combina��o de cor do pixel  "Cores � um VETOR"    
	        sumR =sumR+ cores[0];
	        sumG =sumG+ cores[1];
	        sumB =sumB+ cores[2];
	    }
	}
		
	mediaInR = sumR/(image.getHeight()*image.getWidth());
	mediaInB = sumB/(image.getHeight()*image.getWidth());
	mediaInG = sumG/(image.getHeight()*image.getWidth());
	
	mediaR = normalizeCores(mediaInR);
	mediaG = normalizeCores(mediaInG);
	mediaB = normalizeCores(mediaInB);
	
	for (int x = 0; x < image.getWidth(); x++) {      
	    for (int y = 0; y < image.getHeight(); y++) { 
                sumDesvioR =sumDesvioR + Math.pow(Math.abs((cores[0]-mediaR)),2);		    	 ;
	    	sumDesvioG =sumDesvioG + Math.pow(Math.abs((cores[1]-mediaG)),2);
		sumDesvioB =sumDesvioB + Math.pow(Math.abs((cores[0]-mediaB)),2);
            }
	}
	desvioR = Math.sqrt(sumDesvioR/(image.getHeight()*image.getWidth()-1));
	desvioG = Math.sqrt(sumDesvioG/(image.getHeight()*image.getWidth()-1));
	desvioB = Math.sqrt(sumDesvioB/(image.getHeight()*image.getWidth()-1));
	
	for (int x = 0; x < image.getWidth(); x++) {      
	    for (int y = 0; y < image.getHeight(); y++) { 
		sumInclR =sumInclR + Math.pow(Math.abs((cores[0]-mediaR)),3);		    	 ;
	    	sumInclG =sumInclG + Math.pow(Math.abs((cores[1]-mediaG)),3);
		sumInclB =sumInclB + Math.pow(Math.abs((cores[2]-mediaB)),3);
            }
	}
	if(sumInclR == 0){
            inclR = 0.0;
            inclG = Math.cbrt(sumDesvioG/(image.getHeight()*image.getWidth()-1));
            inclB = Math.cbrt(sumDesvioB/(image.getHeight()*image.getWidth()-1));
	}else if(sumInclG == 0){
            inclR = Math.cbrt(sumInclR/(image.getHeight()*image.getWidth()-1));
            inclG = 0.0;
            inclB = Math.cbrt(sumInclB/(image.getHeight()*image.getWidth()-1));
	}else if(sumInclB == 0){
            inclR = Math.cbrt(sumInclR/(image.getHeight()*image.getWidth()-1));
            inclG = Math.cbrt(sumInclG/(image.getHeight()*image.getWidth()-1));
            inclB = 0.0;
	}else{
            inclR = Math.cbrt(sumInclR/(image.getHeight()*image.getWidth()-1));
            inclG = Math.cbrt(sumInclG/(image.getHeight()*image.getWidth()-1));
            inclB = Math.cbrt(sumInclB/(image.getHeight()*image.getWidth()-1));
	}
		
	//System.out.println("*****M�dia e Dist�ncia em Pixel em RGB da imagem "+ path +"****");
	//System.out.println("Media = R: "+ mediaR + " G: "+ mediaG+ " B: "+ mediaB);
	//System.out.println("Desvio = R: "+ desvioR + " G: "+ desvioG+ " B: "+ desvioB);
	//System.out.println("Inclina��o = R: "+ inclR + " G: "+ inclG+ " B: "+ inclB);
	vetor[0] = mediaR;
	vetor[1] = mediaG;
	vetor[2] = mediaB;
	vetor[3] = desvioR;
	vetor[4] = desvioG;
	vetor[5] = desvioB;
	
	return vetor;
    }
	
    public double normalizeCores(double media){
	media = (media*1.0)/256;
	return media;
    }

    public static void main (String args[]){
        CalcPixel c = new CalcPixel();
        c.mediaDesvioCinza("xxxx");
        c.mediaDesvioRGB("xxxx");
    }
}