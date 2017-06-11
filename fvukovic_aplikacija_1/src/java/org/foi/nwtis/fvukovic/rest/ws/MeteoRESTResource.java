/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.rest.ws;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.fvukovic.dretve.Baza;
import org.foi.nwtis.fvukovic.konfiguracije.bp.BP_Konfiguracija;
import static org.foi.nwtis.fvukovic.rest.ws.MeteoRESTResourceContainer.sc;
import org.foi.nwtis.fvukovic.web.podaci.Lokacija;
import org.foi.nwtis.fvukovic.web.podaci.MeteoPodaci;
import org.foi.nwtis.fvukovic.web.podaci.Uredjaj; 

/**
 * REST Web Service
 *
 * @author grupa_3
 */
public class MeteoRESTResource {

    private String id;
    public static ServletContext sc; 

    /**
     * Creates a new instance of MeteoRESTResource
     */
    private MeteoRESTResource(String id) {
        this.id = id;
    }

    /**
     * Get instance of the MeteoRESTResource
     */
    public static MeteoRESTResource getInstance(String id) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of MeteoRESTResource class.
        return new MeteoRESTResource(id);
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.matnovak.rest.serveri.MeteoRESTResource
     * GET metoda koja sluzi za dohvat svih meteo podataka prema id uredaja
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
         JsonArrayBuilder jab = Json.createArrayBuilder();
        JsonObjectBuilder job = Json.createObjectBuilder();
        ArrayList<Uredjaj> uredjaji = new ArrayList<>();
        String query = "Select * from meteo where id = " + id ; 
        MeteoPodaci mp = null;
        try {
            Statement s = Baza.c.createStatement();
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {

                job.add("vlaga", rs.getString("vlaga"));
                job.add("temp", rs.getString("temp"));
                job.add("tlak", rs.getString("tlak"));
                jab.add(job);
            }

        } catch (SQLException ex) {
        
        }

        return jab.build().toString();
    }

    /**
     * PUT method for updating or creating an instance of MeteoRESTResource
     *
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource MeteoRESTResource
     */
    @DELETE
    public void delete() {
    }
    /**
     * Metoda za povezivanje na bazu prema konfiguraciji
     */
    
}
