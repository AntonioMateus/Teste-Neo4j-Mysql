/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TransactionFailureException;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;


public class MostraRank extends JPanel {
    private static final long serialVersionUID = 1L;
    private String path;
    private JPanel jContentPane = null;
    private JLabel jLabel = null;
    private JLabel jLabel1 = null;
    private JLabel jLabel2 = null;
    private JLabel jLabel3 = null;
    private JLabel jLabel4 = null;
    private JLabel jLabel5 = null;
    private JLabel jLabel6 = null;
    private JLabel jLabel7 = null;
    private JLabel jLabel8 = null;
    private JLabel jLabel9 = null;
    private JLabel jLabel10 = null;
    private JLabel jLabel11 = null;
    private JTextField jTextField = null;
    private JTextField jTextField1 = null;
    private JTextField jTextField2 = null;
    private JTextField jTextField3 = null;
    private JTextField jTextField4 = null;
    private JButton jButton = null;
    private JLabel jLabel12 = null;
    
    public  MostraRank(String path2, String[]cand)  {
	path = path2;
	ImageIcon iconObj = new ImageIcon(path, "fileload");
	ImageIcon icon1 = new ImageIcon(cand[0],"fileload");
        ImageIcon icon2 = new ImageIcon(cand[1], "fileload");
	ImageIcon icon3 = new ImageIcon(cand[2], "fileload");
	ImageIcon icon4 = new ImageIcon(cand[3], "fileload");
	ImageIcon icon5 = new ImageIcon(cand[4],"fileload");
		
	jLabel12 = new JLabel();
	jLabel12.setBounds(new Rectangle(145, 334, 767, 30));
	jLabel12.setFont(new Font("Dialog", Font.PLAIN, 12));
	jLabel12.setText("Pontue de 0 - 10.0 cada uma das imagens retornadas para a Imagem - Objetivo. Sendo 0 a pior imagem retornada e 10.0 a melhor. ");
	jLabel11 = new JLabel(icon5);
	jLabel11.setBounds(new Rectangle(963, 43, 180, 200));
	jLabel11.setText(cand[4]);
	jLabel10 = new JLabel(icon4);
	jLabel10.setBounds(new Rectangle(781, 43, 180, 200));
	jLabel10.setText(cand[3]);
	jLabel9 = new JLabel(icon3);
	jLabel9.setBounds(new Rectangle(598, 43, 180, 200));
	jLabel9.setText(cand[2]);
	jLabel8 = new JLabel(icon2);
	jLabel8.setBounds(new Rectangle(416, 43, 180, 200));
	jLabel8.setText(cand[1]);
	jLabel7 = new JLabel(icon1);
	jLabel7.setBounds(new Rectangle(232, 43, 180, 200));
	jLabel7.setText(cand[0]);
	jLabel6 = new JLabel(iconObj);
	jLabel6.setBounds(new Rectangle(25, 43, 180, 200));
	jLabel6.setText(path);
	jLabel5 = new JLabel();
	jLabel5.setBounds(new Rectangle(986, 16, 129, 22));
	jLabel5.setText("Imagem Candidata: 5");
	jLabel4 = new JLabel();
	jLabel4.setBounds(new Rectangle(805, 16, 130, 21));
	jLabel4.setText("Imagem Candidata: 4");
	jLabel3 = new JLabel();
	jLabel3.setBounds(new Rectangle(619, 15, 127, 17));
	jLabel3.setText("Imagem Candidata: 3");
	jLabel2 = new JLabel();
	jLabel2.setBounds(new Rectangle(439, 14, 134, 19));
	jLabel2.setText("Imagem Candidata: 2");
	jLabel1 = new JLabel();
	jLabel1.setBounds(new Rectangle(262, 15, 119, 19));
	jLabel1.setText("Imagem Candidata: 1");
	jLabel = new JLabel();
	jLabel.setBounds(new Rectangle(34, 12, 116, 20));
	jLabel.setText("Imagem-Objetivo");
	jTextField = new JTextField();
	jTextField.setBounds(new Rectangle(274, 248, 89, 25));
	jTextField1 = new JTextField();
	jTextField1.setBounds(new Rectangle(456, 248,89,25));
	jTextField2 = new JTextField();
	jTextField2.setBounds(new Rectangle(643, 248,89,25));
	jTextField3 = new JTextField();
	jTextField3.setBounds(new Rectangle(832, 248, 89, 25));
	jTextField4 = new JTextField();
	jTextField4.setBounds(new Rectangle(1013, 247, 89, 25));
	jButton = new JButton();
	jButton.setBounds(new Rectangle(519, 285, 98, 27));
	jButton.setText("Submit");
		
	setLayout(null);
	add(jLabel, null);
	add(jLabel1, null);
	add(jLabel2, null);
	add(jLabel3, null);
	add(jLabel4, null);
	add(jLabel5, null);
	add(jLabel6, null);
	add(jLabel7, null);
	add(jLabel8, null);
	add(jLabel9, null);
	add(jLabel10, null);
	add(jLabel11, null);
	add(jTextField, null);
	add(jTextField1, null);
	add(jTextField2, null);
	add(jTextField3, null);
	add(jTextField4, null);
	add(jButton, null);
	add(jLabel12, null);
	
	Botao acao = new Botao();  
        jButton.addActionListener(acao);
		
    }
	
