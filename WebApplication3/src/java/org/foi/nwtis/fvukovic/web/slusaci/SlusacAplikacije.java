/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Web application lifecycle listener.
 *
 * @author filip
 * Slusac aplikacije je "pocetak nase aplikacije". pokrece sve ostale klase, te otvara sve i dohvaca konfiguracijeske datoteke
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

 
    
 

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
}

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
