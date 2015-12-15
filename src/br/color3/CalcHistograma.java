/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;

import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.Histogram;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;

public class CalcHistograma {
    /*static
    {
    System.setProperty("com.sun.media.jai.disableMediaLib", "true");
    }*/
    public double media, desvio, incl;
    public double mediaR, mediaG, mediaB ;
    public double desvioR, desvioG, desvioB;
    public double inclR, inclG, inclB;
	
    public double[] mediaDesvioCinza(String path){
	double sum = 0;
	double mediaIn =0;
	media = 0;
	desvio = 0;
	incl=0;
	double sumDesvio = 0;
	double sumIncl = 0;
		
	/* Transforma em cinza*
	 * Equa��o de Lumin�ncia:
	 * Rela��o entre XYZ e RGB+1. Sa�da = uma banda, por isso uma linha*/
	double[][] bandCombine = {{0.5f,0.71f,0.25f,0.0f}};
	double[] vetorCaracteristicas = new double [2];
	PlanarImage image = JAI.create("fileload",path);
	
	/*Transformando em n�veis de cinza*/
	PlanarImage imageBand = JAI.create("BandCombine",image, bandCombine);
	ParameterBlock pb = new ParameterBlock();
        pb.addSource(imageBand);
	pb.add(null); // The ROI.
        pb.add(1); // Samplings.
        pb.add(1);
        pb.add(new int[]{256}); // Num. bins.
        pb.add(new double[]{0}); // Min value to be considered.
        pb.add(new double[]{256}); // Max value to be considered.
        
	/*Cria o histograma*/
	PlanarImage temp = JAI.create("histogram", pb);
	Histogram h = (Histogram)temp.getProperty("histogram");
	   
	for (int i= 0; i < h.getNumBands(); i++) {
            for (int j = 0; j< h.getNumBins(i); j++) {      
	        sum = sum + (h.getBinSize(i, j)*j);
            }
	}		
	mediaIn = (sum/(image.getHeight()*image.getWidth()-1));
	media = normalizeCores(mediaIn);
	
	for (int i= 0; i < h.getNumBands(); i++) {      
	    for (int j = 0; j< h.getNumBins(i); j++) {
                sumDesvio =sumDesvio + Math.pow((Math.abs(h.getBinSize(i, j)-media)),2);
            }
	}		    
	desvio = Math.sqrt(sumDesvio/256);
	
	for (int i= 0; i < h.getNumBands(); i++) {      
            for (int j = 0; j< h.getNumBins(i); j++) {
                sumIncl =sumIncl + Math.pow((Math.abs(h.getBinSize(i, j)-media)),3);
            }
	}	
	incl = Math.cbrt(sumIncl/256);
		
	//System.out.println("*****M�dia e Dist�ncia em Histograma em NIVEL DE CINZA da imagem " + path +"****");
	//System.out.println("Media = " + media);
	//System.out.println("Desvio = " + desvio);
	//System.out.println("Inclina��o = " + incl);
	
	vetorCaracteristicas[0]=media;
	vetorCaracteristicas[1]=desvio;
	
	return vetorCaracteristicas;
    }

    public double[] mediaDesvioRGB(String path){
	double sumR=0, sumG=0, sumB =0;
	double mediaInR =0, mediaInG=0, mediaInB=0 ;
	mediaR =0;
	mediaG=0;
	mediaB=0;
	double sumDesvioR = 0, sumDesvioG = 0, sumDesvioB = 0;
	desvioR=0;
	desvioG=0;
	desvioB=0;
	double sumInclR=0, sumInclG=0, sumInclB=0;
	inclR=0;
	inclG=0; 
	inclB=0;
	
	double[] vetor = new double[6];
	PlanarImage image = JAI.create("fileload",path);
	ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(null); // The ROI.
        pb.add(1); // Samplings.
        pb.add(1);
        pb.add(new int[]{256}); // Num. bins.
        pb.add(new double[]{0}); // Min value to be considered.
        pb.add(new double[]{256}); // Max value to be considered.
	  	    
        /*Cria o histograma*/
    	PlanarImage temp = JAI.create("histogram", pb);
	Histogram h = (Histogram)temp.getProperty("histogram");
	      
	for (int j = 0; j < 256; j++) { 
            sumR = sumR + (h.getBinSize(0, j)*j);
	}
	for (int j = 0; j < 256; j++) { 
	    sumG = sumG + (h.getBinSize(1, j)*j);
	}
	for (int j = 0; j < 256; j++) { 
	    sumB = sumB + (h.getBinSize(2, j)*j);   
	}
        //System.out.println("SUM R: "+ sumR+ "SUM G: "+ sumG + "SUM B: "+ sumB);
        //System.out.println("Area = "+ image.getHeight()*image.getWidth());
        mediaInR = sumR/(image.getHeight()*image.getWidth());
        mediaInG = sumG/(image.getHeight()*image.getWidth());
        mediaInB = sumB/(image.getHeight()*image.getWidth());
    
        mediaR = normalizeCores(mediaInR);
        mediaG = normalizeCores(mediaInG);
        mediaB = normalizeCores(mediaInB);
        
	for (int i = 0; i < h.getNumBands(); i++) {      
	    for (int j = 0; j < h.getNumBins(i); j++) { 
                sumDesvioR =sumDesvioR + Math.pow(Math.abs((h.getBinSize(0, j)-mediaR)),2);		    	 ;
	    	sumDesvioG =sumDesvioG + Math.pow(Math.abs((h.getBinSize(1, j)-mediaG)),2);
	    	sumDesvioB =sumDesvioB + Math.pow(Math.abs((h.getBinSize(2, j)-mediaB)),2);
	    }
        }
        
	if(sumDesvioR == 0){
            desvioR = 0.0;
            desvioG = Math.sqrt(sumDesvioG/(image.getHeight()*image.getWidth()-1));
            desvioB = Math.sqrt(sumDesvioB/(image.getHeight()*image.getWidth()-1));
	}else if(sumDesvioG == 0){
            desvioR = Math.sqrt(sumDesvioR/(image.getHeight()*image.getWidth()-1));
            desvioG = 0.0;
            desvioB = Math.sqrt(sumDesvioB/(image.getHeight()*image.getWidth()-1));
	}else if(sumDesvioB == 0){
            desvioR = Math.sqrt(sumDesvioR/(image.getHeight()*image.getWidth()-1));
            desvioG = Math.sqrt(sumDesvioG/(image.getHeight()*image.getWidth()-1));
            desvioB = 0.0;
	}else{
            desvioR = Math.sqrt(sumDesvioR/(image.getHeight()*image.getWidth()-1));
            desvioG = Math.sqrt(sumDesvioG/(image.getHeight()*image.getWidth()-1));
            desvioB = Math.sqrt(sumDesvioB/(image.getHeight()*image.getWidth()-1));
	}
	
	for (int i = 0; i < h.getNumBands(); i++) {      
	     for (int j = 0; j < h.getNumBins(i); j++) { 
	    	 sumInclR =sumInclR + Math.pow(Math.abs((h.getBinSize(0, j)-mediaR)),3);		    	 ;
	    	 sumInclG =sumInclG + Math.pow(Math.abs((h.getBinSize(1, j)-mediaG)),3);
	    	 sumInclB =sumInclB + Math.pow(Math.abs((h.getBinSize(2, j)-mediaB)),3);
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
	
	//System.out.println("*****M�dia e Dist�ncia em Histograma em RGB da imagem "+ path +"****");
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
        CalcHistograma c = new CalcHistograma();
        c.mediaDesvioCinza("xxxx");
        c.mediaDesvioRGB("xxxx");
    }
}