    private class Botao implements ActionListener{
    	// trata evento de bot�o  
        public void actionPerformed( ActionEvent event ){
            double value1 = Double.parseDouble(jTextField.getText());
	    double value2 = Double.parseDouble(jTextField1.getText());
	    double value3 = Double.parseDouble(jTextField2.getText());
	    double value4 = Double.parseDouble(jTextField3.getText());
	    double value5 = Double.parseDouble(jTextField4.getText());
	    String obj = jLabel6.getText();
	    String cand1 = jLabel7.getText();
	    String cand2 = jLabel8.getText();
	    String cand3 = jLabel9.getText();
	    String cand4 = jLabel10.getText();
	    String cand5 = jLabel11.getText();
	    	
	    SistemaFuzzy f = new SistemaFuzzy();
	    	    	
	    double c1 = f.calculaPertinenciaNota(value1);
	    double c2 = f.calculaPertinenciaNota(value2);
	    double c3 = f.calculaPertinenciaNota(value3);
	    double c4 = f.calculaPertinenciaNota(value4);
	    double c5 = f.calculaPertinenciaNota(value5);
	    	    	
            try {
		updateTable(c1, c2, c3, c4, c5, obj, cand1, cand2, cand3, cand4, cand5);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }	
	}   
    }
	
    public void updateTable(double v1, double v2, double v3, double v4, double v5, String path, String c1, String c2, String c3, String c4, String c5)throws Exception{
        /*int id =0;
        double[][] matriz = new double[5][2];
        double []val = {v1,v2,v3,v4,v5};
        String sel = "match (a:image)-[r:imgToImg]->(b:image) where a.pathImage = '"+path+"' AND (b.pathImage = '"+c1+"' OR b.pathImage = '"+c2+"' OR b.pathImage = '"+c3+"' OR b.pathImage = '"+c4+"' OR b.pathImage = '"+c5+"') return a.idImage as idInicio, r.dist as dist, b.idImage as idFim";
        //********************* Conexão com o banco de dados (imagens e caracteristicas) *************************
        GraphDatabaseService x = conn.connect();
        //********************************************************************************************************
        ExecutionEngine stm2 = new ExecutionEngine(x);
        ExecutionEngine stm3 = new ExecutionEngine(x);
        try (Transaction tx = x.beginTx()) {
            ExecutionResult result2 = stm2.execute(sel);
            Iterator<Map<String,Object>> rs2 = result2.iterator();    
            while (rs2.hasNext()) {
                Map<String,Object> imagem = rs2.next();
                for (int i = 0; i < 5; i++) {
                    int id1 = ((Long) imagem.get("idInicio")).intValue();
                    int id2 = ((Long) imagem.get("idFim")).intValue();
                    double value = (Double) imagem.get("dist");
                    String up = "match (a:image)-[r:imgToImg]->(b:image) where a.idImage = "+id1+" AND b.idImage = "+id2+" set r.dist = "+value*val[i]+"";
                    stm3.execute(up);
                }
            }
            tx.success(); 
        }
        finally {
            //********************* desconexão com o banco de dados (imagens e caracteristicas) *********************
            conn.disconnect(x);
            //*******************************************************************************************************
        }
	String nova = "match (a:image)-[r:imgToImg]->(b:image) where a.pathImage = '"+path+"' with b, r.dist as dist return b order by dist limit 5";
        //********************* Conexão com o banco de dados (imagens e caracteristicas) *************************
        x = conn.connect();
        //********************************************************************************************************
        ExecutionEngine insert = new ExecutionEngine(x);
        try (Transaction tx = x.beginTx()) {
            ExecutionResult result3 = insert.execute(nova);
            Iterator<Node> res3 = result3.columnAs("b");
            String[] top = new String[5];
            int i = 0;
            while (res3.hasNext()) {
                Node imagemResultado = res3.next();
                id = ((Long) imagemResultado.getProperty("idImage")).intValue();
                String path2 = (String) imagemResultado.getProperty("pathImage");
                top[i] = path2; 
                i++;
            }
            /*for(int i1= 0; i1<top.length; i1++){
                System.out.println(top[i1]);
            }
            JFrame frame = new JFrame("Search");
            frame.setContentPane(new MostraRank(path,top));
            frame.setSize(1173,415);
            frame.setVisible(true);
            tx.success();
        } 
        finally {
            //********************* Desconexão com o banco de dados (imagens e caracteristicas) **********************
            conn.disconnect(x);
            //********************************************************************************************************
        }*/
    }  			    
        
