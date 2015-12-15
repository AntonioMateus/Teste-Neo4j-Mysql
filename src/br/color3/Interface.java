/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.color3;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Rectangle;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;

public class Interface extends JFrame {
    private static final long serialVersionUID = 1L;
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
    /**
     * This is the default constructor
     */
    public Interface() {
	super();
	initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
    	this.setSize(1173, 413);
    	this.setContentPane(getJContentPane());
	this.setTitle("Search");
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
    	if (jContentPane == null) {
            jLabel12 = new JLabel();
            jLabel12.setBounds(new Rectangle(145, 334, 767, 30));
            jLabel12.setFont(new Font("Dialog", Font.PLAIN, 12));
            jLabel12.setText("Pontue de 0 - 10.0 cada uma das imagens retornadas para a Imagem - Objetivo. Sendo 0 a pior imagem retornada e 10.0 a melhor. ");
            jLabel11 = new JLabel();
            jLabel11.setBounds(new Rectangle(963, 43, 180, 200));
            jLabel11.setText("JLabel");
            jLabel10 = new JLabel();
            jLabel10.setBounds(new Rectangle(781, 43, 180, 200));
            jLabel10.setText("JLabel");
            jLabel9 = new JLabel();
            jLabel9.setBounds(new Rectangle(598, 43, 180, 200));
            jLabel9.setText("JLabel");
            jLabel8 = new JLabel();
            jLabel8.setBounds(new Rectangle(416, 43, 180, 200));
            jLabel8.setText("JLabel");
            jLabel7 = new JLabel();
            jLabel7.setBounds(new Rectangle(232, 43, 180, 200));
            jLabel7.setText("JLabel");
            jLabel6 = new JLabel();
            jLabel6.setBounds(new Rectangle(25, 43, 180, 200));
            jLabel6.setText("JLabel");
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
            jContentPane = new JPanel();
            jContentPane.setLayout(null);
            jContentPane.add(jLabel, null);
            jContentPane.add(jLabel1, null);
            jContentPane.add(jLabel2, null);
            jContentPane.add(jLabel3, null);
            jContentPane.add(jLabel4, null);
            jContentPane.add(jLabel5, null);
            jContentPane.add(jLabel6, null);
            jContentPane.add(jLabel7, null);
            jContentPane.add(jLabel8, null);
            jContentPane.add(jLabel9, null);
            jContentPane.add(jLabel10, null);
            jContentPane.add(jLabel11, null);
            jContentPane.add(getJTextField(), null);
            jContentPane.add(getJTextField1(), null);
            jContentPane.add(getJTextField2(), null);
            jContentPane.add(getJTextField3(), null);
            jContentPane.add(getJTextField4(), null);
            jContentPane.add(getJButton(), null);
            jContentPane.add(jLabel12, null);
        }
	return jContentPane;
    }

    /**
     * This method initializes jTextField	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextField() {
    	if (jTextField == null) {
            jTextField = new JTextField();
            jTextField.setBounds(new Rectangle(274, 248, 89, 25));
        }
	return jTextField;
    }

    /**
     * This method initializes jTextField1	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextField1() {
    	if (jTextField1 == null) {
            jTextField1 = new JTextField();
            jTextField1.setBounds(new Rectangle(456, 248,89,25));
	}
	return jTextField1;
    }

    /**
     * This method initializes jTextField2	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextField2() {
    	if (jTextField2 == null) {
            jTextField2 = new JTextField();
            jTextField2.setBounds(new Rectangle(643, 248,89,25));
	}
	return jTextField2;
    }

    /**
     * This method initializes jTextField3	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextField3() {
    	if (jTextField3 == null) {
            jTextField3 = new JTextField();
            jTextField3.setBounds(new Rectangle(832, 248, 89, 25));
	}
	return jTextField3;
    }

    /**
     * This method initializes jTextField4	
     * 	
     * @return javax.swing.JTextField	
     */
    private JTextField getJTextField4() {
    	if (jTextField4 == null) {
            jTextField4 = new JTextField();
            jTextField4.setBounds(new Rectangle(1013, 247, 89, 25));
	}
	return jTextField4;
    }

    /**
     * This method initializes jButton	
     * 	
     * @return javax.swing.JButton	
     */
    private JButton getJButton() {
    	if (jButton == null) {
            jButton = new JButton();
            jButton.setBounds(new Rectangle(519, 285, 98, 27));
            jButton.setText("Submit");
	}
	return jButton;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"