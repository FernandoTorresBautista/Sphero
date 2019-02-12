/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spheroj;
/**
 * @author t-.-t
 */
import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;

public class PanelDibujo extends JPanel {
    public int cuentaPuntos = 0; // cuenta el número de puntos
    public Point puntos[] = new Point[ 10000 ];
    public Color []PorPuntos = new Color[ 10000 ];
    public Color c = Color.BLACK;
    public PanelDibujo(){
        addMouseMotionListener(
            new MouseMotionAdapter(){
                // almacena las coordenadas de arrastre y vuelve a dibujar
                @Override
                public void mouseDragged( MouseEvent evento ){
                    if ( cuentaPuntos < puntos.length ){
                        puntos[ cuentaPuntos ] = evento.getPoint(); // busca el punto
                        PorPuntos[cuentaPuntos] = c;
                        cuentaPuntos++; // incrementa el número de puntos en el arreglo
                        repaint(); // vuelve a dibujar JFrame
                    } // fin de if
                } // fin del método mouseDragged
            } // fin de la clase interna anónima
        ); // fin de la llamada a addMouseMotionListener
    } // fin del constructor de PanelDibujo
    @Override
    public void paintComponent( Graphics g ){
        super.paintComponent( g ); // borra el área de dibujo
        // dibuja todos los puntos en el arreglo
        for ( int i = 0; i < cuentaPuntos; i++ ){
            if(puntos[i].x != -1){
                g.setColor(PorPuntos[i]);
                g.fillOval( puntos[ i ].x, puntos[ i ].y, 8, 8 );
            }
            //System.out.println("posicion x="+puntos[ i ].x+", y="+puntos[ i ].y + " , total = " + puntos.length);
        }
    } // fin del método paint
    public void resetComponent(){
        repaint();
    }
    public Point[] getArreglo(){
        return puntos;
    }
    public Color[] getArregloC(){
        return PorPuntos;
    }
    public int getTotalPuntos(){
        return cuentaPuntos;
    }
    public void setTotalPuntos(int t){
        cuentaPuntos = t;
    }
    public void resetPuntos(){
        for (int i = 0; i < cuentaPuntos; i++) { 
            puntos[i].x = -1;
            puntos[i].y = -1;
        } 
    }
}

