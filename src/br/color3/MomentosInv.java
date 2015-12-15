/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import com.sun.media.jai.widget.DisplayJAI;

public class MomentosInv {
    public double valor1, valor2, valor3, valor4, valor5, valor6, valor7;
    public void imagemBinarizada(PlanarImage image){
        double[][] bandCombine = {{0.3f,0.59f,0.11f,0.0f}}; 
    	PlanarImage imageBand = JAI.create("BandCombine",image, bandCombine);
	PlanarImage imageBin = JAI.create("binarize",imageBand,70.0);
	int [][]matriz = new int[imageBin.getHeight()][imageBin.getWidth()];
	int[] pixel = new int[1];
	int [][] matrizTeste = { {0,0,1,1}, {0,0,1,1}, {0,0,1,1}, {0,0,1,1} };
        int l, alt;
	BufferedImage imageB = imageBin.getAsBufferedImage();
        WritableRaster rs = imageB.getRaster();
        l=rs.getHeight();
        alt=rs.getWidth();
        
        for(int i = 0; i< rs.getHeight(); i++){
            for(int j = 0; j< rs.getWidth(); j++){
                rs.getPixel(j,i, pixel);
                matriz[i][j] = pixel[0];
            }
        }
        MomentosInv m = new MomentosInv();
        valor1 = m.escolheNorma(matriz,1,l,alt);
        valor2 = m.escolheNorma(matriz,2,l,alt);
        valor3 = m.escolheNorma(matriz,3,l,alt);
        /*double soma = valor1 + valor2 +valor3 +valor4 +valor5 + valor6 + valor7;
        System.out.println("Valores" + soma);*/
        //System.out.println(valor1);
        //System.out.println(valor2);
        //System.out.println(valor3);
        /*JFrame frame = new JFrame("Binarizada");
        DisplayJAI display = new DisplayJAI(imageBin);
        frame.getContentPane().add(new JScrollPane(display));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200,300);
        frame.setVisible(true);*/
    }
	
    double calculaMomento(int[][]matriz, int p,int q,int l, int alt){
        double sum = 0.0;
	for(int i = 0; i<l; i++){
            for(int j = 0; j< alt; j++){
                sum += ((Math.pow(i, p))*(Math.pow(j,q))*matriz[i][j]);
	    }
	}
	return sum;
    }
	
    double calculaCentral(int[][] matriz,int p,int q, int l, int alt){
	double xl, yl;
	double sum = 0.0;
	xl = calculaMomento(matriz,1,0,l,alt)/calculaMomento(matriz,0,0,l,alt);
	yl = calculaMomento(matriz,0,1,l,alt)/calculaMomento(matriz,0,0,l,alt);
        for(int i = 0; i< l; i++){
            for(int j = 0; j< alt; j++){
                sum += ((Math.pow((matriz[i][j]- xl),p))* (Math.pow((matriz[i][j]-yl),q))*matriz[i][j]);
	    }
	}	
	return sum;
    }
	
    double norma(int[][]matriz, int p, int q, int l, int alt){
    	double central =0;
	central = (calculaCentral(matriz,p,q, l, alt)/(Math.pow(calculaCentral(matriz,0,0,l,alt),(((p+q)/2)+1))));
	return central;
    }
	
    double escolheNorma(int[][]matriz,int opt, int l, int alt){
	double valor=0.0;
	switch(opt){
            case 1:
		valor=0;
		valor=norma(matriz,2,0,l,alt)+norma(matriz,0,2,l,alt);
		break;
            case 2:
		valor=0;
		valor=Math.pow((norma(matriz,2,0,l,alt)-norma(matriz,0,2,l,alt)),2)+(4*Math.pow(norma(matriz,1,1,l,alt), 2));
		break;
            case 3:
		valor=0;
		valor=Math.pow((norma(matriz,3,0,l,alt)-3*norma(matriz,1,2,l,alt)),2)+ Math.pow(3*norma(matriz,2,1,l,alt)-(norma(matriz,0,3,l,alt)),2);
		valor = valor/10000.0;
		break;
            case 4:
		valor=0;
		valor=Math.pow((norma(matriz,3,0,l,alt)+norma(matriz,1,2,l,alt)),2)
		+ Math.pow((norma(matriz,0,3,l,alt)+norma(matriz,2,1,l,alt)),2);
		valor = valor/10000.0;
		break;
            case 5:
		valor=0;
		valor=(3*norma(matriz,3,0,l,alt)-3*norma(matriz,1,2,l,alt))*(norma(matriz,3,0,l,alt)+norma(matriz,1,2,l,alt))*
		((Math.pow((norma(matriz,3,0,l,alt)+norma(matriz,1,2,l,alt)),2)- 3*(Math.pow((norma(matriz,2,1,l,alt)+norma(matriz,0,3,l,alt)),2))))+
		(3*norma(matriz,2,1,l,alt)-norma(matriz,0,3,l,alt))*(norma(matriz,2,1,l,alt)-norma(matriz,0,3,l,alt))*
		(3*(Math.pow((norma(matriz,3,0,l,alt)+norma(matriz,1,2,l,alt)),2))- Math.pow((norma(matriz,2,1,l,alt)+norma(matriz,0,3,l,alt)),2));
		valor = valor /5000.0;
            case 6:
		valor=0;
		valor=(norma(matriz,2,0,l,alt)-norma(matriz,0,2,l,alt))*((Math.pow((norma(matriz,3,0,l,alt)+norma(matriz,1,2,l,alt)),2)- Math.pow((norma(matriz,2,1,l,alt)+norma(matriz,0,3,l,alt)),2)))+
		4*norma(matriz,1,1,l,alt)*(norma(matriz,3,0,l,alt)+norma(matriz,1,2,l,alt))*(norma(matriz,2,1,l,alt)+norma(matriz,0,3,l,alt));
		valor = valor /5000.0;
		break;
            case 7:
		valor=0;
		valor=(3*norma(matriz,2,1,l,alt)-3*norma(matriz,0,3,l,alt))*(norma(matriz,3,0,l,alt)+norma(matriz,1,2,l,alt))*
		((Math.pow((norma(matriz,3,0,l,alt)+norma(matriz,1,2,l,alt)),2)- 3*(Math.pow((norma(matriz,2,1,l,alt)+norma(matriz,0,3,l,alt)),2))))+
		(3*norma(matriz,1,2,l,alt)-norma(matriz,3,0,l,alt))*(norma(matriz,2,1,l,alt)-norma(matriz,0,3,l,alt))*
		(3*(Math.pow((norma(matriz,3,0,l,alt)+norma(matriz,1,2,l,alt)),2))- Math.pow((norma(matriz,2,1,l,alt)+norma(matriz,3,0,l,alt)),2));
		valor = valor/10000000;
		break;
            default:
		//System.out.println("Valor inv�lido");
		break;
        }
	return valor;
    }

    public static void main(String args[]){
	MomentosInv m = new MomentosInv();
	//System.out.println("L2");
        PlanarImage image = JAI.create("fileload","imagem/24.jpg");
        m.imagemBinarizada(image);
    }
}