/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

/**
 *
 * @author filip
 */
public class ServerDretva extends Thread{
    
      private ServerSocket serverSocket;
    public static Socket s;
    public static ServletContext context;

    @Override
    public void interrupt() {
       try{
       s.close();
       }catch(IOException ex){
           System.err.println("INterupt");
       }
    }

    @Override
    public void run() {
        
        System.out.println("Dosao sam u server");
      
         try {

            Short redniBrojDretve = 1;
            int port = 8000;

              serverSocket = new ServerSocket(port);
//            
//              RadnaDretva nova1 = new RadnaDretva(context);
//                nova1.start();  
             
                System.out.println("Kreirao sam radnu dretvu ");
            while (true) {    
                Socket so = serverSocket.accept();
                System.out.println("NOVI ZAHTJEV DOSAO ");
                DretvaZahtjeva nova = new DretvaZahtjeva(so);
                nova.start();
            }
            } catch (IOException ex) {
                Logger.getLogger(ServerDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
                
            
            
            
    
    }
    @Override
    public synchronized void start() {
       super.start(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
