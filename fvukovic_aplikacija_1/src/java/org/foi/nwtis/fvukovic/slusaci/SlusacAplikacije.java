/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.slusaci;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.fvukovic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
/**
 * Web application lifecycle listener.
 *
 * @author filip
 */
public class SlusacAplikacije implements ServletContextListener {

      @Override
    public void contextInitialized(ServletContextEvent sce) {
          System.out.println("RADI KOD POKRETANJA");
         ServletContext context = sce.getServletContext();
        String datoteka = context.getRealPath("/WEB-INF") 
                    + File.separator 
                    + context.getInitParameter("konfiguracija");
        
        BP_Konfiguracija bp_konf = new BP_Konfiguracija(datoteka);
        context.setAttribute("BP_Konfig", bp_konf);
        System.out.println("Ucitana konfiguacija");
        
        Konfiguracija konf = null;
        try {
            konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            context.setAttribute("Mail_Konfig", konf);
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
