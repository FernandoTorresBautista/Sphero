/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spheroj;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import static org.opencv.imgcodecs.Imgcodecs.imencode;
import org.opencv.videoio.VideoCapture;
import static org.opencv.imgproc.Imgproc.rectangle;
import org.opencv.objdetect.CascadeClassifier;

/**
 *
 * @author t-.-t
 */
public class Panel_video {
    CascadeClassifier spheroDetector = new CascadeClassifier("C:\\OpenCV\\3.1.0\\opencv\\build\\x64\\vc12\\bin\\data8_Bueno\\cascade.xml");
    //CascadeClassifier spheroDetector = new CascadeClassifier("C:\\OpenCV\\3.1.0\\opencv\\build\\x64\\vc12\\bin\\cascade.xml");
    MatOfRect spheroDetections = new MatOfRect();
    //VideoCapture cap = new VideoCapture(1);//Para video usb
    VideoCapture cap = new VideoCapture(0);//Para video de camara de laptop
    Mat imagen=new Mat();
    
    public void run() {
        Video ventana = new Video();
        if(cap.isOpened()){
            while(true){
                try {
                    //Thread.sleep(100);
                    cap.read(imagen);
                    if(!imagen.empty()){
                        spheroDetector.detectMultiScale(imagen, spheroDetections);
                        for (Rect rect : spheroDetections.toArray()) {
                            rectangle(imagen, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                        }
                        ventana.setImage(convertir(imagen));
                    }
                } catch (Exception ex) {
                    Logger.getLogger( Panel_video.class.getName()).log(Level.SEVERE, null, ex );
                }
            }
        }
    } 
    private Image convertir(Mat imagen) {
        MatOfByte matOfByte = new MatOfByte();
        imencode(".jpg", imagen, matOfByte); 
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (Image)bufImage;
    }
    
}
