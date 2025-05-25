package com.example.clientconsdb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ClientConnecte {

    @Id
    private String nom;
    private boolean actif;

    public ClientConnecte() {}

    public ClientConnecte(String nom, boolean actif) {
        this.nom = nom;
        this.actif = actif;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }
}
