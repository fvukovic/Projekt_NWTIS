/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import static javax.ws.rs.core.Response.status;
import static org.foi.nwtis.fvukovic.dretve.ServerDretva.context;
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.fvukovic.rest.klijenti.GMKlijent;
import org.foi.nwtis.fvukovic.rest.klijenti.OWMKlijent;
import org.foi.nwtis.fvukovic.rest.ws.MeteoRESTResourceContainer;
import static org.foi.nwtis.fvukovic.rest.ws.MeteoRESTResourceContainer.sc;
import org.foi.nwtis.fvukovic.web.podaci.Lokacija;
import org.foi.nwtis.fvukovic.master.Iot_Master;
import org.foi.nwtis.dkermek.ws.serveri.StatusKorisnika;
import org.foi.nwtis.dkermek.ws.serveri.Uredjaj;

/**
 *
 * @author filip
 */
public class DretvaZahtjeva extends Thread {

    private ServerSocket serverSocket;
    public Socket s;
    public InputStream is;
    public OutputStream os;
    public ServletContext sc;
    public String zahtjev;
    public String korisnik;
    public String lozinka;
    public boolean uspjesnaNaredba = false;

    public DretvaZahtjeva(Socket s, ServletContext sc) {
        this.s = s;
        this.sc = sc;
    }

    @Override
    public void interrupt() {
        try {
            s.close();
        } catch (IOException ex) {
            System.out.println("ispis greske: " + ex);
        }
    }
/**
 * Glavna metoda dretve, gdje se dogada sva obrada zahtjeva i pozivi servisa
 */
    @Override
    public void run() {

//oWbMz
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        try {
            is = s.getInputStream();
            os = s.getOutputStream();
            StringBuffer sb = new StringBuffer();
            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            System.out.println("Primljena komanda: " + sb);

            String sintaksa_adminPause = "^USER ([^\\s]+); PASSWD ([^\\s]+); PAUSE;$";
            String sintaksa_adminStop = "^USER ([^\\s]+); PASSWD ([^\\s]+); STOP;$";
            String sintaksa_adminStart = "^USER ([^\\s]+); PASSWD ([^\\s]+); START;$";
            String sintaksa_adminStat = "^USER ([^\\s]+); PASSWD ([^\\s]+); STATUS;$";
            String sintaksa_adminWORK = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master WORK;$"; 
            String sintaksa_IoT_Master_WAIT = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master WAIT;$";
            String sintaksa_IoT_Master_Start = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master START;$";
            String sintaksa_IoT_Master_STOP = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master STOP;$";
            String sintaksa_IoT_Master_LOAD = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master LOAD;$";
            String sintaksa_IoT_Master_CLEAR = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master CLEAR;$"; 
            String sintaksa_IoT_Master_STATUS = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master STATUS;$";
            String sintaksa_IoT_Master_LIST = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master LIST;$";
            this.zahtjev = sb.toString();
            zapisiUDnevnik(true);

            if (ServerDretva.preuzimaj == false) {
                os.write("ERR 12".getBytes());
                os.flush();
                return;
            }
            this.zahtjev = sb.toString();
            Pattern p = Pattern.compile(sintaksa_adminPause);
            Matcher m = p.matcher(sb);
          
            boolean status = m.matches();
            System.out.println("koja opcija: " + status);
            if (status) {
                System.out.println("USERNAMEEE: " + m.group(1));

          
                {
                     
                }
                uspjesnaNaredba = true;
                System.out.println("koja opcija:sintaksa_adminPause " + status);
                boolean state = ServerPause();
                zapisiUDnevnik(state);
            }
            p = Pattern.compile(sintaksa_adminStart);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                System.out.println("USERNAMEEE: " + m.group(1));
                      if (!loginBaza(m.group(1), m.group(2))) {
                    os.write("ERR 10  ".getBytes());
                    os.flush();
                    return;
                }
           
                uspjesnaNaredba = true;
                //  System.out.println("ISPIIIIS::: "+ aktivirajGrupuIoT("fvukovic","oWbMz"));
                System.out.println("koja opcija: sintaksa_adminStart" + status);

                adminStart();
                zapisiUDnevnik(true);
            }
             p = Pattern.compile(sintaksa_IoT_Master_LIST);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                System.out.println("USERNAMEEE: " + m.group(1));
                      if (!loginBaza(m.group(1), m.group(2))) {
                    os.write("ERR 10  ".getBytes());
                    os.flush();
                    return;
                }
       
                uspjesnaNaredba = true;
                System.out.println("koja opcija: sintaksa_adminStop" + status);
                IoT_master_list();
                zapisiUDnevnik(true);
            }        
                    
                    
                    
