/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.zrna;

import javax.inject.Named;
import javax.enterprise.context.Dependent;
import org.foi.nwtis.fvukovic.ws.klijenti.MeteoWSKlijent;

/**
 *
 * @author filip
 */
@Named(value = "pregledKorisnika")
@Dependent
public class pregledKorisnika {

    /**
     * Creates a new instance of pregledKorisnika
     */
    public pregledKorisnika() {
        MeteoWSKlijent.getUsersREST();
    }
    
}
