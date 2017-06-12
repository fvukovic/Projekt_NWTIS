/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.dretve;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.ServletContext;
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author grupa_3
 *
 * Klasa(dretva) koja obraduje pristigle poruke u sandučić i dijeli ih u razne
 * foldere koji su postavljeni u konfiguraciji. Ako folderi ne postoje, dretva
 * ih sama kreira. Na kraju svakog ciklusa obrade poruka, podaci, odnosno sve
 * poruke i njezine pojedinosti se salju administratoru.
 */
public class ObradaPoruka extends Thread {

    public static ServletContext sc = null;
    private boolean stop = false;
    public Connection c;
    public Folder folder;
    public static int redniBroj = 0;

    @Override
    public void interrupt() {
        stop = true;
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            /**
             * Sljedeci kod nam dohvaca sve postavke iz konfiguracijske datoteke, a
             * to su serve,port,korisnik,lozinka za odredenog korisnika koji ce
             * koristiti mail sustav.
             */
            sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("Mail_Konfig");
        String server = konf.dajPostavku("mail.server");
        String port = konf.dajPostavku("mail.port");
        String korisnik = konf.dajPostavku("mail.usernameThread");
        String lozinka = konf.dajPostavku("mail.passwordThread");
        int trajanjeCiklusa = Integer.parseInt(konf.dajPostavku("mail.timeSecThread"));
        //TODO i za ostale pareametre
        int trajanjeObrade = 0;
        //TODO odredi trajanje obrade
        int redniBrojCiklusa = 0;
//        BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");

//        try {
//            Class.forName(bp_konf.getDriverDatabase());
//        } catch (ClassNotFoundException ex) {
//            System.out.println(ex);
//            return;
//        }
        /**
         * Spajamo se na bazu kako bi upisivali potrebne podatke
         */
        /**
         * Petlja koja ce svakog odredenog vremena(definirano u konfiguraciji)
         * provjeravati da li ima poruka u sanducicu i obradivati ih
         */
        while (true) {
            String pocetnoVrijeme = "";
            int brojDodaniIot = 0;
            int brojMjerenihTemp = 0;
            int brojPogreska = 0;
            int brojEventa = 0;
            redniBrojCiklusa++;
            System.out.println("Ciklus dretve ObradaPoruka: " + redniBrojCiklusa);
            try {

                // Start the session
                java.util.Properties properties = System.getProperties();
                properties.put("mail.smtp.host", server);
                Session session = Session.getInstance(properties, null);

                // Connect to the store
                Store store = session.getStore("imap");
                store.connect(server, korisnik, lozinka);

                // Open the INBOX folder
                this.folder = store.getFolder("INBOX");
                folder.open(Folder.READ_WRITE);
                Folder defaultFolder = store.getDefaultFolder();
                Folder folderNwtis = defaultFolder.getFolder(konf.dajPostavku("mail.folderNWTiS"));
                if (folderNwtis == null) {
                    folderNwtis.create(Folder.HOLDS_MESSAGES);
                }
                Folder folderOther = defaultFolder.getFolder(konf.dajPostavku("mail.folderOther"));
                if (folderOther == null) {
                    folderOther.create(Folder.HOLDS_MESSAGES);
                }

                /**
                 * Sljedece sintakse nam sluze za provjeravanje komandi koje nam
                 * dolaze u sadrzaju maila
                 */
                Message[] messages = folder.getMessages();

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                pocetnoVrijeme = dateFormat.format(date);

                /**
                 * Prolazimo kroz sve pristigle poruke te ih obradujemo i
                 * razcrstavamo. Ovisi o   naslovu
                 * maila TEMO
                 */
                for (int i = 0; i < messages.length; ++i) {

                    String sub = messages[i].getSubject();
                    String body = "";
                    if (messages[i].isMimeType("text/plain")) {
                        body = messages[i].getContent().toString();
                        System.out.println("PORUKA: " + body);
                    } else {
                        brojPogreska++;
                        System.err.println("Ovo nije text!");
                    }
                    if (messages[i].getSubject().equals(konf.dajPostavku("mail.subject"))) {
                        folderNwtis.appendMessages(new Message[]{messages[i]});
                        Flags deletede = new Flags(Flags.Flag.DELETED);
                        messages[i].setFlags(deletede, true);
                     

                    }

                    /**
                     * Resetiranje foldera INBOX
                     */
                }
                   folder.close(true);
                DateFormat dateFormatt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date datet = new Date();
                String zavrsnoVrijeme = dateFormatt.format(datet);
                sleep(3000);

            } catch (InterruptedException ex) {

                Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MessagingException ex) {
                Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

   

}
