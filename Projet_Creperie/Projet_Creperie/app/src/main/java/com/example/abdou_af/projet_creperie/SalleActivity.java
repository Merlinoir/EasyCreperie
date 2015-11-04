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

    ArrayList<String> tabliste = new ArrayList<String>();
    ArrayList<String> tabliste2 = new ArrayList<String>();

    private PrintWriter writer= new PrintWriter(System.out, true);
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    ArrayAdapter adapter;

    private ReadMessages readMessages;

    private Socket socket;


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        writer.println("COMMANDE " + tabliste2.get(position));
        refreshQuantite();
        Toast toast = Toast.makeText(getApplicationContext(), ("Plats " + tabliste2.get(position)+" commandé"), Toast.LENGTH_SHORT);
        toast.show();
        recapCommande.setText(tabliste2.get(position) + "\n");
        adapter.notifyDataSetChanged();
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

                        tabliste.add(message);
                        tabliste2.add(libelle);
                        adapter.notifyDataSetChanged();
                        libelle = "";

                    }
                    //publishProgress(message);
                } catch (IOException e) {
                    System.out.println("pb writer ou reader POUR LISTE");
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... messages) {
            //displayMessage(messages[0] + "\n");
            System.out.println("Execution méthode onProgressUpdate");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salle);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tabliste);

        listeview = (ListView) findViewById(R.id.listView);
        recapCommande = (TextView) findViewById(R.id.recapCommande);

        listeview.setOnItemClickListener(this);

        System.out.println("Dans create");
    }


    @Override
    protected void onStart() {
        super.onStart();
        new StartNetwork().execute();
        System.out.println("Dans onstart");
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

    public void quantite(View v) {
        tabliste.clear();
        writer.println("QUANTITE");
        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tabliste);
        listeview.setAdapter(adapter);
        System.out.println("Appel méthode askForQuantite");
    }

    public void refreshQuantite(){
        tabliste.clear();
        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, tabliste);
        listeview.setAdapter(adapter);
        System.out.println("Appel méthode refreshQuantite");
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
}
