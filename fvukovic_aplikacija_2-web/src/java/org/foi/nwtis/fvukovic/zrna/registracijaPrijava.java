/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.context.FacesContext;

/**
 *
 * @author filip
 */
@Named(value = "registracijaPrijava")
@SessionScoped
public class registracijaPrijava implements Serializable {

    /**
     * Creates a new instance of registracijaPrijava
     */
    public registracijaPrijava() { 
    }
    private void prijaviSe(){
        FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "pregledKorisnika.xhtml");
    }
    private void registrirajSe(){
    
    }
}