            p = Pattern.compile(sintaksa_adminStop);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                System.out.println("USERNAMEEE: " + m.group(1));
                      if (!loginBaza(m.group(1), m.group(2))) {
                    os.write("ERR 10  ".getBytes());
                    os.flush();
                    return;
                }
                uspjesnaNaredba = true;
                System.out.println("koja opcija: sintaksa_adminStop" + status);
                adminStop();
                zapisiUDnevnik(true);
            }
            p = Pattern.compile(sintaksa_IoT_Master_Start);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                System.out.println("USERNAMEEE: " + m.group(1));
                      if (!loginBaza(m.group(1), m.group(2))) {
                    os.write("ERR 10  ".getBytes());
                    os.flush();
                    return;
                }
                uspjesnaNaredba = true;
                System.out.println("koja opcija: IOT_MASTEEEEER" + status);
                zapisiUDnevnik(true);
            }

            p = Pattern.compile(sintaksa_adminStat);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                uspjesnaNaredba = true;
                adminStatus();
                zapisiUDnevnik(true);
            }

            /**
             * IOT_MASTER sintakse za regex
             */
            p = Pattern.compile(sintaksa_adminWORK);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                uspjesnaNaredba = true;
                System.out.println("koja opcija: sintaksa_IOT WORK" + status);
                IoT_master_WORK();

            }
            p = Pattern.compile(sintaksa_IoT_Master_WAIT);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                IoT_master_WAIT();
                uspjesnaNaredba = true;
                System.out.println("koja opcija: sintaksa_IOT WAIT" + status);

            }
            p = Pattern.compile(sintaksa_IoT_Master_Start);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                IoT_master_start();
                uspjesnaNaredba = true;
                System.out.println("koja opcija: sintaksa_IOT START" + status);

            }
            p = Pattern.compile(sintaksa_IoT_Master_STOP);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                IoT_master_stop();
                uspjesnaNaredba = true;
                System.out.println("koja opcija: sintaksa_IOT STOP" + status);

            }
            p = Pattern.compile(sintaksa_IoT_Master_LOAD);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                IoT_master_load();
                uspjesnaNaredba = true;
                System.out.println("koja opcija:   sintaksa_IoT_Master_LOAD" + status);

            }
            p = Pattern.compile(sintaksa_IoT_Master_CLEAR);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                IoT_master_clear();
                uspjesnaNaredba = true;
                System.out.println("koja opcija: sintaksa_IoT_Master_CLEAR " + status);

            }
            p = Pattern.compile(sintaksa_IoT_Master_STATUS);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                IoT_master_status();
                uspjesnaNaredba = true;
                System.out.println("koja opcija: sintaksa_IoT_Master_CLEAR " + status);

            }

       

        } catch (IOException ex) {
            System.out.println(ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
        if (uspjesnaNaredba) {
               Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
            java.util.Properties properties = System.getProperties();
            System.out.println("SERVER MAIL: "+konf.dajPostavku("mail.server") );
            properties.put("mail.smtp.host",konf.dajPostavku("mail.server"));
            Session session = Session.getInstance(properties, null);

            try {
                 System.out.println("MAIL je poslan");
                MimeMessage message = new MimeMessage(session);
                message.setFrom(); 
                 System.out.println("adresa je : "+ konf.dajPostavku("mail.usernameThread"));
                message.setRecipients(Message.RecipientType.TO, konf.dajPostavku("mail.usernameThread"));
                message.setSubject(konf.dajPostavku("mail.subject")); 
                message.setText(this.zahtjev); 
                DateFormat dateFormatt = new SimpleDateFormat("yyyy/MM/dd");
                Date datet = new Date(); 
                message.setSentDate(datet); 
                Transport.send(message); 

            } catch (AddressException ex) {
                System.out.println("greska: " + ex);
            } catch (MessagingException ex) {
                    System.out.println("greska: "+ex);
            }

        }

    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }
/**
 * Pauzira radnu dretvu
 * @return 
 */
    private boolean ServerPause() {

        try {
            if (RadnaDretva.dretva == false) {
                os.write("Error 10;".getBytes());
                os.flush();
                return false;

            } else {
                RadnaDretva.dretva = false;
                os.write("OK".getBytes());
                os.flush();
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }
/**
 * pokrece radnu dretvu
 * 
 * @return 
 */
    private boolean adminStart() {
        try {
            if (RadnaDretva.dretva == true) {
                os.write("Error 11;".getBytes());
                os.flush();
                return false;

            } else {
                RadnaDretva.dretva = true;
                os.write("OK 10;".getBytes());
                os.flush();
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
/**
 *  ZAustavlja radnu dretvu i sve daljnje pristigle komande*
 */
    private boolean adminStop() {
        try {

            ServerDretva.preuzimaj = false;
            os.write("OK".getBytes());
            os.flush();
            return false;

        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private boolean adminStatus() {
        try {
            if (RadnaDretva.dretva == false) {
                os.write("OK 15;".getBytes());
                os.flush();

            } else {
                os.write("OK 14;".getBytes());
                os.flush();
            }

            return true;
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
/**
 * zapisuje svaki zahtjev u dnevnik
 * @param state 
 */
    public void zapisiUDnevnik(boolean state) {
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            System.out.println("Dosel sam u dnevnik");
            String strDate = sdfDate.format(now);
            String query = "insert into zahtjevi values(default,'fvukovic','" + this.zahtjev + "','" + strDate + "'," + state + ")";
            Statement s = Baza.c.createStatement();
            s.executeUpdate(query);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }
/**
 * upisuje uredaj
 * @param naziv
 * @param adresa 
 */
    public void uspisiUredaj(String naziv, String adresa) {

        GMKlijent novi = new GMKlijent();
        Lokacija loc = novi.getGeoLocation(adresa);
        int id = 0;

        try {

            String query = "SELECT id, MAX(id) FROM uredaji GROUP BY id desc limit 1";
            Statement s = Baza.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                id = rs.getInt("id");
            }

            query = "Select * from uredaji where naziv='" + naziv + "'";

            s = Baza.c.createStatement();
            rs = s.executeQuery(query);
            while (rs.next()) {
                os.write("Error 30".getBytes());
                os.flush();
                return;
            }
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            String strDate = sdfDate.format(now);
            id++;
            query = "insert into uredaji values(" + id + ",'" + naziv + "','" + loc.getLatitude() + "','" + loc.getLongitude() + "',1, vrijeme_promjene='" + strDate + "',vrijeme_kreiranja='" + strDate + "')";
            s = Baza.c.createStatement();
            s.executeUpdate(query);
            os.write("OK".getBytes());
            os.flush();
        } catch (SQLException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
/**
 * aktivacija uredaja
 * @param id 
 */
    public void aktivacijaUredaja(String id) {

        try {

            String query = "Select * from uredaji where id=" + id + "";

            Statement s = Baza.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt("status") == 1) {
                    os.write("Error 31".getBytes());
                    os.flush();
                } else {
                    query = "update uredaji set status=1 where id=" + id;
                    s = Baza.c.createStatement();
                    s.executeUpdate(query);
                    os.write("OK 10".getBytes());
                    os.flush();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
/**
 * login trenutnog korisnika prema bazi
 * @param username
 * @param pass
 * @return 
 */
    public boolean loginBaza(String username, String pass) {
        try {

            String query = "Select * from korisnici where username='" + username + "' and password ='" + pass + "';";

            Statement s = Baza.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                return true;

            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public void deaktivacijaUredaja(String id) {

        try {

            String query = "Select * from uredaji where id=" + id;

            Statement s = Baza.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt("status") == 0) {
                    os.write("Error 32".getBytes());
                    os.flush();
                } else {
                    query = "update uredaji set status=0 where id=" + id;
                    s = Baza.c.createStatement();
                    s.executeUpdate(query);
                    os.write("OK".getBytes());
                    os.flush();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void statusUredaja(String id) {

        try {

            String query = "Select * from uredaji where id=" + id;

            Statement s = Baza.c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt("status") == 0) {
                    os.write("OK 34".getBytes());
                    os.flush();
                } else {
                    query = "update uredaji set status=0 where id=" + id;
                    s = Baza.c.createStatement();
                    s.executeUpdate(query);
                    os.write("OK 35".getBytes());
                    os.flush();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void brisanjeUredaja(String id) {

        try {

            String query = "Select * from uredaji where id=" + id;

            Statement s = Baza.c.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {

                query = "delete from meteo  where id=" + id;
                s = Baza.c.createStatement();
                s.executeUpdate(query);
                query = "delete from uredaji  where id=" + id;
                s = Baza.c.createStatement();
                s.executeUpdate(query);
                os.write("OK 10".getBytes());
                os.flush();
                return;

            }
            os.write("Error 33".getBytes());
            os.flush();
            return;
        } catch (SQLException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Pozivanje metode servisa IoT_master
     *
     */
    
    
    /**
     * pokrece grupu 
     */
    public void IoT_master_start() {
        boolean status = Iot_Master.registrirajGrupuIoT("fvukovic", "oWbMz");
        System.out.println("START GRUPE: " + status);
        if (status) {
            try {
                os.write("OK 10".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                os.write("Error 20".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     * puni grupu sa predefiniranim uredajima
     */
     public void IoT_master_load() {
        boolean status = Iot_Master.ucitajSveUredjajeGrupe("fvukovic", "oWbMz");
        System.out.println("START GRUPE: " + status);
      
            try {
                os.write("OK 10".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
         
    }
    /**
     * brise sve uredaje iz grupe
     */
      public void IoT_master_clear() {
        boolean status = Iot_Master.obrisiSveUredjajeGrupe("fvukovic", "oWbMz");
        System.out.println("START GRUPE: " + status);
 
            try {
                os.write("OK 10".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
       
    }
      }
  /**
   * vraca status grupe
   */   
      public void IoT_master_status() {
          
          StatusKorisnika a = Iot_Master.dajStatusGrupeIoT("fvukovic", "oWbMz");
          System.out.println(a.AKTIVAN);
       if (a.AKTIVAN!=null) {
            try {
                os.write("OK 24".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                os.write("Error 25".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      
      }
      /**
       * ispisuje sve uredaje
       */
          public void IoT_master_list() {
              System.out.println("USAO U LIIIIIIIIIST");
          List<Uredjaj> lista = Iot_Master.dajSveUredjajeGrupe("fvukovic", "oWbMz");
        try {
            String rezultat="";
                System.out.println("BROJ UREDAJA: "+lista.size()+" ids. "+rezultat);
            for(Uredjaj uredaj : lista){
                rezultat+="{"+ uredaj.getId()+" " +uredaj.getNaziv()+"} <br/>";
            }
            System.out.println("BROJ UREDAJA: "+lista.size()+" ids. "+rezultat);
            os.write(("OK 10 <br/> "+rezultat).getBytes());
             os.flush();
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        } 
      }
      
    /**
     * Deregistrira grupu
     */  
    public void IoT_master_stop() {
        if (Iot_Master.deregistrirajGrupuIoT("fvukovic", "oWbMz")) {
            try {
                os.write("OK 10".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                os.write("Error 21".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
/**
 * aktivira grupu
 */
    public void IoT_master_WORK() {
        if (Iot_Master.aktivirajGrupuIoT("fvukovic", "oWbMz")) {
            System.out.println("WOK WORK");
            try {
                os.write("OK 10".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                os.write("Error 22".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    /**
     * pauzira rad grupe
     */
    public void IoT_master_WAIT() {
        if (Iot_Master.blokirajGrupuIoT("fvukovic", "oWbMz")) {
            try {
                os.write("OK 10".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                os.write("Error 23".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
/**
 * brise sve uredaje*/
    
    public void IoT_master_CLEAR() {
        if (Iot_Master.obrisiSveUredjajeGrupe("fvukovic", "oWbMz")) {
            try {
                os.write("OK 10".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                os.write("Error 24".getBytes());
                os.flush();
            } catch (IOException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

   
}
