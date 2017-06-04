/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.dretve;

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
        while (1 == 1) {
            Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig"); 
            int trajanjeCiklusa = Integer.parseInt(konf.dajPostavku("mail.timeSecThread"));
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
