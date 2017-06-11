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
        /**
         * Sljedeci kod nam dohvaca sve postavke iz konfiguracijske datoteke, a
         * to su serve,port,korisnik,lozinka za odredenog korisnika koji ce
         * koristiti mail sustav.
         */
        
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
           
        }

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
                 *Sljedece sintakse nam sluze za provjeravanje komandi koje nam dolaze u sadrzaju maila
                 */
                Message[] messages = folder.getMessages();
                String sintaksaADD = "^ADD IoT (\\d{1,6}) (\".{1,30}\") GPS: (-?\\d{1,3}.\\d{6}),(-?\\d{1,3}.\\d{6});$";
                String sintaksaTEMP = "^TEMP IoT (\\d{1,6}) T: (\\d{4}.\\d{2}.\\d{2}) (\\d{2}:\\d{2}:\\d{2}) C: (-?\\d{1,2}.\\d);$";
                String sintaksaEVENT = "^EVENT IoT (\\d{1,6}) T: (\\d{4}.\\d{2}.\\d{2}) (\\d{2}:\\d{2}:\\d{2}) F: (\\d{1,2});$";
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

                    if (konf.dajPostavku("mail.subject").equals(sub)) {
                        Pattern pattern = Pattern.compile(sintaksaADD);
                        Matcher m = pattern.matcher(body);
                        boolean status = m.find();
                        if (status) {
                            int poc = 0;
                            int kraj = m.groupCount();
                            String query = "select * from uredaji";
                            Statement s = c.createStatement();
                            ResultSet rs = s.executeQuery(query);

                            boolean postojiUredaj = false;
                            while (rs.next()) {
                                if (rs.getString("id").equals(m.group(1))) {
                                    brojPogreska++;
                                    System.err.println("Greska, Uredaj vec postoji");
                                    postojiUredaj = true;

                                }
                            }
                            if (!postojiUredaj) {
                                folderNwtis.appendMessages(new Message[]{messages[i]});

                                DateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date date1 = new Date();
                                System.err.println("Upisan uredjaj");
                                query = "insert into uredaji values (" + m.group(1) + ",'" + m.group(2).substring(1, m.group(2).length() - 1) + "'," + m.group(3) + "," + m.group(4) + ",1,'" + dateFormat.format(date) + "','" + dateFormat.format(date) + "')";
                                s = c.createStatement();
                                s.executeUpdate(query);
                                brojDodaniIot++;

                            }
                        } //Ulaz u drugu Sintaksu TEMP
                        else {
                            pattern = Pattern.compile(sintaksaTEMP);
                            m = pattern.matcher(body);
                            status = m.lookingAt();
                            if (status) {
                                int poc = 0;
                                int kraj = m.groupCount();
                                String query = "select * from uredaji";
                                Statement s = c.createStatement();
                                ResultSet rs = s.executeQuery(query);

                                boolean postojiUredaj = false;
                                while (rs.next()) {
                                    if (rs.getString("id").equals(m.group(1))) {
                                        String my_new_str = m.group(2).toString().replace(".", "/");
                                        folderNwtis.appendMessages(new Message[]{messages[i]});
                                        DateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                        Date date1 = new Date();
                                        query = "insert into temperature values (" + m.group(1) + ",34,'" + my_new_str + " " + m.group(3) + "','" + dateFormat.format(date) + "' )";
                                        s = c.createStatement();
                                        s.executeUpdate(query);
                                        brojMjerenihTemp++;
                                        System.out.println("Upisana temparature");
                                        postojiUredaj = true;

                                    }
                                }
                                if (!postojiUredaj) {
                                    brojPogreska++;
                                    System.err.println("Ne postoji uredaj");
                                    folderOther.appendMessages(new Message[]{messages[i]});
                                }
                            } //Ulaz u trecu sintaksu EVENT
                            else {

                                pattern = Pattern.compile(sintaksaEVENT);
                                m = pattern.matcher(body);
                                status = m.find();
                                if (status) {
                                    int poc = 0;
                                    int kraj = m.groupCount();
                                    for (int j = poc; j <= kraj; j++) {
                                        System.out.println(i + ". " + m.group(i));
                                    }
                                    String query = "select * from uredaji";
                                    Statement s = c.createStatement();
                                    ResultSet rs = s.executeQuery(query);

                                    boolean postojiUredaj = false;
                                    while (rs.next()) {
                                        if (rs.getString("id").equals(m.group(1))) {
                                            String my_new_str = m.group(2).toString().replace(".", "/");
                                            folderNwtis.appendMessages(new Message[]{messages[i]});
                                            DateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                            Date date2 = new Date();
                                            query = "insert into dogadaji values (" + m.group(1) + "," + m.group(4) + ",'" + my_new_str + " " + m.group(3) + "','" + dateFormat.format(date) + "' )";
                                            s = c.createStatement();
                                            s.executeUpdate(query);
                                            brojEventa++;
                                            postojiUredaj = true;
                                            System.err.println("Dogadaj upisan");

                                        }
                                    }
                                    if (!postojiUredaj) {
                                        brojPogreska++;
                                        System.err.println("Ne postoji uredaj");
                                        folderOther.appendMessages(new Message[]{messages[i]});
                                    }
                                } else {
                                    //Ako je upisana kriva naredba
                                    System.err.println("Kriva naredba");
                                    brojPogreska++;
                                    folderOther.appendMessages(new Message[]{messages[i]});

                                }
                            }

                        }

                    } else {
                        System.err.println("Subject nije pravilan");
                        folderOther.appendMessages(new Message[]{messages[i]});
                        brojPogreska++;
                    }

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

                try {

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

                    MimeMessage message = new MimeMessage(session);
                    message.setFrom("SERVER");
                    message.setRecipients(Message.RecipientType.TO, konf.dajPostavku("mail.usernameStatistics"));
                    message.setSubject(konf.dajPostavku("mail.subjectStatistics"));
                    message.setText("Redni broj poruke statistike: " + redniBrojPoruke +"\n Broj poruka: " + messages.length + "\n Broj pogresaka: " + brojPogreska + "\n Broj dodanih IoT:" + brojDodaniIot + "\n broj mjerenih "
                            + "\n temperatura: " + brojMjerenihTemp + "\n Broj izvršenih EVENT: " + brojEventa);
                    System.out.println("Redni broj poruke statistike: " + redniBrojPoruke + "\n Broj poruka: " + messages.length + "\n Broj pogresaka: " + brojPogreska + "\n Broj dodanih IoT:" + brojDodaniIot + "\n broj mjerenih "
                            + "temperatura: " + brojMjerenihTemp + "\n Broj izvršenih EVENT: " + brojEventa + "\n Obrada započela u: " + pocetnoVrijeme + "\n Obrada završila u: " + zavrsnoVrijeme);
                    Transport.send(message);
                } catch (AddressException ex) {
                    
                }

            } catch (InterruptedException ex) {

              
            } catch (MessagingException ex) {
                 
            } catch (IOException ex) {
           
            } catch (SQLException ex) {
                
                Flags deletede = new Flags(Flags.Flag.DELETED);

                try {
                    Message[] messages = null;
                    messages = folder.getMessages();
                    folder.setFlags(messages, deletede, true);
                    folder.close(true);
                } catch (MessagingException ex1) {
                    
                }

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
