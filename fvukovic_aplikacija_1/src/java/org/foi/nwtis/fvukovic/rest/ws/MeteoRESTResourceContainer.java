/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.rest.ws;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.foi.nwtis.fvukovic.rest.klijenti.GMKlijent;
import org.foi.nwtis.fvukovic.web.podaci.Lokacija;
import org.foi.nwtis.fvukovic.web.podaci.Uredjaj;
import static org.foi.nwtis.fvukovic.ws.GeoMeteoWS.sc;

/**
 * REST Web Service
 *
 * @author grupa_3
 */
@Path("/meteoREST")
public class MeteoRESTResourceContainer {

    public static ServletContext sc;
    public Connection c;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of MeteoRESTResourceContainer
     */
    public MeteoRESTResourceContainer() {
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.matnovak.rest.serveri.MeteoRESTResourceContainer GET metoda
     * za dohvat svih uredaja u bazi
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        try {

            spojiNaBazu();

            String query = "Select * from uredaji";
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            int brojac = 0;
            while (rs.next()) {
                JsonObjectBuilder job = Json.createObjectBuilder();
                job.add("uid", rs.getInt("id"));
                job.add("naziv", rs.getString("naziv"));
                job.add("lat", rs.getString("latitude"));
                job.add("lon", rs.getString("longitude"));

                jab.add(job);
            }
        } catch (SQLException ex) {
        }
        return jab.build().toString();
    }

    /**
     * POST method for creating an instance of MeteoRESTResource POST metoda za
     * upis uredaja u bazu prema jsonu koji je parametar, u jsonu su naziv
     * uredaja i adresa
     *
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
    /**
     * Sub-resource locator method for {id}
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMeteoRESTResource(@PathParam("id") String id) {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        try {

            spojiNaBazu();

            String query = "Select * from uredaji where id=" + id;
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            System.out.println("padne na upitu");
            int brojac = 0;
            while (rs.next()) {

                JsonObjectBuilder job = Json.createObjectBuilder();
                job.add("uid", rs.getInt("id"));
                job.add("naziv", rs.getString("naziv"));
                job.add("lat", rs.getString("latitude"));
                job.add("lon", rs.getString("longitude"));
                jab.add(job);

            }
        } catch (SQLException ex) {
            return "[33]";
        }
        return jab.build().toString();
    }

    @POST
    @Path("/dodaj")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String dodajUredaj(String content) {
        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject(); 
        String naziv = jo.getString("naziv");
        String lat = jo.getString("lat");
        int id=0;
        String lon = jo.getString("lon");
          String status = jo.getString("status");
         
        try {
             spojiNaBazu();
               String query = "SELECT id, MAX(id) FROM uredaji GROUP BY id desc limit 1";
          Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query); 
            while (rs.next()) {
                id=rs.getInt("id");
            }
          
          query = "Select * from uredaji where naziv='"+naziv+"'";
       
            
             s = c.createStatement();
             rs = s.executeQuery(query); 
            while (rs.next()) {
                return "0";
            }
            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();
                String strDate = sdfDate.format(now);
                id++;
            query = "insert into uredaji values("+id+",'" + naziv + "','" + lat + "','" + lon + "',"+status+", vrijeme_promjene='"+strDate+"',vrijeme_kreiranja='"+strDate+"')";
            s = c.createStatement();
            s.executeUpdate(query);
            return "1";
        } catch (SQLException ex) {
            System.out.println(ex);
            return "0";
        }
        //TODO upiši uređaj u bazu 
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/azuiraj")
    public String azurirajUredaj(String content) {
        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject();
        String id = jo.getString("id");
        String naziv = jo.getString("naziv");
        String lat = jo.getString("lat");
        String lon = jo.getString("lon");
        String query = "Select * from korisnici where id=" + id + "";
        spojiNaBazu();
        try {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date now = new Date();
                String strDate = sdfDate.format(now);
                query = "update uredaji set naziv='" + naziv + "',latitude='" + lat + "',longitude='" + lon + "' where id=" + id;
                s.executeUpdate(query);
                return "1";
            }

            return "0";
        } catch (SQLException ex) {
            return "0";
        }
        //TODO upiši uređaj u bazu 
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
