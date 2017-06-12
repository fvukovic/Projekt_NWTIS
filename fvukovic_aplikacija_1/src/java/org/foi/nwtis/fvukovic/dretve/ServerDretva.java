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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext; 
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.fvukovic.master.Iot_Master;

/**
 *
 * @author filip
 */
public class ServerDretva extends Thread{
    
      private ServerSocket serverSocket;
    public static Socket s;
    public static ServletContext context;
 public static boolean preuzimaj=true;
    @Override
    public void interrupt() {
       try{
       s.close();
       }catch(IOException ex){
           System.err.println("INterupt");
       }
    }
    
    
    /**
     *Server dretva..ceka sljedeci zahtjev preko socketa
     */
    @Override
    public void run() {
        
        System.out.println("Dosao sam u server");
           
         try {

            Short redniBrojDretve = 1;
            //    Konfiguracija konf = (Konfiguracija) context.getAttribute("Baza_Konfig"); 
            int port =  (8000);

              serverSocket = new ServerSocket(port);
            
              RadnaDretva nova1 = new RadnaDretva(context);
               // nova1.start();  
             
                System.out.println("Kreirao sam radnu dretvu ");
            while (true) {    
                Socket so = serverSocket.accept();
                System.out.println("NOVI ZAHTJEV DOSAO ");
                DretvaZahtjeva nova = new DretvaZahtjeva(so,context);
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
