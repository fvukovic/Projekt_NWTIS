package org.foi.nwtis.fvukovic.dretve;

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
    public Connection c;
    public List<Uredjaj> listaUredjaja = new ArrayList<>();
    public List<Uredjaj> listaUredjajaZaPrognozu = new ArrayList<>();
    public List<MeteoPodaci> prognoze = new ArrayList<>();

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        int brojCiklusa = 1;
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        try {
            Class.forName(bp_konf.getDriverDatabase());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
            return;
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
        try {
            String query = "Select * from uredaji";
            Statement s = c.createStatement();
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

        //radna petlja  
        while (1 == 1) {

            System.out.println("LISTA SIZE" + listaUredjaja.size() + " LIstaz za prognozu: " + listaUredjajaZaPrognozu.size());
            Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
            int trajanjeCiklusa = Integer.parseInt(konf.dajPostavku("timeSecThread"));
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
            System.out.println("mater: "+this.prognoze.size());
            for (Uredjaj uredaj : listaUredjaja) {
                for (MeteoPodaci mp : this.prognoze) {
                    System.out.println("ovo: "+uredaj.getGeoloc().getLatitude()+" "+ (mp.getLatitude()) );
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
                            System.err.println("OVO JE OPIS: " + vrijemeOpisa + "OVO JE ICON: " + mp.getWeatherIcon());
                            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date now = new Date();
                            String strDate = sdfDate.format(now);
                            String query = "insert into meteo values(default," + uredaj.getId() + ",'" + uredaj.getNaziv() + "'," + uredaj.getGeoloc().getLatitude() + "," + uredaj.getGeoloc().getLongitude() + ""
                                    + ",'" + mp.getWeatherIcon() + "','" + vrijemeOpisa + "'," + temp + "," + tempMin + "," + tempMax + "," + vlaga + "," + tlak + "," + vjetar + "," + vjetarSmjer + ",'" + strDate + "')";
                            Statement s;
                            try {
                                s = c.createStatement();
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
