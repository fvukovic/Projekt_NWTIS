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
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import static javax.ws.rs.core.Response.status;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.fvukovic.rest.ws.MeteoRESTResourceContainer;
import static org.foi.nwtis.fvukovic.rest.ws.MeteoRESTResourceContainer.sc;

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

    public DretvaZahtjeva(Socket s, ServletContext sc) {
        this.s = s;
        this.sc = sc;
    }

    @Override
    public void interrupt() {
        try {
            s.close();
        } catch (IOException ex) {
        }
    }

    @Override
    public void run() {

        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        spojiNaBazu();
        this.zahtjev="USER pero; PASSWD 123456; START;";
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
            String sintaksaIOT_WORK = "^IoT ([^\\s]+); WORK;$";
            this.zahtjev = sb.toString();
            Pattern p = Pattern.compile(sintaksa_adminPause);
            Matcher m = p.matcher(sb);
            this.korisnik = m.group(1);
            boolean status = m.matches();
            System.out.println("koja opcija: " + status);
            if (status) {
                System.out.println("koja opcija:sintaksa_adminPause " + status);
                boolean state=ServerPause();
                zapisiUDnevnik(state);
            }
            p = Pattern.compile(sintaksa_adminStart);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                //  System.out.println("ISPIIIIS::: "+ aktivirajGrupuIoT("fvukovic","oWbMz"));
                System.out.println("koja opcija: sintaksa_adminStart" + status);
                
                adminStart();
                zapisiUDnevnik(true);
            }
            p = Pattern.compile(sintaksa_adminStop);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                System.out.println("koja opcija: sintaksa_adminStop" + status);
                adminStop();
                 zapisiUDnevnik(true);
            }
            p = Pattern.compile(sintaksa_adminStat);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                adminStatus();
                 zapisiUDnevnik(true);
            }
            // IOT

            p = Pattern.compile(sintaksa_adminWORK);
            m = p.matcher(sb);
            status = m.matches();
            if (status) {
                System.out.println("koja opcija: sintaksa_IOT WORK" + status);

            }

            //TODO treba provjeriti ima li "mjesta" za novu radnu dretvu 
        } catch (IOException ex) {
            System.out.println(ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                  System.out.println(ex);
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
            if (ServerDretva.preuzimaj == false) {
                os.write("Error 12;".getBytes());
                os.flush();
                return false;

            } else {
                ServerDretva.preuzimaj = false;
                os.write("OK".getBytes());
                os.flush();
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private boolean adminStatus() {
        try {
            if (ServerDretva.preuzimaj == false) {
                os.write("OK 15;".getBytes());
                os.flush();

            }

            String response = "OK";
            if (RadnaDretva.dretva == false) {

            } else {
                response = response + " 14;";
            }

            os.write(response.getBytes());
            os.flush();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }return true;
    }

    public static Boolean aktivirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.aktivirajGrupuIoT(korisnickoIme, korisnickaLozinka);
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

    public void zapisiUDnevnik(boolean state) {
        try {
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();
            System.out.println("Dosel sam u dnevnik");
            String strDate = sdfDate.format(now);
            spojiNaBazu();
            String query = "insert into zahtjevi values(default,'fvukovic','" + this.zahtjev + "','" + strDate + "',"+state+")";
            Statement s = c.createStatement();
            s.executeUpdate(query);
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

}
