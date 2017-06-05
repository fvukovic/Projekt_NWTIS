/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.master;

import javax.jws.WebService;

/**
 *
 * @author filip
 */
@WebService(serviceName = "IoT_Master", portName = "IoT_MasterPort", endpointInterface = "org.foi.nwtis.dkermek.ws.serveri.IoTMaster", targetNamespace = "http://serveri.ws.dkermek.nwtis.foi.org/", wsdlLocation = "WEB-INF/wsdl/IoTMaster/nwtis.foi.hr_8080/DZ3_Master/IoT_Master.wsdl")
public class IoTMaster {

    public java.lang.Boolean registrirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.lang.Boolean deregistrirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.lang.Boolean blokirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.lang.Boolean aktivirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public org.foi.nwtis.dkermek.ws.serveri.StatusKorisnika dajStatusGrupeIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.lang.Boolean dodajUredjajGrupi(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, org.foi.nwtis.dkermek.ws.serveri.Uredjaj iotUredjaj) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean obrisiUredjajGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean aktivirajOdabraneUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.util.List<java.lang.Integer> odabraniUredjaji) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean blokirajOdabraneUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.util.List<java.lang.Integer> odabraniUredjaji) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean obrisiOdabraneUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, java.util.List<java.lang.Integer> odabraniUredjaji) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.lang.Boolean autenticirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.lang.Boolean dodajNoviUredjajGrupi(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj, java.lang.String nazivUredjaj, java.lang.String adresaUredjaj) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.util.List<org.foi.nwtis.dkermek.ws.serveri.Uredjaj> dajSveUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public java.lang.Boolean obrisiSveUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean ucitajSveUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public org.foi.nwtis.dkermek.ws.serveri.StatusUredjaja dajStatusUredjajaGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean blokirajUredjajGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public boolean aktivirajUredjajGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }
    
}
