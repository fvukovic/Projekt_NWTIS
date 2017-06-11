/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.zrna;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.foi.nwtis.fvukovic.dretve.RadnaDretva;
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.fvukovic.rest.ws.MeteoRESTResourceContainer;
import org.foi.nwtis.fvukovic.web.podaci.Korisnik;

/**
 *
 * @author filip
 */
public class PregledKorisnika {
    
    public static ServletContext sc;
    Connection c;
    private List<Korisnik> listaSvihKorisnika = new ArrayList<>();
    private List<Korisnik> listaTrenutnihKorisnika = new ArrayList<>();
    private int brojPrikaza;
    private boolean init=false;
    private int pocetakKorisnika=0;
    private int krajKorisnika=0;

   
    public PregledKorisnika() {
          Socket socket = null; 
        System.out.println("start funkcija");
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
             String zahtjev = "USER fvukovic; PASSWD 123456; START;";
           //  String zahtjev = "IoT 123456 ; WORK;";
            System.out.println(zahtjev);
            os.write(zahtjev.getBytes());
            os.flush();
            s.shutdownOutput();

            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            s.close();
            System.out.println("Primljeni  odgovor: " + sb);
        } catch (IOException ex) {
                 System.out.println(ex);
        }  
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
        System.out.println("KOLKO BROJA: "+Integer.parseInt(konf.dajPostavku("broj.prikaza")));
        this.brojPrikaza = Integer.parseInt(konf.dajPostavku("broj.prikaza"));
       
        preuzmiKorisnike();
    }
    
    
    
    public static ServletContext getSc() {
        return sc;
    }

    public static void setSc(ServletContext sc) {
        PregledKorisnika.sc = sc;
    }

    public Connection getC() {
        return c;
    }

    public void setC(Connection c) {
        this.c = c;
    }

    public List<Korisnik> getListaSvihKorisnika() {
        return listaSvihKorisnika;
    }

    public void setListaSvihKorisnika(List<Korisnik> listaSvihKorisnika) {
        this.listaSvihKorisnika = listaSvihKorisnika;
    }

    public List<Korisnik> getListaTrenutnihKorisnika() {
        return listaTrenutnihKorisnika;
    }

    public void setListaTrenutnihKorisnika(List<Korisnik> listaTrenutnihKorisnika) {
        this.listaTrenutnihKorisnika = listaTrenutnihKorisnika;
    }

    public int getBrojPrikaza() {
        return brojPrikaza;
    }

    public void setBrojPrikaza(int brojPrikaza) {
        this.brojPrikaza = brojPrikaza;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public int getPocetakKorisnika() {
        return pocetakKorisnika;
    }

    public void setPocetakKorisnika(int pocetakKorisnika) {
        this.pocetakKorisnika = pocetakKorisnika;
    }

    public int getKrajKorisnika() {
        return krajKorisnika;
    }

    /**
     * Creates a new instance of PregledKorisnika
     */
    public void setKrajKorisnika(int krajKorisnika) {
        this.krajKorisnika = krajKorisnika;
    }

    
    public void preuzmiKorisnike()
    {
        try {
            listaSvihKorisnika.clear();
            spojiNaBazu();
            String query = "Select * from korisnici";
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            int brojac=0;
            listaTrenutnihKorisnika.clear();
            while(rs.next()){
                  Korisnik novi = new Korisnik(rs.getString("id"),rs.getString("username"),rs.getString("password"),rs.getString("email"));
                if(brojac>=this.pocetakKorisnika && this.brojPrikaza+this.pocetakKorisnika>brojac){
               
                listaSvihKorisnika.add(novi);
                }
                listaTrenutnihKorisnika.add(novi);
               brojac++;
            }
                    } catch (SQLException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void prethodniKorisnici(){
       
 

        if(this.pocetakKorisnika-this.brojPrikaza<0){
            return;
        }
            this.pocetakKorisnika=this.pocetakKorisnika-this.brojPrikaza;
            preuzmiKorisnike();
            System.err.println("Ja sam devojka sa sela");
    }
        public void sljedeciKorisnici(){
            
             if(this.pocetakKorisnika+this.brojPrikaza>listaTrenutnihKorisnika.size()){
            return;
        }
            this.pocetakKorisnika=this.pocetakKorisnika+this.brojPrikaza;
             preuzmiKorisnike();
            System.err.println("Ja sam devojka sa sela");
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
        public void zapisiUDnevnik(int trajanje, int status,String url) {
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String strDate = sdfDate.format(now);
            spojiNaBazu();
            String query="insert into dnevnik values(default,'fvukovic','"+url+"','localhost','"+strDate+"', "+trajanje+", "+status+")";
            Statement s = c.createStatement();
            s.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(MeteoRESTResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
