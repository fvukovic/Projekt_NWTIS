/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.zrna;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.apache.catalina.manager.util.SessionUtils;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author filip
 */
public class PrijavaKorisnika {
     private static final long serialVersionUID = 1094801825228386363L;
     private String pwd;
	private String msg;
	private String user;
        public static ServletContext sc;
        Connection c;
    /**
     * Creates a new instance of PrijavaKorisnika
     */
    public PrijavaKorisnika() {
        
    }
    
    public String prijava(){
        System.out.println("ULOGIRAN SI");
       boolean valid = false;
        String query ="select * from korisnici where username='"+this.user+"' and password ='"+this.pwd+"'";
        return "success";   
        
        
        
    }
	
	

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

         public void spojiNaBazu() {
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        try {
            Class.forName(bp_konf.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }        
        /**
         * Spajamo se na bazu kako bi upisivali potrebne podatke
         */
        try {
            c = DriverManager.getConnection(bp_konf.getServerDatabase() + bp_konf.getUserDatabase(),
                    bp_konf.getUserUsername(),
                    bp_konf.getUserPassword());
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }
        
        
    }
    
 
