/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.zrna;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.net.Socket;
import org.foi.nwtis.fvukovic.sesije.SessionUtils;

/**
 *
 * @author filip
 */
@Named(value = "iot_master")
@SessionScoped
public class iot_master implements Serializable {

    /**
     * Creates a new instance of iot_master
     */
    public iot_master() {
    }
        private String odgovorMaster;
    
    
 
    public String getOdgovorMaster() {
        return odgovorMaster;
    }

    public void setOdgovorMaster(String odgovorMaster) {
        this.odgovorMaster = odgovorMaster;
    }

    
    
  
       public void clearMaster(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
              String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; IoT_Master CLEAR;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
            odgovorMaster=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
       
        
          public void listMaster(){
    
        Socket socket = null; 
        System.out.println("list funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
              String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; IoT_Master LIST;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
            odgovorMaster=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
             public void loadMaster(){
    
        Socket socket = null; 
        System.out.println("list funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
              String zahtjev = "USER "+SessionUtils.getUserName()+"; PASSWD "+SessionUtils.getPassword()+"; IoT_Master LOAD;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
            odgovorMaster=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
         
         
 
     
    
    
    
}
