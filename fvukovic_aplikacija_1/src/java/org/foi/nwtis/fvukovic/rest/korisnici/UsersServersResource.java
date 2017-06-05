/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.rest.korisnici;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
import static org.foi.nwtis.fvukovic.rest.ws.MeteoRESTResourceContainer.sc;

/**
 * REST Web Service
 *
 * @author filip
 */
@Path("/uss")
public class UsersServersResource {
    
      private String id;
    public static ServletContext sc;
    public Connection c;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of UsersServersResource
     */
    public UsersServersResource() {
    }

    /**
     * Retrieves representation of an instance of org.foi.nwtis.fvukovic.rest.korisnici.UsersServersResource
     * @return an instance of java.lang.String
     */
     @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        try {

            spojiNaBazu();

            String query = "Select * from korisnici";
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            int brojac = 0;
            while (rs.next()) {
                JsonObjectBuilder job = Json.createObjectBuilder();
                job.add("id", rs.getInt("id"));
                job.add("username", rs.getString("username"));
                job.add("password", rs.getString("password"));
                job.add("email", rs.getString("email"));

                jab.add(job);
            }
        } catch (SQLException ex) {
        }
        return jab.build().toString();
    }

    /**
     * POST method for creating an instance of UsersServerResource
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
   @POST
    @Path("/dodaj")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String dodajKorisnika(String content) {
        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject();
        String email = jo.getString("email");
        String naziv = jo.getString("username");
        String password = jo.getString("password");
        String query = "Select * from korisnici where username='" + naziv + "'";
        spojiNaBazu();
        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            int id = 1;
            while (rs.next()) {
                return "0";
            }
            query = "insert into korisnici values(default,'" + naziv + "','" + password + "','" + email + "')";
            s = c.createStatement();
            s.executeUpdate(query);
            return "1";
        } catch (SQLException ex) {
            return "0";
        }
        //TODO upiši uređaj u bazu 
    }
      @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
      @Path("/azuiraj")
    public String azurirajKorisnika(String content) {
        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject();
        String id = jo.getString("id");
        String email = jo.getString("email");
        String naziv = jo.getString("username");
        String password = jo.getString("password");
        String query = "Select * from korisnici where id=" + id + "";
        spojiNaBazu();
        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                query = "update korisnici set username='" + naziv + "',email='" + email + "',password='" + password + "' where id=" + id;
                s.executeUpdate(query);
                return "1";
            }

            return "0";
        } catch (SQLException ex) {
            return "0";
        }
        //TODO upiši uređaj u bazu 
    }
    

    /**
     * Sub-resource locator method for {korisnickoIme}
     */
    @GET
     @Produces(MediaType.APPLICATION_JSON)
    @Path("{korisnickoIme}")
    public String getUsersServerResource(@PathParam("korisnickoIme") String korisnickoIme) {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        try {

            spojiNaBazu();

            String query = "Select * from korisnici where username='"+korisnickoIme+"'";
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            System.out.println( "padne na upitu");
            int brojac = 0;
            while (rs.next()) {
                 
                    JsonObjectBuilder job = Json.createObjectBuilder();
                  job.add("uid", rs.getInt("id"));
                job.add("username", rs.getString("username"));
                job.add("password", rs.getString("password"));
                job.add("email", rs.getString("email"));
                    jab.add(job); 

            }
        } catch (SQLException ex) {
            return "[]";
        }
        return jab.build().toString();
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
}
