/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.zrna;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.ServletContext;
import org.foi.nwtis.fvukovic.konfiguracije.Konfiguracija;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.fvukovic.web.podaci.Izbornik;
import org.foi.nwtis.fvukovic.web.podaci.Poruka;

/**
 *
 * @author filip
 */
@Named(value = "pregledMailova")
@RequestScoped
public class PregledMailova {

    /**
     * Creates a new instance of PregledMailova
     */

    private String posluzitelj;
    private String korisnik;
    private String lozinka;
    public static ServletContext sc;
    
    private String id;
    private ArrayList<Izbornik> mape = new ArrayList<>();
    private String odabranaMapa;
    private ArrayList<Poruka> poruke = new ArrayList<>();
    private ArrayList<Poruka> porukePretrazivanje = new ArrayList<>();
    private int ukupnoPorukaMapa = 0;
    private int brojPrikazanihPoruka = 0;
    private int pozicijaOdPoruke = 0;
    private int pozicijaDoPoruke = 0;
    private String traziPoruke;
    public static int pocetakPoruka;
    private int maxBrojPoruka;
    public static int brojPorukaFoldera;
    public static boolean trazi = false;
    private String port;
    private String server;
    private String username;
    private String password;

    /**
     * Creates a new instance of PregledPoruka i preuzima poruke iz foldera
     * index i prikazuje ih
     */
    public PregledMailova() {
        preuzmiMape();
        preuzmiPoruke();
        Konfiguracija konf = (Konfiguracija) sc.getAttribute("Mail_Konfig");
        this.maxBrojPoruka = Integer.parseInt(konf.dajPostavku("mail.numMessages"));

    }

