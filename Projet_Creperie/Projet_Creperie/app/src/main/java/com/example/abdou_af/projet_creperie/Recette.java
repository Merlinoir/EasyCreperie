package com.example.abdou_af.projet_creperie;

/**
 * Created by Beekman on 03/11/2015.
 */
public class Recette {

    public String nomRecette;
    public String quantitéRecette;

    public Recette(String nomRecette, String quantitéRecette) {
        this.nomRecette = nomRecette;
        this.quantitéRecette = quantitéRecette;
    }

    public String getQuantitéRecette() {
        return quantitéRecette;
    }

    public String getNomRecette() {
        return nomRecette;
    }

    public void setNomRecette(String nomRecette) {
        this.nomRecette = nomRecette;
    }

    public void setQuantitéRecette(String quantitéRecette) {
        this.quantitéRecette = quantitéRecette;
    }

    @Override
    public String toString() {
        return nomRecette + " quantité : " + quantitéRecette ;
    }
}
