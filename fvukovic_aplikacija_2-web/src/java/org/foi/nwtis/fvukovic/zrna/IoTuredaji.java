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

/**
 *
 * @author filip
 */
@Named(value = "ioTuredaji")
@SessionScoped
public class IoTuredaji implements Serializable {

    /**
     * Creates a new instance of IoTuredaji
     */
    public IoTuredaji() {
    }
    
     public void pregledUredaja(){
    
        Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
             String zahtjev = "USER fvukovic; PASSWD 123456; PAUSE;";
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
            String odgovor=sb.toString();
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
    }
    
    
    
}
