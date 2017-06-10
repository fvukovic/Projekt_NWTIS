/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.fvukovic.web.podaci;
 
/**
 * 
 * @author filip
 * Entiteska klasa za sve jezike koje nam omogucava nasa aplikacija
 */
public class Izbornik {
    private String labela;
    private String vrijednost;
    private int broj;   

    /**
     * Konstruktor za kreiranje Novog izbornika, gdje lable predstavlja ono sta ce biti ispisano na aplikaciji, vrijdnost jedinstveni identifikator za svaki jezik
     * @param labela
     * @param vrijednost 
     */
    public Izbornik(String labela, String vrijednost) {
        this.labela = labela;
        this.vrijednost = vrijednost;
    }

    public int getBroj() {
        return broj;
    }

    public void setBroj(int broj) {
        this.broj = broj;
    }
    
    public String getLabela() {
        return labela;
    }

    public void setLabela(String labela) {
        this.labela = labela;
    }

    public String getVrijednost() {
        return vrijednost;
    }

    public void setVrijednost(String vrijednost) {
        this.vrijednost = vrijednost;
    }
    
    
}
