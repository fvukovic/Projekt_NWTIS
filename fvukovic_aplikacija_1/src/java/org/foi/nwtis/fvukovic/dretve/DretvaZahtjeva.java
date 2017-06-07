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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static javax.ws.rs.core.Response.status;

/**
 *
 * @author filip
 */
public class DretvaZahtjeva extends Thread {

    private ServerSocket serverSocket;
    public Socket s;
    public InputStream is;
    public OutputStream os;
   
    public DretvaZahtjeva(Socket s) {
        this.s = s;
    }

    @Override
    public void interrupt() {
        try {
            s.close();
        } catch (IOException ex) { 
        }
    }

    @Override
    public void run() { 
        
        
           System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        try {
            is = s.getInputStream();
             os = s.getOutputStream();
            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            System.out.println("Primljena komanda: " + sb);
         
            String sintaksa_adminPause = "^USER ([^\\s]+); PASSWD ([^\\s]+); PAUSE;$";
            String sintaksa_adminStop = "^USER ([^\\s]+); PASSWD ([^\\s]+); STOP;$";
            String sintaksa_adminStart = "^USER ([^\\s]+); PASSWD ([^\\s]+); START;$";
            String sintaksa_adminStat = "^USER ([^\\s]+); PASSWD ([^\\s]+); STATUS;$";

            
             
            Pattern p = Pattern.compile(sintaksa_adminPause);
            Matcher m = p.matcher(sb);
           boolean status = m.matches();
            System.out.println("koja opcija: " + status);
            if (status) {
  System.out.println("koja opcija:sintaksa_adminPause " + status);
                ServerPause();
            }
            p = Pattern.compile(sintaksa_adminStart);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                  System.out.println("koja opcija: sintaksa_adminStart" + status);
                adminStart();
            }
            p = Pattern.compile(sintaksa_adminStop);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                  System.out.println("koja opcija: sintaksa_adminStop" + status);
                adminStop();
            }
            p = Pattern.compile(sintaksa_adminStat);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                adminStatus();
            }

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

    private void ServerPause() {
        
            try {
                if (RadnaDretva.dretva == false) {
               os.write("Error 10;".getBytes());
                os.flush();
                
                 }else{
                    RadnaDretva.dretva=false;
                 os.write("OK".getBytes());
                os.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
       
    }

    private void adminStart() {
           try {
                if (RadnaDretva.dretva == true) {
               os.write("Error 11;".getBytes());
                os.flush();
                
                 }else{
                    RadnaDretva.dretva=true;
                 os.write("OK 10;".getBytes());
                os.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
}
    private void adminStop() {
        try {
                if (ServerDretva.preuzimaj == false) {
               os.write("Error 12;".getBytes());
                os.flush();
                
                 }else{
                    ServerDretva.preuzimaj=false;
                 os.write("OK".getBytes());
                os.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    private void adminStatus() {
          try {
                if(ServerDretva.preuzimaj==false){
                    os.write("OK 15;".getBytes());
                os.flush();
                return;
                }    
        
                String response ="OK";
                if(RadnaDretva.dretva==false){
                      
                }else{
                response=response+" 14;";
                }
        
        
          os.write(response.getBytes());
                os.flush();
               
               
               
               
               } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

}
