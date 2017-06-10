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
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Cookie;
import org.foi.nwtis.fvukovic.sesije.SessionUtils;
import org.foi.nwtis.fvukovic.slusaci.SlusacAplikacije;
import org.foi.nwtis.fvukovic.web.podaci.Korisnik;
import org.foi.nwtis.fvukovic.ws.klijenti.MeteoWSKlijent;
/**
 *
 * @author filip
 */
@Named(value = "registracijaPrijava")
@SessionScoped
public class registracijaPrijava implements Serializable {

    /**
     * Creates a new instance of registracijaPrijava
     */
    private String ispis = "adasd";
    private String username = "";
    private String password = "";
    private String email = "";
    private String usernameReg = "";
    private String passwordReg = "";
    private String emailReg = "";
    private String regGreska = "";
    public static String usernameSesija = "";
    public static int idSesija ;
    public static String emailSesija ;
    public static String passwordSesija ;

    public String getRegGreska() {
        return regGreska;
    }

    public void setRegGreska(String regGreska) {
        this.regGreska = regGreska;
    }

    public registracijaPrijava() {
        
        System.out.println("JSONaaaaaaaa"+ MeteoWSKlijent.dohvatiSveUsereREST());
        
    }

    public String getUsernameReg() {
        return usernameReg;
    }

    public void setUsernameReg(String usernameReg) {
        this.usernameReg = usernameReg;
    }

    public String getPasswordReg() {
        return passwordReg;
    }

    public void setPasswordReg(String passwordReg) {
        this.passwordReg = passwordReg;
    }

    public String getEmailReg() {
        return emailReg;
    }

    public void setEmailReg(String emailReg) {
        this.emailReg = emailReg;
    }

    public String getIspis() {
        return ispis;
    }

    public void setIspis(String ispis) {
        this.ispis = ispis;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String prijaviSe() {
        
      HttpSession session = SessionUtils.getSession();
			session.setAttribute("username", "Ja sam filip");
        if (this.username.isEmpty() || this.password.isEmpty()) {
            System.out.println("NIJE SVE POPUNJENO" + this.username);
            System.out.println("NIJE SVE POPUNJENO" + this.email);
            System.out.println("NIJE SVE POPUNJENO" + this.password);
        }
        String json = MeteoWSKlijent.registracijaREST(this.username);
        if (json.equals("[]")) {
            System.out.println("Ne postoji username");
        } 
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonArray array = jsonReader.readArray();
        jsonReader.close();
        System.out.println("OVOLIKO_" + array.size());
        for (JsonValue aa : array) {
            JsonReader reader = Json.createReader(new StringReader(aa.toString()));
            JsonObject jo = reader.readObject();
            int id = jo.getInt("uid");
            String username = jo.getString("username");
            String pass = jo.getString("password");
            String email = jo.getString("email");
            System.out.println(username);
            if (pass.equals(this.password)) {
                System.out.println("Ulogirani ste");  
              
              return"korisnici";
            }else{
                System.out.println("Kriva lozinka");  
              
            return "index";
            }
        }
    
    return "index";
    }

    public void registrirajSe() {
        if (this.usernameReg.isEmpty() || this.passwordReg.isEmpty() || this.emailReg.isEmpty()) {
            System.out.println("NIJE SVE POPUNJENO" + this.usernameReg);
            System.out.println("NIJE SVE POPUNJENO" + this.emailReg);
            System.out.println("NIJE SVE POPUNJENO" + this.passwordReg);

        }
        String json = MeteoWSKlijent.registracijaREST(this.usernameReg);
        System.out.println(MeteoWSKlijent.registracijaREST(this.username));
        if (json.equals("[]")) {
            JsonObjectBuilder job = Json.createObjectBuilder();
            job.add("username", this.usernameReg);
            job.add("password", this.passwordReg);
            job.add("email", this.emailReg);
            System.out.println("USPJESNOS: " + MeteoWSKlijent.registrirajREST(job.build()));
            this.regGreska = "";
        } else {
            this.regGreska = "A";
            System.out.println("Korisnicko ime vec postoji");
        }

    }
}
