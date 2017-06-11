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
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.fvukovic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija; 
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
       

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
