package com.example.abdou_af.projet_creperie;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class SalleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView affListePlatDispo;
    private TextView recapCommande;
    private TextView titreRecapCommande;

    private PrintWriter writer= new PrintWriter(System.out, true);
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    ArrayAdapter adapter;  //

    private Socket socket;

    private ReadMessages readMessages;

    ArrayList<String> tabLibPlatQuantite = new ArrayList<String>();
    private final static String SAVE_TABLISTE = "SAVE_TABLISTE";

    ArrayList<String> tabLibPlats = new ArrayList<String>();
    private final static String SAVE_TABLISTE2 = "SAVE_TABLISTE2";

    String textRecapCommande = "";
    private final static String TEXT_RECAP_COMMANDE = "TEXT_RECAP_COMMANDE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Dans create");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salle);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tabLibPlatQuantite);

        affListePlatDispo = (ListView) findViewById(R.id.listView);
        recapCommande = (TextView) findViewById(R.id.recapCommande);
        titreRecapCommande = (TextView) findViewById(R.id.titreRecapCommande);
        titreRecapCommande.setVisibility(View.INVISIBLE);

        // affectation listener pour chaque item de la Listview
        affListePlatDispo.setOnItemClickListener(this);

        //persistance des données (lors rotation tablette) Maj variables et affichage avec données sauvegardées
        if(savedInstanceState != null){

            // Maj zone commande
            tabLibPlatQuantite = savedInstanceState.getStringArrayList(SAVE_TABLISTE);
            tabLibPlats = savedInstanceState.getStringArrayList(SAVE_TABLISTE2);
            tabLibPlatQuantite.clear();
            ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tabLibPlatQuantite);
            affListePlatDispo.setAdapter(adapter);

            // Maj zone Recap Commande
            textRecapCommande = savedInstanceState.getString(TEXT_RECAP_COMMANDE);
            System.out.println("Valeur de textRecapCommande aprés rotation = " + textRecapCommande );
            if(!(textRecapCommande=="")){
                titreRecapCommande.setVisibility(View.VISIBLE);
                recapCommande.setText(textRecapCommande);
            }
        }
    }

    // sauvegarde données dans bundle (persistance) lors rotation tablette
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        System.out.println("Dans onSaveInstanceState");

        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList(SAVE_TABLISTE, tabLibPlatQuantite);
        savedInstanceState.putStringArrayList(SAVE_TABLISTE2,tabLibPlats);
        savedInstanceState.putString(TEXT_RECAP_COMMANDE, textRecapCommande);
    }


    @Override
    protected void onStart() {
        System.out.println("Dans onstart");

        super.onStart();
        new StartNetwork().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("SalleActivity.onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("SalleActivity.onRestart");
    }

    @Override
    protected void onPause() {
        System.out.println("SalleActivity.onPause");
        super.onPause();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        System.out.println("SalleActivity.onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        System.out.println("SalleActivity.onDestroy");
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_salle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Maj liste plat dipo dans listView (zone commande)
    public void refreshQuantite(){
        System.out.println("Appel méthode refreshQuantite");
        writer.println("QUANTITE");
        tabLibPlatQuantite.clear();
        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tabLibPlatQuantite);
        affListePlatDispo.setAdapter(adapter);
    }

    // action sur click d'un item de la listview (zone commande)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        writer.println("COMMANDE " + tabLibPlats.get(position));
        refreshQuantite();
        Toast toast = Toast.makeText(getApplicationContext(), ("Plats " + tabLibPlats.get(position)+" commandé"), Toast.LENGTH_SHORT);
        toast.show(); // dans version 2 sera dans ReadMessages et sur reception message serveur commande ok

        textRecapCommande += (tabLibPlats.get(position)+ "\n") ;
        titreRecapCommande.setVisibility(View.VISIBLE);
        recapCommande.setText(textRecapCommande);

    }

    //------------------------ Methode appelée sur appui boutton------------------------------------

    // Maj listview plats dispo (zone commande) par appui bp
    public void quantite(View v) {
        System.out.println("Appel méthode quantite");

        tabLibPlatQuantite.clear();
        writer.println("QUANTITE");
        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tabLibPlatQuantite);
        affListePlatDispo.setAdapter(adapter);
    }

    // deconnexion du serveur et retour sur page acceuil
    public void logout(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        writer.println("LOGOUT");
        readMessages.cancel(true);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivity(intent);
        finish();
    }

    //----------------------------------- threads secondaires----------------------------------------------

    private class StartNetwork extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            System.out.println("StartNetwork.onPreExecute");
        }

        @Override
        protected Boolean doInBackground(Void... v) {
            System.out.println("StartNetwork.doInBackground");
            try {
                socket = new Socket("10.0.2.2", 7777);
                writer = new PrintWriter(socket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("connexion au serveur ok");
                return true;
            } catch (IOException e) {
                System.out.println("pb connexion au serveur");
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean b) {
            System.out.println("StartNetwork.onPostExecute");
            if (b) {
                //displayMessage("Connected to server\n");
                readMessages = new ReadMessages();
                readMessages.execute();
                writer.println("QUANTITE");
            } 
        }
    }

    private class ReadMessages extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... v) {
            while (!isCancelled()) {
                String message = "";

                try {
                    message = reader.readLine();
                    if(!(message.equals("FINLISTE"))&& !(message.contains("de chacun des plats")) && !(message.contains("Plat")) ){

                        String libelle = message;
                        message = libelle +"    quantité: " + reader.readLine();

                        if(!(message.contains("quantité: 0"))){
                            tabLibPlatQuantite.add(message);
                            tabLibPlats.add(libelle);
                        }
                        libelle = "";
                    }
                } catch (IOException e) {
                    System.out.println("pb writer ou reader POUR LISTE");
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... messages) {
            System.out.println("Execution méthode onProgressUpdate");
        }
    }
}
