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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
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

/**
 *
 * @author filip
 */
public class DretvaZahtjeva extends Thread {

    private ServerSocket serverSocket;
    public Socket s;
    public InputStream is;
    public OutputStream os;
    public Connection c;
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

    @Override
    public void run() {

        spojiNaBazu();

        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        this.zahtjev = "USER pero; PASSWD 123456; START;";
        zapisiUDnevnik(true);
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
            String sintaksa_adminWORK = "^USER ([^\\s]+); PASSWD ([^\\s]+); WORK;$"; 
              String sintaksa_IoT_Master_Start = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master WORK;$";

            if (!loginBaza("majstor", "12")) {
                os.write("Neuspjesna prijava".getBytes());
                os.flush();
                return;
            }
            {
                os.write("Uspjesna prijava".getBytes());
                os.flush();
            }

            if (ServerDretva.preuzimaj == false) {
                os.write("ERR 12".getBytes());
                os.flush();
                return;
            }
            this.zahtjev = sb.toString();
            Pattern p = Pattern.compile(sintaksa_adminPause);
            Matcher m = p.matcher(sb);
            this.korisnik = "fvukovic";
            boolean status = m.matches();
            System.out.println("koja opcija: " + status);
            if (status) {
                uspjesnaNaredba = true;
                System.out.println("koja opcija:sintaksa_adminPause " + status);
                boolean state = ServerPause();
                zapisiUDnevnik(state);
            }
            p = Pattern.compile(sintaksa_adminStart);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                uspjesnaNaredba = true;
                //  System.out.println("ISPIIIIS::: "+ aktivirajGrupuIoT("fvukovic","oWbMz"));
                System.out.println("koja opcija: sintaksa_adminStart" + status);

                adminStart();
                zapisiUDnevnik(true);
            }
            p = Pattern.compile(sintaksa_adminStop);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                uspjesnaNaredba = true;
                System.out.println("koja opcija: sintaksa_adminStop" + status);
                adminStop();
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
            // IOT

            p = Pattern.compile(sintaksa_adminWORK);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                uspjesnaNaredba = true;
                System.out.println("koja opcija: sintaksa_IOT WORK" + status);

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
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", "127.0.0.1");
            Session session = Session.getInstance(properties, null);

            try {
                MimeMessage message = new MimeMessage(session);
                Address[] toAddresses = InternetAddress.parse("servis@nwtis.nastava.foi.hr");
                message.setRecipients(Message.RecipientType.TO, toAddresses);
                Konfiguracija konf = (Konfiguracija) sc.getAttribute("Baza_Konfig");
                message.setSubject(konf.dajPostavku("mail.subject"));
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();
                String strDate = sdfDate.format(now);
                message.setText(this.zahtjev + "/n" + now);
                DateFormat dateFormatt = new SimpleDateFormat("yyyy/MM/dd");
                Date datet = new Date();
                message.setSentDate(datet);
                Transport.send(message);
            } catch (MessagingException ex) {
                Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

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


    public void spojiNaBazu() {
        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
        System.out.println("PRINTAJ ME OVE NOCIIII");
        try {
            System.out.println("DRIVER: " + bp_konf.getDriverDatabase());
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

    public void zapisiUDnevnik(boolean state) {
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            System.out.println("Dosel sam u dnevnik");
            String strDate = sdfDate.format(now);
            String query = "insert into zahtjevi values(default,'fvukovic','" + this.zahtjev + "','" + strDate + "'," + state + ")";
            Statement s = c.createStatement();
            s.executeUpdate(query);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public void uspisiUredaj(String naziv, String adresa) {

        GMKlijent novi = new GMKlijent();
        Lokacija loc = novi.getGeoLocation(adresa);
        spojiNaBazu();
        int id = 0;

        try {

            String query = "SELECT id, MAX(id) FROM uredaji GROUP BY id desc limit 1";
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                id = rs.getInt("id");
            }

            query = "Select * from uredaji where naziv='" + naziv + "'";

            s = c.createStatement();
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
            s = c.createStatement();
            s.executeUpdate(query);
            os.write("OK".getBytes());
            os.flush();
        } catch (SQLException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void aktivacijaUredaja(String id) {

        try {

            String query = "Select * from uredaji where id=" + id + "";

            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt("status") == 1) {
                    os.write("Error 31".getBytes());
                    os.flush();
                } else {
                    query = "update uredaji set status=1 where id=" + id;
                    s = c.createStatement();
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

    public boolean loginBaza(String username, String pass) {
        try {

            String query = "Select * from korisnici where username='" + username + "' and password ='" + pass + "';";

            Statement s = c.createStatement();
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

            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt("status") == 0) {
                    os.write("Error 32".getBytes());
                    os.flush();
                } else {
                    query = "update uredaji set status=0 where id=" + id;
                    s = c.createStatement();
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

            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt("status") == 0) {
                    os.write("OK 34".getBytes());
                    os.flush();
                } else {
                    query = "update uredaji set status=0 where id=" + id;
                    s = c.createStatement();
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

            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {

                query = "delete from meteo  where id=" + id;
                s = c.createStatement();
                s.executeUpdate(query);
                query = "delete from uredaji  where id=" + id;
                s = c.createStatement();
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
     * */
    
    public void IoT_master_start (){
            if(Iot_Master.registrirajGrupuIoT(korisnik, lozinka)){
                try {
                    os.write("OK 10".getBytes());
                    os.flush();
                } catch (IOException ex) {
                    Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            try {
                    os.write("Error 20".getBytes());
                    os.flush();
                } catch (IOException ex) {
                    Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }
        public void IoT_master_stop (){
            if(Iot_Master.deregistrirajGrupuIoT(korisnik, lozinka)){
                try {
                    os.write("OK 10".getBytes());
                    os.flush();
                } catch (IOException ex) {
                    Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            try {
                    os.write("Error 21".getBytes());
                    os.flush();
                } catch (IOException ex) {
                    Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }
       public void IoT_master_WORK (){
            if(Iot_Master.aktivirajGrupuIoT(korisnik, lozinka)){
                try {
                    os.write("OK 10".getBytes());
                    os.flush();
                } catch (IOException ex) {
                    Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            try {
                    os.write("Error 22".getBytes());
                    os.flush();
                } catch (IOException ex) {
                    Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
    }
             public void IoT_master_WAIT (){
            if(Iot_Master.blokirajGrupuIoT(korisnik, lozinka)){
                try {
                    os.write("OK 10".getBytes());
                    os.flush();
                } catch (IOException ex) {
                    Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            try {
                    os.write("Error 23".getBytes());
                    os.flush();
                } catch (IOException ex) {
                    Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    }
           public void IoT_master_CLEAR (){
            if(Iot_Master.obrisiSveUredjajeGrupe(korisnik, lozinka)){
                try {
                    os.write("OK 10".getBytes());
                    os.flush();
                } catch (IOException ex) {
                    Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
            try {
                    os.write("Error 24".getBytes());
                    os.flush();
                } catch (IOException ex) {
                    Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            }
             public void IoT_master_CLEARa (){
             }
      
       
    
}