    /**
     * Preuzima sve mape te ih prikazuje na ekran
     */
    void preuzmiMape() {
        try {
            //TODO promjeni sa stvarnim preuzimanjem mapa
            mape.add(new Izbornik("INBOX", "INBOX"));
            Konfiguracija konf = (Konfiguracija) sc.getAttribute("Mail_Konfig");
            mape.add(new Izbornik(konf.dajPostavku("mail.folderNWTiS"), konf.dajPostavku("mail.folderNWTiS")));
            mape.add(new Izbornik(konf.dajPostavku("mail.folderOther"), konf.dajPostavku("mail.folderOther")));
            //     mape.add(new Izbornik("Sent", "Sent"));
            //      mape.add(new Izbornik("Spam", "Spam"));
           
            server = konf.dajPostavku("mail.server");
            port = konf.dajPostavku("mail.port");
            username = konf.dajPostavku("mail.usernameThread");
            password = konf.dajPostavku("mail.passwordThread");
            int trajanjeCiklusa = Integer.parseInt(konf.dajPostavku("mail.timeSecThread"));
            //TODO i za ostale pareametre
            int trajanjeObrade = 0;
            //TODO odredi trajanje obrade
            int redniBrojCiklusa = 0;
            BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
             java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", server);
            Session session = Session.getInstance(properties, null);

            // Connect to the store
            Store store = session.getStore("imap");
            store.connect(server, username, password);
            for (Izbornik mapa : mape) {
                Folder folder = store.getFolder(mapa.getVrijednost());
                folder.open(Folder.READ_ONLY);
                Message[] messages = folder.getMessages();
                mapa.setBroj(messages.length);

            }
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(PregledMailova.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(PregledMailova.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Preuzima sve poruke te ih prikazuje na ekran
     */
    void preuzmiPoruke() {
        try {
            poruke.clear();
            java.util.Properties properties = System.getProperties();
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
            properties.put("mail.smtp.host", server);
            Session session = Session.getInstance(properties, null);

            // Connect to the store
            Store store = session.getStore("imap");
            store.connect(server, korisnik, lozinka);

            // Open the INBOX folder
            if (odabranaMapa == null) {
                odabranaMapa = "INBOX";
            }

            Folder folder = store.getFolder(odabranaMapa);
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.getMessages();
            this.brojPorukaFoldera = messages.length;
            String[] parts = null;
            if (this.traziPoruke != null) {
                parts = this.traziPoruke.split(" ");
            }
            if (this.trazi) {
                int b = 0;
                for (int i = 0; i < messages.length; ++i) {
                    boolean postoji = true;
                    for (String kljucnaRijec : parts) {
                        String body = messages[i].getContent().toString();
                        if (!body.contains(kljucnaRijec)) {
                            postoji = false;
                        }
                    }
                    if (postoji) {
                        String sub = messages[i].getSubject();
                        Address[] froms = messages[i].getFrom();
                        String body = messages[i].getContent().toString();
                        porukePretrazivanje.add(new Poruka(Integer.toString(i), new Date(), new Date(), froms[0].toString(), sub, body, "0"));
                    }

                }
                b = 0;
                for (int i = pocetakPoruka; i < porukePretrazivanje.size(); ++i) {
                    b++;
                    if (b > 6) {
                        break;
                    }
                    String sub = porukePretrazivanje.get(i).getPredmet();
                    String froms = porukePretrazivanje.get(i).getSalje();
                    String body = porukePretrazivanje.get(i).getSadrzaj();
                    poruke.add(new Poruka(Integer.toString(i), new Date(), new Date(), froms, sub, body, "0"));

                }

            } else {
                int b = 0;
                for (int i = this.pocetakPoruka; i < messages.length; ++i) {
                    if (b == 6) {
                        break;
                    }
                    b++;
                    String sub = messages[i].getSubject();
                    Address[] froms = messages[i].getFrom();
                    String body = messages[i].getContent().toString();
                    poruke.add(new Poruka(Integer.toString(i), new Date(), new Date(), froms[0].toString(), sub, body, "0"));

                }
            }

            int b = 0;
            if (trazi) {
                ukupnoPorukaMapa = porukePretrazivanje.size();
            } else {
                ukupnoPorukaMapa = messages.length;
            }
            //TODO promjeni sa stvarnim preuzimanjem poruka
            //TODO razmisli o optimiranju preuzimanja poruka
            int i = 0;
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(PregledMailova.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(PregledMailova.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PregledMailova.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * mjenja folder maila te prikazuje njegove poruke
     *
     * @return
     */
    public String promjenaMape() {
        this.pocetakPoruka = 0;
        this.trazi = false;
        this.preuzmiPoruke();
        return "promjenaMape";
    }

    /**
     * U this trazis stavlja true, sto poslije kod preuzmiPoruke() nam govori da
     * pretrazuje po kljucnih rijecima, a ne da ispise sve poruke
     */
    public void brisiMail() throws NoSuchProviderException, MessagingException {
         java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", server);
            Session session = Session.getInstance(properties, null);
        Store store = session.getStore("imap");
        store.connect(server, username, password);
        Folder folder = store.getFolder(odabranaMapa);
        folder.open(Folder.READ_ONLY);
        Message[] messages = folder.getMessages();
        System.out.println("ID: "+this.id+" poruka:"+messages.length+" MAPA: "+odabranaMapa); 
            Flags deletede = new Flags(Flags.Flag.DELETED);
                        messages[Integer.parseInt(this.id)].setFlags(deletede, true);
                        folder.close(true); 
                        System.out.println("UKUPNO : "+  messages.length);
                        this.ukupnoPorukaMapa=this.ukupnoPorukaMapa-1;
                        preuzmiPoruke(); 
                        

    }

    /**
     * prikazuje prethodnih X poruka, zavisi o konfiguraciji
     *
     * @return
     */
    public String prethodnePoruke() {
        this.pocetakPoruka = this.pocetakPoruka - this.maxBrojPoruka;
        if (this.pocetakPoruka < 0) {
            this.pocetakPoruka = 0;
        }
        this.preuzmiPoruke();
        return "prethodnePoruke";
    }

    /**
     * prikazuje sljedece X poruka, zavisi o konfiguraciji
     *
     * @return
     */
    public String sljedecePoruke() {
        try {
            java.util.Properties properties = System.getProperties();
//            Konfiguracija konf = (Konfiguracija) sc.getAttribute("Mail_Konfig");
//            String server = konf.dajPostavku("mail.server");
//            String port = konf.dajPostavku("mail.port");
//            String korisnik = konf.dajPostavku("mail.usernameThread");
//            String lozinka = konf.dajPostavku("mail.passwordThread");
//            int trajanjeCiklusa = Integer.parseInt(konf.dajPostavku("mail.timeSecThread"));
            //TODO i za ostale pareametre
            int trajanjeObrade = 0;
            //TODO odredi trajanje obrade
            int redniBrojCiklusa = 0;
//            BP_Konfiguracija bp_konf = (BP_Konfiguracija) sc.getAttribute("BP_Konfig");
            properties.put("mail.smtp.host", "127.0.0.1");
            Session session = Session.getInstance(properties, null);

            // Connect to the store
            Store store = session.getStore("imap");
            store.connect("127.0.0.1", "fvukovic1@nwtis.nastava.foi.hr", "123456");

            // Open the INBOX folder          
            Folder folder = store.getFolder(odabranaMapa);
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.getMessages();
            if ((this.pocetakPoruka + this.maxBrojPoruka) < (messages.length + 1)) {
                this.pocetakPoruka = this.pocetakPoruka + this.maxBrojPoruka;
            }
            if (trazi) {
                System.out.println("ISPIS SVIH PRETRAZIVANJE BROJ : " + porukePretrazivanje.size());
                if ((this.pocetakPoruka + this.maxBrojPoruka) < (porukePretrazivanje.size() + 1)) {
                    this.pocetakPoruka = this.pocetakPoruka + this.maxBrojPoruka;
                }
            }

            this.preuzmiPoruke();

        } catch (NoSuchProviderException ex) {
            Logger.getLogger(PregledMailova.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(PregledMailova.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "sljedecePoruke";
    }

    public String promjenaJezika() {
        return "promjenaJezika";
    }

    public String saljiPoruku() {
        return "saljiPoruku";
    }

    public ArrayList<Izbornik> getMape() {
        return mape;
    }

    public ArrayList<Poruka> getPoruke() {
        return poruke;
    }

    public String getOdabranaMapa() {
        return odabranaMapa;
    }

    public int getUkupnoPorukaMapa() {
        return ukupnoPorukaMapa;
    }

    public String getTraziPoruke() {
        return traziPoruke;
    }

    public void setOdabranaMapa(String odabranaMapa) {
        this.odabranaMapa = odabranaMapa;
    }

    public void setUkupnoPorukaMapa(int ukupnoPorukaMapa) {
        this.ukupnoPorukaMapa = ukupnoPorukaMapa;
    }

    public void setTraziPoruke(String traziPoruke) {
        this.traziPoruke = traziPoruke;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
