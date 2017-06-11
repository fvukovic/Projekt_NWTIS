/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
/**
 *
 * @author filip
 */
@Named(value = "pregleddnevnika")
@SessionScoped
public class Pregleddnevnika implements Serializable {

    /**
     * Creates a new instance of Pregleddnevnika
     */
    public Pregleddnevnika() {
    }
     
    
}
