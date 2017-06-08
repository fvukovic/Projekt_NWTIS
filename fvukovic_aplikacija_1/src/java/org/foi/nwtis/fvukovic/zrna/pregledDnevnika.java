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
import org.foi.nwtis.fvukovic.dretve.DretvaZahtjeva;
import org.foi.nwtis.fvukovic.dretve.RadnaDretva;
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.fvukovic.rest.ws.MeteoRESTResourceContainer;
import org.foi.nwtis.fvukovic.web.podaci.Dnevnik;
import org.foi.nwtis.fvukovic.web.podaci.Korisnik;

/**
 *
 * @author filip
 */
public class pregledDnevnika {
    
    public static ServletContext sc;
    Connection c;
    private List<Dnevnik> listaSvihKorisnika = new ArrayList<>();
    private List<Dnevnik> listaTrenutnihKorisnika = new ArrayList<>();
    private int brojPrikaza;
    private boolean init=false;
    private int pocetakKorisnika=0;
    private int krajKorisnika=0;

   
    
    
    
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

    public List<Dnevnik> getListaSvihKorisnika() {
        return listaSvihKorisnika;
    }

    public void setListaSvihKorisnika(List<Dnevnik> listaSvihKorisnika) {
        this.listaSvihKorisnika = listaSvihKorisnika;
    }

    public List<Dnevnik> getListaTrenutnihKorisnika() {
        return listaTrenutnihKorisnika;
    }

    public void setListaTrenutnihKorisnika(List<Dnevnik> listaTrenutnihKorisnika) {
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

    public pregledDnevnika() {
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
        this.brojPrikaza = Integer.parseInt(konf.dajPostavku("broj.prikaza"));
        preuzmiKorisnike(); 
    }
    
    public void preuzmiKorisnike()
    {
        try {
            listaSvihKorisnika.clear();
            spojiNaBazu();
            String query = "Select * from dnevnik";
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            int brojac=0;
            System.err.println("BROJ PRIKAZA: "+this.brojPrikaza+"pocetak: "+this.pocetakKorisnika+" max: "+this.brojPrikaza+this.pocetakKorisnika);
            while(rs.next()){
                if(brojac>=this.pocetakKorisnika && this.brojPrikaza+this.pocetakKorisnika>brojac){
                Dnevnik novi = new Dnevnik(rs.getInt("id"),rs.getString("korisnik"),rs.getString("url"),rs.getString("ipadresa"),rs.getDate("vrijeme"),rs.getInt("trajanje"),rs.getInt("status"));
                listaSvihKorisnika.add(novi);
                }
               brojac++;
            }
                    } catch (SQLException ex) {
            Logger.getLogger(PregledKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void prethodniKorisnici(){
         System.out.println("usao sam nazalost");
    //    System.out.println(  DretvaZahtjeva.aktivirajGrupuIoT("fvukovic", "1234123"));
         Socket socket = null;
//  
//            try {
//                socket = new Socket("localhost", 8000);
//                 byte[] bytes = new byte[14 * 1024];
//                String myString = "USER pero; PASSWD 123456; START;";
//                InputStream is = new ByteArrayInputStream(myString.getBytes());
//                OutputStream out = socket.getOutputStream();
//
//                int count;
//                while ((count = is.read(bytes)) > 0) {
//                    out.write(bytes, 0, count);
//                }
//                
//            StringBuffer sb = new StringBuffer();
//            while (true) {
//                int znak = is.read();
//                if (znak == -1) {
//                    break;
//                }
//                sb.append((char) znak);
//            }
//
//                out.close();
//                is.close();
//                socket.close();
//
//                System.out.println("Naredba poslana");
//            } catch (IOException ex) {
//                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
//            }

             
             try {
                 
            Socket s = new Socket("localhost", 8000);
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
             String zahtjev = "USER pero; PASSWD 123456; START;";
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
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }  
        if(this.pocetakKorisnika-this.brojPrikaza<0){
            return;
        }
            this.pocetakKorisnika=this.pocetakKorisnika-this.brojPrikaza;
            preuzmiKorisnike();
            System.err.println("Ja sam devojka sa sela");
    }
        public void sljedeciKorisnici(){
            
             if(this.pocetakKorisnika+this.brojPrikaza>listaSvihKorisnika.size()){
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
