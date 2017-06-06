/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.zrna;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import org.foi.nwtis.fvukovic.dretve.RadnaDretva;
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.fvukovic.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author filip
 */
@Singleton
@LocalBean
public class PokretanjeDretveBean {

   
    
    public PokretanjeDretveBean() {
        System.err.println("tucam ");
        RadnaDretva rd = new RadnaDretva();
        rd.start();
    }

    
}
