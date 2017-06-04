 
package org.foi.nwtis.fvukovic.dretve;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;

/**
 *
 * @author filip
 */
public class RadnaDretva extends Thread {
    
     private boolean prekid_obrade = false;
    private ServletContext sc = null;
    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        int brojCiklusa = 1;
        while (1 == 1) {
            Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig"); 
            int trajanjeCiklusa = Integer.parseInt(konf.dajPostavku("mail.timeSecThread"));
            System.out.println("Ciklus dretve: "+brojCiklusa);
            brojCiklusa++;
            try {
                sleep(trajanjeCiklusa*1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    public RadnaDretva(ServletContext sc) {
        this.sc=sc;
    }
    

}