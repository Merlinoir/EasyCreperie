package com.example.abdou_af.projet_creperie;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abdou_af.projet_creperie.R;
import com.example.abdou_af.projet_creperie.Recette;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Beekman on 03/11/2015.
 */
public class RecetteAdapter  extends BaseAdapter {

    // Une liste de recettes
    private List<Recette> mListR;

    //Le contexte dans lequel est présent notre adapter
    private Context mContext;

    //Un mécanisme pour gérer l'affichage graphique depuis un layout XML
    private LayoutInflater mInflater;

    public RecetteAdapter(Context mContext, List<Recette> mListR) {
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.mListR = mListR;
    }

    @Override
    public int getCount() {
        return mListR.size();
    }

    @Override
    public Object getItem(int position) {
        return mListR.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layoutItem;
        //(1) : Réutilisation des layouts
        if (convertView == null) {
            //Initialisation de notre item à partir du  layout XML "recette_layout.xml"
            layoutItem = (LinearLayout) mInflater.inflate(R.layout.recette_layout, parent, false);
        } else {
            layoutItem = (LinearLayout) convertView;
        }

        //(2) : Récupération des TextView de notre layout
        TextView recette_Nom = (TextView)layoutItem.findViewById(R.id.RECETTE_Nom);
        TextView recette_Quantite = (TextView)layoutItem.findViewById(R.id.RECETTE_Quantite);

        //(3) : Renseignement des valeurs
        recette_Nom.setText(mListR.get(position).nomRecette);
        recette_Quantite.setText(" = "+mListR.get(position).quantitéRecette);

        //(4) Changement de la couleur du fond de notre item
        if (mListR.get(position).quantitéRecette.equals("0")) {
            layoutItem.setBackgroundColor(Color.RED);
        } else {
            layoutItem.setBackgroundColor(Color.GREEN);
        }


        //On mémorise la position de la "Recette" dans le composant textview
        recette_Nom.setTag(position);
        //On ajoute un listener
        recette_Nom.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Lorsque l'on clique sur le nom, on récupère la position de la "Recette"
                Integer position = (Integer)v.getTag();

                //On prévient les listeners qu'il y a eu un clic sur le TextView "recette_Nom".
                sendListener(mListR.get(position), position);

            }

        });

        //On retourne l'item créé.
        return layoutItem;
    }

    /**
     * Interface pour écouter les évènements sur le nom d'une recette
     */
    public interface RecetteAdapterListener {

        public void onClickRecette(Recette item, int position);
    }

    //Contient la liste des listeners
    private ArrayList<RecetteAdapterListener> mListListener = new ArrayList<RecetteAdapterListener>();

    /**
     * Pour ajouter un listener sur notre adapter
     */
    public void addListener(RecetteAdapterListener aListener) {
        mListListener.add(aListener);
    }

    // prévenir tous les listeners
    private void sendListener(Recette item, int position) {
        for(int i = mListListener.size()-1; i >= 0; i--) {
            mListListener.get(i).onClickRecette(item, position);
        }
    }
}
