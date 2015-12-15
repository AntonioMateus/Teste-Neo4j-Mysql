/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;
public class SistemaFuzzy {
    public double calculaPertinenciaNota(double nota){
        double pertNB=0, pertNM=0, pertNA=0;
	/*Funcao trapeizodal para Nota Baixa
	 * Trapezio notaBaixa(x:0,0,1.5,4)*/
	if(nota<0.0) pertNB =0.0;
	if(0.0<=nota && nota<=1.5) pertNB = 1.0;
	if(1.5<nota && nota<4.0) pertNB = ((4.0-nota)/(4.0-1.5));
	if(nota>=4.0) pertNB=0.0;
	
	/*Funcao triangular para Nota M�dia
	 * Triangulo notaMedia(x: 3,6,8)*/
	if(nota<=3.0) pertNM =0.0;
	if(3.0< nota && nota<6.0)pertNM = ((nota-3.0)/(6.0-3.0));
	if(6.0<=nota && nota<8.0)pertNM = ((8.0-nota)/(8.0-6.0));
	if(nota>=8.0) pertNM=0.0;
	
	/*Funcao trapeizodal para Nota Alta
	 * Trapezio notaAlta(x: 7,8.5,10,10))*/
	if(nota<=7.0) pertNA =0.0;
	if(7.0< nota && nota<8.5)pertNA = ((nota-7.0)/(8.5-7.0));
	if(8.5<=nota && nota<=10.0)pertNA = 1.0;
	if(nota>10.0) pertNA=0.0;
		
	//System.out.println("NB: " + pertNB + "NM: "+ pertNM + " NA: "+ pertNA);
	double c = calculaPeso(pertNB,pertNM,pertNA);
	return c;
    }
	
    public double calculaPeso(double pertB, double pertM, double pertA){
	/*Funcao trapeizodal para Relevancia Baixa
	 * Trapezio relevanciaBaixa(x:0.65,0.85,1,1)*/
	/*Formula do trapezio*/
	/*if(peso<=0.65) pertRB =0.0;
	if(0.65<peso && peso<0.85) pertRB =((peso-0.65)/(0.85-0.65));
	if(0.85<=peso && peso<=1.0) pertRB = 1.0;
	if(peso>=1.0) pertRB=0.0;*/
		
	//Encontrar pontos para formar base menor//
	double p1B = ((pertB*(8.5-6.5))+6.5);
	double p2B = 10.0; 
		
	//Area de Relevancia Baixa//
	double areaB = (pertB*((3.5+Math.abs(p1B-p2B))/2));
	//Centroide eixo X//
	double centB = (((10-6.5)/2)+6.5);
	
	/*Funcao triangular para Relevancia Media
	 * Triangulo relevanciaM�dia(x:0.1,0.5,0.9)*/
	/*Formula do triangulo, com corte da funcao de pertinencia, vira um trapezio*/
	/*if(peso<=0.1) pertRM =0.0;
	if(0.1< peso && peso<0.5)pertRM = ((peso-0.1)/(0.5-0.1));
	if(0.5<=peso && peso<0.9)pertRM = ((0.9-peso)/(0.9-0.5));
	if(peso>=0.9) pertRM=0.0;*/
	
	//Encontrar pontos para formar base menor//
	double p1M = (pertM*(5.0-1.0)+1.0);
	double p2M = (pertM*(9.0-5.0)-9.0)*-1; 
	
	//Area de Relevancia Media//
	double areaM = (pertM*((8.0+Math.abs(p1M-p2M))/2));
	//Centroide eixo X//
	double centM = (((9.0-1.0)/2)+1.0);
						
	/*Funcao trapeizodal para Relevancia Alta
	 * Trapezio relevanciaAlta(x: 0,0,0.2,0.3))*/
	/*Formula do trapezio*/
	/*if(peso<0.0) pertRA =0.0;
	if(0.0<=peso && peso<=0.2) pertRA =1.0;
	if(0.2<peso && peso<0.3) pertRA = ((0.3-peso)/(0.3-0.2));
	if(peso>=0.3) pertRA=0.0;*/
		
	//Encontrar pontos para formar base menor//
	double p1A = 0.0;
	double p2A = (pertA*(3.0-2.0)-3.0)*-1; 
		
	//Area de Relevancia Media//
	double areaA = (pertA*((3.0+Math.abs(p1A-p2A))/2));
	
	//Centroide eixo X//
	double centA = ((3.0-0.0)/2);
		
	double centroide = (((centB*areaB)+(centM*areaM)+(centA*areaA))/(areaB+areaM+areaA));
	//System.out.println("centroide: "+centroide/10);
	return centroide;
    }
}