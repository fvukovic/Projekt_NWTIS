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
import org.foi.nwtis.fvukovic.dretve.DretvaZahtjeva;
import org.foi.nwtis.fvukovic.dretve.RadnaDretva;
import org.foi.nwtis.fvukovic.dretve.ServerDretva;
import static org.foi.nwtis.fvukovic.dretve.ServerDretva.context;
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.fvukovic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.fvukovic.master.Iot_Master;
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
            
            ServerDretva SD = new ServerDretva();
            SD.start();

            RadnaDretva nova1 = new RadnaDretva(context);
            nova1.start();

            System.err.println("DOSAO SAAAAAAAAAAAAM");
            GeoMeteoWS.sc = context;
            Baza.sc=context;
            Baza.spojiNaBazu();
            
            MeteoRESTResourceContainer.sc = context;
            MeteoRESTResource.sc = context;
            UsersServersResource.sc = context;
            PregledKorisnika.sc = context;
            pregledDnevnika.sc = context;
            ServerDretva.context = context;
            PregledZahtjeva.sc=context;

        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            System.err.println(ex);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
