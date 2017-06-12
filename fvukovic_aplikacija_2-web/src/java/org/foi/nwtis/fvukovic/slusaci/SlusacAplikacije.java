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
import javax.ejb.EJB;
import org.foi.nwtis.fvukovic.dretve.ObradaPoruka;
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.fvukovic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija; 
import org.foi.nwtis.fvukovic.zrna.PregledMailova;
import org.foi.nwtis.fvukovic.zrna.SviKorisniciIAzuriranje;

/**
 * Web application lifecycle listener.
 *
 * @author filip
 */
public class SlusacAplikacije implements ServletContextListener {
    
    public static String usernameSesija = "";
    public static int idSesija ;
    public static String emailSesija="" ;
    public static String passwordSesija ;

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
            context.setAttribute("Mail_Konfig", konf);
            PregledMailova.sc=context;
            ObradaPoruka.sc=context;
           

        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            System.err.println(ex);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