    private enum TipoRelacionamento implements RelationshipType {
        imgToImg; 
    }
	
    public static void main (String args[]) throws Exception{
        /*int id =0;
        String path = null;
        double[] dif = new double [24];
        double matriz [][] = new double [24][20];
		
        String cypherRandom = "match (n:image) with n, rand() as indAleatorio return n order by indAleatorio limit 1";
        String cypher = "match (a:image)-[r:image_has_feature]->(b:feature) with r.value as value, a.idImage as idImage, b.idFeature as idFeature return value, idImage, idFeature order by idImage";
        //********************* Conexão com o banco de dados (imagens e caracteristicas) *************************
        GraphDatabaseService x = conn.connect();
        //********************************************************************************************************
        ExecutionEngine stm = new ExecutionEngine(x);
        ExecutionResult result, result2, result3;
            
        try (Transaction tx = x.beginTx()){
            result = stm.execute(cypherRandom);
            Iterator<Node> rs = result.columnAs("n");
            result2 = stm.execute(cypher);
            Iterator<Map<String,Object>> rs2 = result2.iterator();
            while (rs.hasNext()) {
                Node image = rs.next(); 
                id = ((Long) image.getProperty("idImage")).intValue();
                path = (String) image.getProperty("pathImage");                
            }
            Map<String,Object> rel = rs2.next(); 
            for (int i = 0; i < 18; i++) {
                for (int j = 0; j < 16; j++) {
                    int id2 = ((Long) rel.get("idImage")).intValue();
                    int feat = ((Long) rel.get("idFeature")).intValue();
                    double value = (Double) rel.get("value");
                    matriz[i][j] = value; 
                    rel = rs2.next(); 
                }
            }
            EuclideanDistance d = new EuclideanDistance();
            dif = d.calculaDistancia(matriz, id);
            ExecutionEngine criacao = new ExecutionEngine(x);
            criacao.execute("optional match (:image)-[r:imgToImg]->(:image) delete r");
            ExecutionResult resultLocal = criacao.execute("match (n:image) where n.idImage = "+id+" return n");
            Iterator<Node> rl = resultLocal.columnAs("n");
            Node inicio = rl.next();
            for (int i = 0; i < 18; i++) {
                //System.out.println(dif[i]);
                resultLocal = criacao.execute("match (n:image) where n.idImage = "+(i+1)+" return n");
                rl = resultLocal.columnAs("n");
                Node fim = rl.next(); 
                Relationship r = inicio.createRelationshipTo(fim, TipoRelacionamento.imgToImg); 
                r.setProperty("dist", dif[i]);
                //r.setProperty("idInicio",id);
                //r.setProperty("idFim", i+1);
            }
            result3 = criacao.execute("match (a:image)-[r:imgToImg]->(b:image) where a.idImage = "+id+" with b, r.dist as dist return b order by dist limit 5");
            Iterator<Node> rs3 = result3.columnAs("b");
            String[] top = new String[5];
            int i =0;
            Node imagemAtual = null; 
            while (rs3.hasNext()) {
                imagemAtual = rs3.next();
                id = ((Long) imagemAtual.getProperty("idImage")).intValue();
                String path2 = (String) imagemAtual.getProperty("pathImage");
                top[i] = path2;
                i++;                     
            }  
            /*for(int i1= 0; i1<top.length; i1++){
                System.out.println(top[i1]);
            }            
            JFrame frame = new JFrame("Search");
            frame.setContentPane(new MostraRank(path,top));
            frame.setSize(1173,415);
            frame.setVisible(true);
            tx.success();
        }
        catch (TransactionFailureException t) {
            //System.out.println("Transacao mal sucedida mas tudo bem...");
        }
        catch (NoSuchElementException n) {
            //System.out.println ("Uma das consultas nao encontrou nada.");
        }
        finally {
            //********************* Conexão com o banco de dados (imagens e caracteristicas) *************************
            conn.disconnect(x);
            //********************************************************************************************************
        }    */                   
    	/***M�todo tempor�rio para popular Banco de dados***/
	    	
    	/*String path= rs.getString("pathImage"); 
    	CalcHistograma c = new CalcHistograma();
	CalcPixel p = new CalcPixel();
	MatrizOcorrencia m1 = new MatrizOcorrencia();
	MomentosInv mi = new MomentosInv();
        String path = rs.getString("pathImage");
        int idPath = rs.getInt("idImage");
		    
        PlanarImage image = JAI.create("fileload",path);
		    
	/*Cor*/ /*ID_Feature = 3, 4, 5*/
	//c.mediaDesvioRGB(path);
	    
	/*ID_FEATURE 9,10,11 */
	// p.mediaDesvioRGB(path);
		
	/*ID_FEATURE = 12,13,14*/
	//p.mediaDesvioCinza(path);
			
	/*ID_FEATURE = 6,7,8*/
	//c.mediaDesvioCinza(path);
		    
	/*Textura*ID_FEATURE = 15,16 */
	//m1.nivelCinza(image);
		    
	/*Forma* ID_FEATURE = 17,18,19,20,21,22,23*/
		    
	/*C�digo usado para inser��o dos valores*/
	/*mi.imagemBinarizada(image);
	double v1,v2,v3,v4,v5,v6,v7;
	v1 = mi.valor1;
	v2 = mi.valor2;
	v3 = mi.valor3;
	v4 = mi.valor4;
	v5 = mi.valor5;
	v6 = mi.valor6;
	v7 = mi.valor7;
	    	    
	Statement insert = x.createStatement();
	int bool = insert.executeUpdate("INSERT INTO mydb.image_has_feature(image_idImage,feature_idFeature,value) VALUES ("+idPath+",17,"+v1+");");
	bool = insert.executeUpdate("INSERT INTO mydb.image_has_feature(image_idImage,feature_idFeature,value) VALUES ("+idPath+",18,"+v2+");");
	bool = insert.executeUpdate("INSERT INTO mydb.image_has_feature(image_idImage,feature_idFeature,value) VALUES ("+idPath+",19,"+v3+");");
	bool = insert.executeUpdate("INSERT INTO mydb.image_has_feature(image_idImage,feature_idFeature,value) VALUES ("+idPath+",20,"+v4+");");
	bool = insert.executeUpdate("INSERT INTO mydb.image_has_feature(image_idImage,feature_idFeature,value) VALUES ("+idPath+",21,"+v5+");");
	bool = insert.executeUpdate("INSERT INTO mydb.image_has_feature(image_idImage,feature_idFeature,value) VALUES ("+idPath+",22,"+v6+");");	    bool = insert.executeUpdate("INSERT INTO mydb.image_has_feature(image_idImage,feature_idFeature,value) VALUES ("+idPath+",23,"+v7+");");
		    
        if(bool!=0){
            System.out.println("Registro inserido com sucesso");
        }else{
            System.out.println("Falha na inser��o");
	}
	} */  
	//conn.disconnect(x);
		
	//}	
    }
}