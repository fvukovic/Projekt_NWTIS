/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.rest.korisnici;

import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author filip
 */
public class UsersServerResource {

    private String korisnickoIme;

    /**
     * Creates a new instance of UsersServerResource
     */
    private UsersServerResource(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    /**
     * Get instance of the UsersServerResource
     */
    public static UsersServerResource getInstance(String korisnickoIme) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of UsersServerResource class.
        return new UsersServerResource(korisnickoIme);
    }

    /**
     * Retrieves representation of an instance of org.foi.nwtis.fvukovic.rest.korisnici.UsersServerResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of UsersServerResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

    /**
     * DELETE method for resource UsersServerResource
     */
    @DELETE
    public void delete() {
    }
}
