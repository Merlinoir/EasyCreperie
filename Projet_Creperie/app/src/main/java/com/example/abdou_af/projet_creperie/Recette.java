package com.example.abdou_af.projet_creperie;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Beekman on 03/11/2015.
 */
public class Recette implements Parcelable {

    public String nomRecette;
    public String quantitéRecette;

    public Recette(String nomRecette, String quantitéRecette) {
        this.nomRecette = nomRecette;
        this.quantitéRecette = quantitéRecette;
    }

    private Recette(Parcel in) {
        String[] data = new String[2];

        in.readStringArray(data);
        this.nomRecette = data[0];
        this.quantitéRecette = data[1];
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.nomRecette, this.quantitéRecette});
    }

    public static final Parcelable.Creator<Recette> CREATOR = new Parcelable.Creator<Recette>() {
        public Recette createFromParcel(Parcel in) {
            return new Recette(in);
        }

        public Recette[] newArray(int size) {
            return new Recette[size];
        }
    };


}
