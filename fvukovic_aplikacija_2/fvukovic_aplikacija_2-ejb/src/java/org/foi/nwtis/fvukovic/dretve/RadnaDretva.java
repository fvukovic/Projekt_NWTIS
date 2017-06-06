package org.foi.nwtis.fvukovic.dretve;

import java.io.IOException;
import static java.lang.Thread.sleep;
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
import javax.jms.ObjectMessage;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext; 
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.fvukovic.web.podaci.Korisnik;

/**
 *
 * @author filip
 */
public class RadnaDretva extends Thread {

    private boolean prekid_obrade = false;
    private ServletContext sc = null;
    public Connection c;   
    public Folder folder;
    public static int redniBroj = 0;

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        int brojCiklusa = 1;
        System.out.println("dretva"); 
           String server ="127.0.0.1";
        String port = "143";
        String korisnik = "fvukovic1@nwtis.nastava.foi.hr";
        String lozinka = "12345";
        int trajanjeCiklusa = 5;
        //TODO i za ostale pareametre
        int trajanjeObrade = 0;
        //TODO odredi trajanje obrade
        int redniBrojCiklusa = 0;
         

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
                Folder folderNwtis = defaultFolder.getFolder("NWTiS_Poruke");
                if (folderNwtis == null) {
                    folderNwtis.create(Folder.HOLDS_MESSAGES);
                }
                Folder folderOther = defaultFolder.getFolder("OTHER");
                if (folderOther == null) {
                    folderOther.create(Folder.HOLDS_MESSAGES);
                }
               

                /**
                 *Sljedece sintakse nam sluze za provjeravanje komandi koje nam dolaze u sadrzaju maila
                 */
                Message[] messages = folder.getMessages(); 
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                pocetnoVrijeme = dateFormat.format(date);
                
                /**
                 *Prolazimo kroz sve pristigle poruke te ih obradujemo i razcrstavamo. Ovisi o komnadi (EVENT,TEMP,ADD) i naslovu maila
                 * TEMO
                 */
                for (int i = 0; i < messages.length; ++i) {

                    String sub = messages[i].getSubject();
                    String body = "";
                    if (messages[i].isMimeType("text/plain")) {
                        body = messages[i].getContent().toString();
                    } else {
                        brojPogreska++;
                        System.err.println("Ovo nije text!");
                    }
                        
                    if ("NWTIS_poruke".equals(sub)) { 
                                folderNwtis.appendMessages(new Message[]{messages[i]});

                       
                        } //Ulaz u drugu Sintaksu TEMP
                         

                    /**
                     * Resetiranje foldera INBOX
                     */
                    Flags deletede = new Flags(Flags.Flag.DELETED);
                    folder.setFlags(messages, deletede, true);
                    folder.close(true);
                }
                DateFormat dateFormatt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date datet = new Date();
                String zavrsnoVrijeme = dateFormatt.format(datet);
                sleep(trajanjeCiklusa * 1000 - trajanjeObrade);

             

                    /**
                     * slanje maila za statistiku
                     * Prvi dio nam sluzi za pravljenje formata #.##0,a drugi dio za slanje 
                     */
                    redniBroj = redniBroj+1;
                    String redniBrojPoruke = "";
                    if (redniBroj < 10) {
                        redniBrojPoruke = "____" + redniBroj;
                        redniBroj = redniBroj + 20;
                    } else {
                        if (redniBroj < 100) {
                            redniBrojPoruke = "___" + redniBroj;
                        } else {
                            if (redniBroj < 1000) {
                                redniBrojPoruke = "__" + redniBroj;
                            } else {
                                int broj = redniBroj;
                                int broj2 = broj - (broj % 1000);
                                String ostatak=""+(broj % 1000);
                                if((broj % 1000)<10){
                                    ostatak="00"+(broj % 1000);
                                }else{
                                    if((broj % 1000)<100){
                                          ostatak="0"+(broj % 1000);
                                    }
                                }
                                        
                                redniBrojPoruke = broj2 / 1000 + "." + ostatak;

                            }

                        }
                    }
               
         

            } catch (InterruptedException ex) {
 
            } catch (MessagingException ex) { 
            } catch (IOException ex) { 
            }  
        }
    }
    @Override
    public synchronized void start() {
        
        System.out.println("dretva");
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    public RadnaDretva() { 
    }

}
