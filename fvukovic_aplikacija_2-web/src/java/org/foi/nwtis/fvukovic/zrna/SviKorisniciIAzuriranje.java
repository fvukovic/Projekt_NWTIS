/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.servlet.ServletContext;
import org.foi.nwtis.fvukovic.slusaci.SlusacAplikacije;
import org.foi.nwtis.fvukovic.web.podaci.Korisnik;
import org.foi.nwtis.fvukovic.ws.klijenti.MeteoWSKlijent;

/**
 *
 * @author filip
 */
@Named(value = "sviKorisniciIAzuriranje")
@SessionScoped
public class SviKorisniciIAzuriranje implements Serializable {
    
     private List<Korisnik> korisnici = new ArrayList<>();
    private String ispis="adasd";
    private String username="";
    private String password="";
    private String email=""; 
    public static ServletContext sc;

    /**
     * Creates a new instance of PregledKorisnika
     */
   
    
    
    public SviKorisniciIAzuriranje() {
        System.out.println("USERNAME nakon logina"+registracijaPrijava.usernameSesija);
           
    }

    public String getIspis() {
        return ispis;
    }

    public String getUsername() {
        
        username =registracijaPrijava.usernameSesija;
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        password =SlusacAplikacije.passwordSesija;
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        email =SlusacAplikacije.emailSesija;
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIspis(String ispis) {
        this.ispis = ispis;
    }
    
  

    public List<Korisnik> getKorisnici() { 
        dohvatiKorisnike();
        return korisnici;
    }

    public void setKorisnici(List<Korisnik> korisnici) {
        this.korisnici = korisnici;
    }
    
    private void azurirajKorisnika(){
        String json =   MeteoWSKlijent.upodateUserREST();
        System.err.println("MOLIMTE:   "+json);
    }
   public void dohvatiKorisnike(){ 
    String json =   MeteoWSKlijent.dohvatiSveUsereREST(); 
        System.out.println(json);
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonArray array = jsonReader.readArray();
        jsonReader.close();
       System.out.println("OVOLIKO_"+array.size());
       korisnici.clear();
        for (JsonValue aa : array) {
            JsonReader reader = Json.createReader(new StringReader(aa.toString()));
            JsonObject jo = reader.readObject();
            int id = (jo.getInt("id"));            
            String username = jo.getString("username");
            String pass = jo.getString("password");
            String email = jo.getString("email");
            String id1=""+id;
            Korisnik novi = new Korisnik(id1,username,pass,email);
            korisnici.add(novi);
        }
   }
    
}