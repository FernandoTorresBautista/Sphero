package spheroj;

import java.awt.*;
import java.awt.Color;
import java.awt.event.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.opencv.core.Core;
import se.nicklasgavelin.bluetooth.Bluetooth;
import se.nicklasgavelin.bluetooth.Bluetooth.EVENT;
import se.nicklasgavelin.bluetooth.BluetoothDevice;
import se.nicklasgavelin.bluetooth.BluetoothDiscoveryListener;
import se.nicklasgavelin.sphero.Robot;
import se.nicklasgavelin.sphero.RobotListener;
import se.nicklasgavelin.sphero.command.CommandMessage;
import se.nicklasgavelin.sphero.command.FrontLEDCommand;
import se.nicklasgavelin.sphero.exception.InvalidRobotAddressException;
import se.nicklasgavelin.sphero.exception.RobotBluetoothException;
import se.nicklasgavelin.sphero.response.ResponseMessage;
import se.nicklasgavelin.sphero.response.InformationResponseMessage;

public class SpheroJ extends JFrame {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
    private static final long serialVersionUID = 6998786554264771793L;	
    // Internal storage
    private int responses = 0;
    private static PanelDibujo Dibujo;
    private ConnectThread ct;
    private FlowLayout esquema;
    private Container contenedor ;
    private final JButton connectButton, disconnectButton, sphero_trace, reset;
    private final String colores[] = {"Azul", "Magenta", "Amarillo", "Verde", "Gris", "Rosa", "Naranja"};
    private final Color colores2[] = {Color.BLUE, Color.MAGENTA, Color.YELLOW, Color.GREEN, Color.GRAY, Color.PINK, Color.ORANGE};
    private Robot r;
    private final JComboBox combColor;
    private JPanel panel1, panel2;
    
    private Panel_video panelvid;
    
