package org.foi.nwtis.fvukovic.dretve;

import static com.google.common.base.CharMatcher.is;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Thread.yield;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext; 
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.fvukovic.rest.klijenti.OWMKlijent;
import org.foi.nwtis.fvukovic.web.podaci.Lokacija;
import org.foi.nwtis.fvukovic.web.podaci.MeteoPodaci;
import org.foi.nwtis.fvukovic.web.podaci.Uredjaj;

/**
 *
 * @author filip
 */
public class RadnaDretva extends Thread {

    private boolean prekid_obrade = false;
    private ServletContext sc = null; 
    public List<Uredjaj> listaUredjaja = new ArrayList<>();
    public List<Uredjaj> listaUredjajaZaPrognozu = new ArrayList<>();
    public List<MeteoPodaci> prognoze = new ArrayList<>();
    public static boolean dretva=true; 
    public static boolean prekid=false; 
    public static Socket socket; 
    InputStream is=null;
    OutputStream os=null;
     

    

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
         System.out.println("DRETVA");
        
        
        
        int brojCiklusa = 1;
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
       
        /**
         * Spajamo se na bazu kako bi upisivali potrebne podatke
         */
     

        //radna petlja  
        while (true) {
            
           
            if(ServerDretva.preuzimaj==false){
                break;
            }
            
           while (!dretva)
                yield();
             try {
            String query = "Select * from uredaji";
            Statement s =Baza.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {

                Uredjaj novi = new Uredjaj();
                novi.setId(rs.getInt("id"));
                Lokacija novaLokacija = new Lokacija(rs.getString("latitude"), rs.getString("longitude"));
                novi.setGeoloc(novaLokacija);
                listaUredjaja.add(novi);
            }

        } catch (SQLException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
        
            
            if(RadnaDretva.prekid){
                break;
            }
            
    
       
 
            Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
            int trajanjeCiklusa = Integer.parseInt(konf.dajPostavku("timeSecThread"));
            System.out.println("OVOLIKO TREBA CEKATI"+  trajanjeCiklusa);
            System.out.println("Ciklus dretve: " + brojCiklusa);
            brojCiklusa++;
            OWMKlijent noviPoziv = new OWMKlijent(konf.dajPostavku("api.key"));
            try {
                sleep(trajanjeCiklusa * 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
            boolean postoji = false;

            /**
             * spremanje u listu uredaja bez duplikata
             */
            for (Uredjaj uredaj : listaUredjaja) {
                for (Uredjaj uredajZaPrognozu : listaUredjajaZaPrognozu) {
                    if (uredaj.getGeoloc().getLatitude().equals(uredajZaPrognozu.getGeoloc().getLatitude())) {
                        if (uredaj.getGeoloc().getLongitude().equals(uredajZaPrognozu.getGeoloc().getLongitude())) {
                            postoji = true;
                        }
                    }
                }
                if (!postoji) {
                    listaUredjajaZaPrognozu.add(uredaj);
                }
                postoji = false;
            }
            /**
             * Dovhvaća prognoze samo za unique lokacije
             */
            for (Uredjaj uredajZaPrognozu : listaUredjajaZaPrognozu) {

                MeteoPodaci mp = noviPoziv.getRealTimeWeather(uredajZaPrognozu.getGeoloc().getLatitude(), uredajZaPrognozu.getGeoloc().getLongitude());
                mp.setLatitude(uredajZaPrognozu.getGeoloc().getLatitude());
                mp.setLongitude(uredajZaPrognozu.getGeoloc().getLongitude());
                this.prognoze.add(mp);
            } 
            for (Uredjaj uredaj : listaUredjaja) {
                for (MeteoPodaci mp : this.prognoze) { 
                    if (uredaj.getGeoloc().getLatitude().equals(mp.getLatitude())) {
                        if (uredaj.getGeoloc().getLongitude().equals(mp.getLongitude())) {
                            String temp = mp.getTemperatureValue().toString();
                            String vlaga = mp.getHumidityValue().toString();
                            String tlak = mp.getPressureValue().toString();
                            String sunset = mp.getSunSet().toString();
                            float tempMin = mp.getTemperatureMin();
                            float tempMax = mp.getTemperatureMax();
                            float vjetar = mp.getWindSpeedValue();
                            float vjetarSmjer = mp.getWindSpeedValue();
                            String vrijemeOpisa = mp.getWeatherValue(); 
                            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date now = new Date();
                            String strDate = sdfDate.format(now);
                            String query = "insert into meteo values(default," + uredaj.getId() + ",'" + uredaj.getNaziv() + "'," + uredaj.getGeoloc().getLatitude() + "," + uredaj.getGeoloc().getLongitude() + ""
                                    + ",'" + mp.getWeatherIcon() + "','" + vrijemeOpisa + "'," + temp + "," + tempMin + "," + tempMax + "," + vlaga + "," + tlak + "," + vjetar + "," + vjetarSmjer + ",'" + strDate + "')";
                            Statement s;
                            try {
                                s = Baza.c.createStatement();
                                s.executeUpdate(query);
                            } catch (SQLException ex) {
                                Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                    }

                }

            }
              this.prognoze.clear();
              
              
        
        }
      
        

    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    public RadnaDretva(ServletContext sc) {
        this.sc = sc;
    }

}


/**



    String sintaksa_adminPause = "^USER ([^\\s]+); PASSWD ([^\\s]+); PAUSE;$";
        String sintaksa_adminStop = "^USER ([^\\s]+); PASSWD ([^\\s]+); STOP;$";
        String sintaksa_adminStart = "^USER ([^\\s]+); PASSWD ([^\\s]+); START;$";
        String sintaksa_adminStat = "^USER ([^\\s]+); PASSWD ([^\\s]+); STATUS;$";
            
               
            Pattern p = Pattern.compile(sintaksa_adminPause);
            Matcher m = p.matcher(sb);
            boolean status = m.matches();
             System.out.println("ADMIN pause : " +status);
            if (status) {
               
                if(RadnaDretva.dretva==false){
                    os.write("ERR 10;".getBytes());
                            os.flush();
                }else{
                os.write("OK;".getBytes());
                     os.flush();
                }
            }else{
            p = Pattern.compile(sintaksa_adminStart);
            m = p.matcher(sb);
            status = m.matches();
              if (status) {
                    System.out.println("ADMIN start : " );
                if(RadnaDretva.dretva==true){
                    os.write("ERR 10;".getBytes());
                            os.flush();
                }else{
                os.write("OK;".getBytes());
                     os.flush();
                }
            }*/