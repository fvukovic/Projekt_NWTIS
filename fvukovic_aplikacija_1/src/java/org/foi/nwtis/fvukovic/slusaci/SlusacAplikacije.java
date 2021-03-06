/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.slusaci;

import org.foi.nwtis.fvukovic.dretve.Baza; 
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;
import java.io.File;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.fvukovic.dretve.RadnaDretva;
import org.foi.nwtis.fvukovic.dretve.ServerDretva;
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.fvukovic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.fvukovic.rest.korisnici.UsersServersResource;
import org.foi.nwtis.fvukovic.rest.ws.MeteoRESTResource;
import org.foi.nwtis.fvukovic.rest.ws.MeteoRESTResourceContainer;
import org.foi.nwtis.fvukovic.ws.GeoMeteoWS;
import org.foi.nwtis.fvukovic.zrna.PregledKorisnika;
import org.foi.nwtis.fvukovic.zrna.PregledZahtjeva;
import org.foi.nwtis.fvukovic.zrna.pregledDnevnika;

/**
 * Web application lifecycle listener.
 *
 * @author filip
 */
public class SlusacAplikacije implements ServletContextListener {
    /**
     * inicijalizacija konteksta te spremanje konfiguracijske datoteke u Servlet kontekt za daljnje koristenje
     * @param sce 
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("RADI KOD POKRETANJA");
        ServletContext context = sce.getServletContext();
        String datoteka = context.getRealPath("/WEB-INF")
                + File.separator
                + context.getInitParameter("konfiguracija");

        BP_Konfiguracija bp_konf = new BP_Konfiguracija(datoteka);
        context.setAttribute("BP_Konfig", bp_konf); 

        Konfiguracija konf = null;
        try {
            konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            context.setAttribute("Baza_Konfig", konf); 
             Baza.sc=context;
            Baza.spojiNaBazu();
            sleep(1000);
            ServerDretva SD = new ServerDretva();
            SD.start();

            RadnaDretva nova1 = new RadnaDretva(context);
            nova1.start();

            System.err.println("DOSAO SAAAAAAAAAAAAM");
            GeoMeteoWS.sc = context;
           
            
            MeteoRESTResourceContainer.sc = context;
            MeteoRESTResource.sc = context;
            UsersServersResource.sc = context;
            PregledKorisnika.sc = context;
            pregledDnevnika.sc = context;
            ServerDretva.context = context;
            PregledZahtjeva.sc=context;

        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            System.err.println(ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
