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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class SalleActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listeview;
    private TextView recapCommande;
    private TextView titreRecapCommande;

    private PrintWriter writer= new PrintWriter(System.out, true);
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    ArrayAdapter adapter;

    private Socket socket;

    private ReadMessages readMessages;

    ArrayList<String> tabliste = new ArrayList<String>();
    private final static String SAVE_TABLISTE = "SAVE_TABLISTE";

    ArrayList<String> tabliste2 = new ArrayList<String>();
    private final static String SAVE_TABLISTE2 = "SAVE_TABLISTE2";

    String textRecapCommande = "";
    private final static String TEXT_RECAP_COMMANDE = "TEXT_RECAP_COMMANDE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Dans create");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salle);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tabliste);

        listeview = (ListView) findViewById(R.id.listView);
        recapCommande = (TextView) findViewById(R.id.recapCommande);
        titreRecapCommande = (TextView) findViewById(R.id.titreRecapCommande);
        titreRecapCommande.setVisibility(View.INVISIBLE);

        listeview.setOnItemClickListener(this);

        if(savedInstanceState != null){

            tabliste = savedInstanceState.getStringArrayList(SAVE_TABLISTE);
            tabliste2 = savedInstanceState.getStringArrayList(SAVE_TABLISTE2);
            tabliste.clear();
            ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tabliste);
            listeview.setAdapter(adapter);

            textRecapCommande = savedInstanceState.getString(TEXT_RECAP_COMMANDE);
            System.out.println("Valeur de textRecapCommande aprés rotation = " + textRecapCommande );
            if(!(textRecapCommande=="")){
                titreRecapCommande.setVisibility(View.VISIBLE);
                recapCommande.setText(textRecapCommande);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        System.out.println("Dans onSaveInstanceState");

        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList(SAVE_TABLISTE, tabliste);
        savedInstanceState.putStringArrayList(SAVE_TABLISTE2,tabliste2);
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

    public void refreshQuantite(){
        System.out.println("Appel méthode refreshQuantite");
        writer.println("QUANTITE");
        tabliste.clear();
        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tabliste);
        listeview.setAdapter(adapter);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        writer.println("COMMANDE " + tabliste2.get(position));
        refreshQuantite();
        Toast toast = Toast.makeText(getApplicationContext(), ("Plats " + tabliste2.get(position)+" commandé"), Toast.LENGTH_SHORT);
        toast.show();

        textRecapCommande += (tabliste2.get(position)+ "\n") ;
        titreRecapCommande.setVisibility(View.VISIBLE);
        recapCommande.setText(textRecapCommande);

    }

    //------------------------ Methode appelée sur appui boutton------------------------------------
    public void quantite(View v) {
        System.out.println("Appel méthode askForQuantite");

        tabliste.clear();
        writer.println("QUANTITE");
        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tabliste);
        listeview.setAdapter(adapter);
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
            } else {
                //displayMessage("Could not connect to server\n");
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
                            tabliste.add(message);
                            tabliste2.add(libelle);
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
