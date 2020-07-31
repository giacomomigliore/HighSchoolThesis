package com.example.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.app.Fragment.FragmentGestioneCorsa;

/**
 * Il compito di questa classe è fare l'upload dei dati relativi alla vendita
 * di una nuova tratta all'utente il cui cellulare è scarico e quindi non
 * può ottenerla tramite nfc. Egli deve conoscere il suo username e la sua
 * password. Si vende solo la corsa base che quindi dura 90 minuti. 
 */
public class UploadNuovaCorsa extends AsyncTask<Void, Void, String> {
	// Variabile che conterra l'activity chiamante.
	private Context activity;
	private Application application;
	
	// Dichiaro tutte le variabili che servono per creare una nuova corsa.
	private String username;
	private String password;
	// Dichiaro una variabile per vedere se eseguire o meno il ProgressDialog.
	private Boolean dialog=false;
	
    public UploadNuovaCorsa(Context activity, String username, String pwd, String codLinea, String durataConvalida, Boolean dialog){
    	this.activity=activity;
    	this.username=username;
    	this.password=pwd;
    	this.dialog=dialog;
    }
	
	private ProgressDialog pDialog;
    private String NUOVA_CORSA ;

    // Tag usato nei log.
    private final String TAG = UploadNuovaCorsa.class.getSimpleName();



    // Tag(ID) del responso Json restituito dallo script php:

    /**
     * Metodo che viene eseguito prima di doInBackground.
     * Avvio il ProgressDialog.
     */
    @SuppressWarnings("static-access")
	@Override
    protected void onPreExecute() {
	    super.onPreExecute();
	    if(dialog){
	    	pDialog = new ProgressDialog(activity);
	        pDialog.setMessage("Caricamento viaggio...");
	        // Dura un tempo indeterminato.
	        pDialog.setIndeterminate(false);
	        // E' cancellabile dal tasto Back.
	        pDialog.setCancelable(true);
	        pDialog.show();
	    }
        

        // Aggiorno NUOVA_CORSA.
        GlobalClass gc= (GlobalClass) application;
        NUOVA_CORSA = gc.getDominio() + "Upload/caricaCorsa.php";
    }

    /**
     * Questo metodo esegue l'upload.
     */
    @Override
    protected String doInBackground(Void... voids) {

        JSONParser jsonParser = new JSONParser();
        String user = username;
        String pass = password;
        String codlinea = FragmentGestioneCorsa.getLineaSelezionata();
        String durataconvalida = "90";
        try {

            // Creazione dei parametri.
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", user));
            params.add(new BasicNameValuePair("password", pass));
            params.add(new BasicNameValuePair("codlinea", codlinea));
            params.add(new BasicNameValuePair("durataconvalida", durataconvalida));

            // Richiesta della pagina .php.
            JSONObject json = jsonParser.makeHttpRequest(
                    NUOVA_CORSA, "POST", params);

            return json.getString(GlobalClass.getTagMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Error " + e.toString());
            return "Errore nel caricamento della nuova corsa";
        }
    }


    /**
     * Metodo eseguito dopo doInBackground.
     * @param response Json response.
     */
    @Override
    protected void onPostExecute(String response) {
        super.onPostExecute(response);

    	// Chiudo il ProgressDialog.
        if(dialog) pDialog.dismiss();
        
        // Aggiorno l'utente circa il risultato.
        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
    }
}
