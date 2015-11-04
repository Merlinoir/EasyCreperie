package com.example.abdou_af.projet_creperie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class CuisineActivity extends AppCompatActivity  implements RecetteAdapter.RecetteAdapterListener {

    private ListView listeview;
    private Button cmdButton;
    private EditText nomRecette;
    private EditText quantiteRecette;

    ArrayList<Recette> tabRecettes = new ArrayList<>();

    private PrintWriter writer= new PrintWriter(System.out, true);
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private ReadMessages readMessages;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine);

        listeview = (ListView) findViewById(R.id.listView);
        cmdButton = (Button) findViewById(R.id.cmd_liste);

        nomRecette = (EditText) findViewById(R.id.editTextNom);
        quantiteRecette = (EditText) findViewById(R.id.editTextQuantite);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cuisine, menu);
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

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//    }

    @Override
    public void onClickRecette(final Recette item, int position) {
        //On instancie notre layout en tant que View
        LayoutInflater factory = LayoutInflater.from(this);
        final View alertDialogView = factory.inflate(R.layout.alertdialog_quantite, null);

        //Création de l'AlertDialog
        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        //On affecte la vue personnalisé que l'on a crée à notre AlertDialog
        adb.setView(alertDialogView);

        //On donne un titre à l'AlertDialog
        adb.setTitle("Réapprovisionner le plat : " + item.getNomRecette() + " ?" + "\n" + "Veuillez saisir la quantité !");

        //On modifie l'icône de l'AlertDialog pour le fun ;)
        adb.setIcon(android.R.drawable.ic_dialog_alert);

        //On affecte un bouton "OK" à notre AlertDialog et on lui affecte un évènement
        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //Lorsque l'on cliquera sur le bouton "OK", on récupère l'EditText correspondant à notre vue personnalisée (cad à alertDialogView)
                EditText quantite = (EditText) alertDialogView.findViewById(R.id.EditText1);

                //j'ajoute la quantité saisie au stock des recettes
                askForAjout(item.getNomRecette(), quantite.getText().toString());

                //On affiche dans un Toast le texte contenu dans l'EditText de notre AlertDialog
                String toastMessage = item.getNomRecette() + " réapprovisionné de "+ quantite.getText()+" !";
                Toast.makeText(CuisineActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });

        //On crée un bouton "Annuler" à notre AlertDialog et on fait rien si Annuler
        adb.setNegativeButton("Annuler",null);
        adb.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new StartNetwork().execute();
    }

    public void quantite(View v) {
        askForQuantite();
    }

    public void create(View view) {
        askForAjout(nomRecette.getText().toString(), quantiteRecette.getText().toString());
        cmdButton.performClick();
    }

    public void askForQuantite(){
        tabRecettes.clear();
        writer.println("QUANTITE");
        RecetteAdapter adapter = new RecetteAdapter(this, tabRecettes);
        //Ecoute des évènements sur votre liste
        adapter.addListener(this);
        listeview.setAdapter(adapter);
    }

    public void askForAjout(String nom, String quantite){
        writer.println("AJOUT " + quantite + " " + nom);
        cmdButton.performClick();
    }

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
        //finish();
    }

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
            if (b) {
                //displayMessage("Connected to server\n");
                readMessages = new ReadMessages();
                readMessages.execute();
            } else {
                System.out.println("Could not connect to server");
            }
        }
    }

    private class ReadMessages extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... v) {
            while (!isCancelled()) {
                parseQuantiteResult();
            }
            return null;
        }

        public void parseQuantiteResult(){
            try {
                String message = reader.readLine();
                if(!(message.equals("FINLISTE"))&& !(message.contains("de chacun des plats")) && !(message.contains("Plat")) ){

                    String libellePlat = message;
                    String quantitePlat = reader.readLine();

                    tabRecettes.add(new Recette(libellePlat, quantitePlat));
                }
            } catch (IOException e) {
                System.out.println("pb writer ou reader POUR LISTE");
            }
        }

        @Override
        protected void onProgressUpdate(String... messages) {
            System.out.println("Execution méthode onProgressUpdate");
        }
    }
}
