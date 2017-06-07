/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.dretve;

import static com.google.common.base.CharMatcher.is;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author filip
 */
public class DretvaZahtjeva extends Thread {
    private ServerSocket serverSocket;
    public  Socket s;

    public DretvaZahtjeva(Socket s) {
        this.s = s;
    } 
    
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
            InputStream is = null;
        try { 
            is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            System.out.println("Primljena naredba: " + sb);  
            
            //TODO treba provjeriti ima li "mjesta" za novu radnu dretvu 
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
 
   
    }
    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

}
