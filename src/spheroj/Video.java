/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spheroj;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author t-.-t
 */
public class Video extends JFrame {
    JPanel panel;
    JLabel etiqueta;
    PanelDibujo Dibujo;
    public Video(){
        setTitle("_Reconocimiento Sphero_");
        setLocation(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600,400);
        setResizable(true);
        setVisible(true);
        
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        etiqueta = new JLabel();
        panel.add(etiqueta);
        //setContentPane(panel);
        getContentPane().add(panel);
        
        Dibujo = new PanelDibujo();
        panel.add(Dibujo);
        //getContentPane().add(Dibujo);
        
    }
 
    public void setImage(Image imagen){
        panel.removeAll();
 
        ImageIcon icon = new ImageIcon(imagen.getScaledInstance(etiqueta.getWidth(), etiqueta.getHeight(), Image.SCALE_SMOOTH));
        etiqueta.setIcon(icon);
 
        panel.add(etiqueta);
        panel.updateUI();
    }
    
}