    /**
    * Main method
    * @param args Will be ignored
    */
    @SuppressWarnings( "unused" )
    public static void main( String[] args ){
        
        SpheroJ My_API = new SpheroJ();
        My_API.setVisible(true);
        
    }
    /**
    * Our example application
    */
    @SuppressWarnings("Convert2Lambda")
    public SpheroJ(){
        super( "API SPHERO" );
        
        panel1 = new JPanel();
        panel2 = new JPanel();
        super.getContentPane().setLayout( null );
        
        this.esquema = new FlowLayout();
        this.contenedor = new Container();
        
        //esquema = new FlowLayout( );
        //contenedor = super.getContentPane();
        //super.setLayout( esquema );
        
        getContentPane().add(panel1);
        panel1.setBounds( new Rectangle(10, 10, 490, 35) );
        panel1.setBorder( BorderFactory.createLineBorder( Color.GRAY ));
        
        getContentPane().add(panel2);
        panel2.setBounds( new Rectangle( 9, 49, 492, 301) );
        panel2.setBorder( BorderFactory.createLineBorder( Color.GRAY ));
        
        //panel = new PanelVideo();
        //panel.setBounds(new Rectangle(10, 50, 490, 300) );
        //panel2.add(panel);
        //this.add(panel);
        //panel.run();
        
        Dibujo = new PanelDibujo();
        Dibujo.setBounds( new Rectangle(10, 50, 490, 300));
        //panel2.add(Dibujo);
        this.add(Dibujo);
        
            
        connectButton = new JButton( "Conectar" );
        disconnectButton = new JButton( "Desconectar" );
        sphero_trace = new JButton( "Recorrer" );
        reset = new JButton ( "Limpiar" );
        
        // Acciones para nuestro boton de conectar
        connectButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ){
                //esquema.setAlignment( FlowLayout.CENTER );
                //esquema.layoutContainer(contenedor);
                // Check if we have something previous to stop
                if( ct != null )
                    ct.stopThread();
                // Create a new thread
                ct = new ConnectThread();
                ct.start();
                //connectButton.setEnabled( false );
                //disconnectButton.setEnabled( true );
            }
	} );
        // Acciones para nuestro bot+on de desconectar
        disconnectButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ){
                //esquema.setAlignment( FlowLayout.CENTER );
                //esquema.layoutContainer(contenedor);
                // Check if we have something to stop
                if( ct != null )
                    ct.stopThread();
                //connectButton.setEnabled( true );
                //disconnectButton.setEnabled( false );
            }
        } );
        // Acciones para el boton de recorrido del dibujo
        sphero_trace.addActionListener( new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                //esquema.setAlignment( FlowLayout.CENTER );
                //esquema.layoutContainer(contenedor);
                recorreCamino();
            }
        } );
        // Add buttons to our GUI
        panel1.add( connectButton );
        panel1.add( disconnectButton );
        panel1.add( sphero_trace );
        
        reset.addActionListener( new ActionListener (){
            @Override
            public void actionPerformed( ActionEvent e ){
                //esquema.setAlignment( FlowLayout.CENTER );
                //esquema.layoutContainer(contenedor);
                Dibujo.resetPuntos();
                Dibujo.resetComponent();
            }
        });
        panel1.add( reset );
        
        //Agregar el menu de colores para poder cambiar de color en el dibujo y el Sphero
        //ListColor = new JList( colores );
        //ListColor.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //ListColor.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        //indices = ListColor.getSelectedIndices();
        //scrollLista = new JScrollPane( ListColor ); 
        //scrollLista.setBounds(10,30,100,110); 
        //this.add(scrollLista);
        
        combColor = new JComboBox();
        combColor.setBounds(10,10,100,20);
        for(String color: colores){
            combColor.addItem(color);
        }
        panel1.add(combColor);
        
        this.pack();
        this.setVisible( true );
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation( EXIT_ON_CLOSE );
        this.setSize(530, 400);
        this.setVisible(true);
        
        //panelvid = new Panel_video();
        //panelvid.run();
        
    }
    
    private double angulo_t(double x1, double y1, double x2, double y2){
        double ang;
        if( x1 == x2 ){
            if( y1 > y2 ){
                //el angulo no cambia y no hay pendiente
                return 90;
            }
            else{
                return 270;
            }
        }
        else{
            if( y1 == y2){
                if( x1 > x2){
                    return 180;
                }
                else{
                    return 0;
                }
            }
            else{
                //angulo = Math.atan( (float)pendiente );
                //angulo = Math.atan2((double)puntosD[i+10].y - (double)puntosD[i].y, (double)puntosD[i+10].x - (double)puntosD[i].x);
                ang = ((Math.atan2( y2 - y1, x2 - x1)) * 180)/Math.PI;
            }
        }
        //Condicion para acomodar el angulo de 0 a 360 en el Sphero, con respecto al angulo que se tiene actualmente
        if( ang > 0 ){
            return (360-ang);
        }
        else{
            return (ang*(-1));
        }
    }
    //Recorre camino entre los puntos dibujados 
    private void recorreCamino(){
        //Empiezo por sacar el angulo entre el punto n y el n+1, hasta el punto cuentaPuntos-1
        //double angSphero = 0;
        int total = Dibujo.getTotalPuntos();
        Point puntosD[] = Dibujo.getArreglo();
        Color []PorPuntos = Dibujo.getArregloC();
        double angulo = 0.0;
        int i=0;
        /*if(total == 0 ){
            System.out.println("calibrando...");
            r.calibrate(TOP_ALIGNMENT);
            return;
        }*/
        if(total>=10){
            for( i=0; i<total-10; i+=10){
                angulo = angulo_t((double)puntosD[i].x, (double)puntosD[i].y, (double)puntosD[i+10].x, (double)puntosD[i+10].y);
                //Se encuentran sobre el mismo eje, entonces el angulo es de 90 con respecto a x
                //Para dar el movimiento entre puntos
                System.out.println("Punto i:" + puntosD[i].x+","+puntosD[i].y+"---Punto i+1:"+puntosD[i+10].x+","+puntosD[i+10].y+", angulo: "+angulo);
                //r.rotate((float)angulo);
                r.setRGBLedColor(PorPuntos[i]);
                try {
                    r.roll((float)angulo ,(float) 0.4); 
                    Thread.sleep( 400 );
                    r.stopMotors();
                } catch (InterruptedException ex) {
                    Logger.getLogger(SpheroJ.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        //Recorrer el último tramo de puntos no visto en el recorrido del for
        int restantes = total%10;
        if( restantes >= 3 ){
            System.out.println("la i final es: "+ i + "   ///   y el total de puntos:"+ total);
            //angulo = ((Math.atan2((double)puntosD[i+restantes-1].y - (double)puntosD[i].y, (double)puntosD[i+restantes-1].x - (double)puntosD[i].x)) * 180)/Math.PI;
            angulo = angulo_t((double)puntosD[i].x, (double)puntosD[i].y, (double)puntosD[i+restantes-1].x, (double)puntosD[i+restantes-1].y);
            r.setRGBLedColor(PorPuntos[i]);
            try {
                r.roll((float)angulo ,(float) 0.4); 
                Thread.sleep( 400 );
                r.stopMotors();
            } catch (InterruptedException ex) {
                Logger.getLogger(SpheroJ.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Se restaura el panel de dibujo
        if( total > 0 ){
            Dibujo.resetPuntos();
            Dibujo.setTotalPuntos( 0 );
        }
        Dibujo.resetComponent();
    }
    /**
    * Set connect button state (also affects the disconnect button)
    * @param enabled True to enable, false otherwise
    */
    private void setConnectEnabled( boolean enabled ){
        this.connectButton.setEnabled( enabled );
        this.disconnectButton.setEnabled( !enabled );
    }
    /**
    * Handles the detection of new devices and listens on our robots for
    * responses and events
    */
    private class ConnectThread extends Thread implements BluetoothDiscoveryListener, Runnable, RobotListener {
        // Internal storage
        private Bluetooth bt;
        private boolean stop = false;
        @SuppressWarnings("FieldMayBeFinal")
        private Collection<Robot> robots;
        /**
        * Create a connect thread
        */
        @SuppressWarnings("Convert2Diamond")
        public ConnectThread(){
            this.robots = new ArrayList<Robot>();
        }
        /**
        * Stop everything regarding the connection and robots
        */
        private void stopThread(){
            System.out.println("Desconectando dispositivo");
            if( bt != null )
                bt.cancelDiscovery();
            this.stop = true;
            // Disconnect from all robots and clear the connected list
            for( Robot r : robots )
                r.disconnect();
            robots.clear();
        }
        @Override
        public void run(){
            try{
                // Esta configurado para conectarse a cualquier dispositivo Bluetooth
                bt = new Bluetooth( this, Bluetooth.SERIAL_COM );
                //Para conectar con los dispositivos cercanos posibles y ponerlos en led blanca
                //bt.discover();
                
                // //Para conectar con un solo dispositivo dando su dirección ..........
                final String bluetoothAddress = "000666441D2A";
                BluetoothDevice btd = new BluetoothDevice( bt, "btspp://" + bluetoothAddress + ":1;authenticate=true;encrypt=false;master=false" );
                // // Create the robot from the bluetooth device
                r = new Robot( btd );
                // // Try to connect to the robot
                // Create our macro object (seen as a command)
                
                // Add macro commands to the macro object
                if ( r.connect() ){
                    System.out.println("Conectado y funcionando ... ");
                    // // Add ourselves as listeners
                    r.addListener( this );
                    // // Send a rgb transition command macro
                    System.out.println("Cambiando de color Verde a Azul");
                    r.rgbTransition( 0, 255, 0, 0, 0, 255, 500); //de verde a azul
                    System.out.println("Cambiando de color Rojo a Verde");
                    r.rgbTransition( 255, 0, 0, 0, 255, 0, 500); //de rojo a verde
                    System.out.println("Conexión extablecida.");
                    r.sendCommand( new FrontLEDCommand( 1F ) );
                }
                // Run 
                //float giro = (float) 0.0;
            /*                
                // Create our macro object (seen as a command)
                MacroObject mo = new MacroObject();
                //Controlar cambios en paralelo
                // Add macro commands to the macro object
                mo.addCommand( new RGBSD2( Color.RED ) );
                mo.addCommand( new Delay( 2000 ) );
                mo.addCommand( new RGBSD2( Color.BLUE ) );
                mo.addCommand( new Delay( 2000 ) );

                // Send the macro object to the Sphero
                r.sendCommand( mo );
            */
                while( !stop ){
                    try{
                        //int selection = ListColor.getSelectedIndex();
                        int selection = combColor.getSelectedIndex();
                        //int select = combColor.getSelectedIndex();
                        //System.out.println("indice: "+ select);
                        if (selection!=-1) {
                            Dibujo.c = colores2[selection];
                        }
                        Thread.sleep( 5000 );
                        System.out.println("time sleep");
                        //rotate gira con respecto al FrontLEDCommand en sentido contrario.
                        /*if( giro > 360){
                            r.disconnect();
                            stop = !stop;
                        }
                        else{
                            //r.rotate( giro );
                            //System.out.println("giro en angulo igual a  " + giro);
                            /**
                            * Roll the robot with a given motorHeading and speed
                            * @param heading The motorHeading (0-360)
                            * @param speed The speed (0-1)
                            */
                        /*    r.roll(giro ,(float) 0.3);
                            Thread.sleep(500);
                            r.stopMotors();
                            //r.stopMacro();
                            //r.drive(90.0, 90.0, 90.0);
                            giro+=20;
                        }*/
                    }
                    catch( InterruptedException e ){
                        System.out.println("Exception error");
                    }
                }
            }
            catch( InvalidRobotAddressException | RobotBluetoothException e ){
                System.out.println("Fallo la conexion");
            }
        }
        
        /**
        * BLUETOOTH DISCOVERY STUFF
        * Called when the device search is completed with detected devices
        * @param devices The devices detected
        */
        @Override
        @SuppressWarnings("CallToPrintStackTrace")
        public void deviceSearchCompleted( Collection<BluetoothDevice> devices ){
            // Device search is completed
            System.out.println( "Completed device discovery" );
            // Try and see if we can find any Spheros in the found devices
            for( BluetoothDevice d : devices ){
                // Check if the Bluetooth device is a Sphero device or not
                if( Robot.isValidDevice( d ) ){
                    System.out.println( "Found robot " + d.getAddress() );
                    // We got a valid device (Sphero device), connect to it and
                    // have some fun with colors.
                    try{
                        // Create our robot from the Bluetooth device that we got
                        Robot r1 = new Robot( d );
                        // Add ourselves as listeners for the responses
                        r1.addListener( this );
                        // Check if we can connect
                        if( r1.connect() ){
                            // Add robots to our connected robots list
                            robots.add( r1 );
                            System.out.println( "Connected to " + d.getName() + " : " + d.getAddress() );
                            //r.rgbTransition( 255, 0, 0, 0, 255, 255, 50 );
                            // Send direct command
                            r1.sendCommand( new FrontLEDCommand( 1 ) );
                        }
                        else
                            ;
                            //System.err.println( "Failed to connect to robot" );
                    }
                    catch( InvalidRobotAddressException ex ){
                        ex.printStackTrace();
                    }
                    catch( RobotBluetoothException ex ){
                        ex.printStackTrace();
                    }
                }
            }
            // Disable the thread and set connected button state
            if( robots.isEmpty() ){
                this.stopThread();
                setConnectEnabled( true );
            }
        }
        /**
        * Called when the search is started
        */
        @Override
        public void deviceSearchStarted(){
            System.out.println( "Started device search" );
        }
        /**
        * Called if something went wrong with the device search
        * @param error The error that occurred
        */
        @Override
        public void deviceSearchFailed( EVENT error ){
            System.err.println( "Failed with device search: " + error +" ***");
            System.out.println( "*** deviceSearchFailed ");
        }
        /**
        * Called when a Bluetooth device is discovered
        * @param device The device discovered
        */
        @Override
        public void deviceDiscovered( BluetoothDevice device ){
            System.out.println("***deviceDiscovered" );
            System.out.println( "Discovered device " + device.getName() + " : " + device.getAddress() +" ***");
        }
        /**
        * ROBOT STUFF
        * Called when a response is received from a robot
        * 
        * @param r The robot the event concerns
        * @param response The response received
        * @param dc The command the response is concerning
        */
        @Override
        public void responseReceived( Robot r, ResponseMessage response, CommandMessage dc ){
            System.out.println( "***responseReceived" );
            System.out.println( "(" + ( ++responses ) + ") Received response: " + response.getResponseCode() + " to message " + dc.getCommand()+" ***" );
        }
        /**
        * Event that may occur for a robot
        * @param r The robot the event concerns
        * @param code The event code for the event
        */
        @Override
        public void event( Robot r, RobotListener.EVENT_CODE code ){
            System.out.println( "***event" );
            System.out.println( "Received event: " + code );
            System.out.println( "Robot: " + r +" ***");
        }
        @Override
        public void informationResponseReceived( Robot r, InformationResponseMessage response ){
            //Information response (Ex. Sensor data)
            System.out.println("***InformationResponseReceived: " );
            System.out.println(" Información recivida del rotob "+ r + "con el string en response "+ response.getResponseCode() +" ***");
        }
    }
}
