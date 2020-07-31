package fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import tesina.gestionelinea.BottoneCliccato;
import tesina.gestionelinea.DatabaseLocale;
import tesina.gestionelinea.GlobalClass;
import tesina.gestionelinea.JSONParser;
import tesina.gestionelinea.R;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Lo scopo di questo fragment è quello di aggiungere una fermata
 * ai databases(locale e sul server). Chiede solo il nome della 
 * fermata perchè il codice è autoincrement.
 */
public class FragmentAggiungiFermata extends Fragment {

    private Button btnConfermaAddFermata = null;
    private EditText edtConfermaAddFermata = null;
    private Context context = null;

    // Tag usato nei log.
    private static final String TAG = FragmentAggiungiFermata.class.getSimpleName();

    /**
     * Primo metodo che viene chiamato quando creo il fragment. Qui associo
     * il layout al fragment.
     * @param inflater Il layout da utilizzare in questo fragment.
     * @param container Il layout che contiene il fragment.
     * @param savedInstanceState Se si creasse un punto di ripristino dell'activity quando si chiude.
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inserisco il layout di questo fragment.
        return inflater.inflate(R.layout.fragment_aggiungi_fermata, container, false);
    }

    /**
     * Metodo chiamato appena dopo che è stato istanziato il layout. Ottengo gli oggetti in esso presenti.
     * @param view Tutte le view nel layout.
     * @param savedInstanceState Se si creasse un punto di ripristino dell'activity quando si chiude.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ottengo gli elementi dall'.xml e il context.
        edtConfermaAddFermata = (EditText) getView().findViewById(R.id.edtConfermaAddFermata);
        btnConfermaAddFermata = (Button) getView().findViewById(R.id.btnConfermaAddFermata);
        context = getActivity();
        
        btnConfermaAddFermata.setOnTouchListener( new BottoneCliccato( btnConfermaAddFermata.getAlpha() ));
	    btnConfermaAddFermata.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Controllo che la connessione sia attiva.
                GlobalClass gc= (GlobalClass)getActivity().getApplication();
                if(gc.isOnline())
                	if(edtConfermaAddFermata.getText().toString().length() < 31)
                    	if(edtConfermaAddFermata.getText().toString().length() >0)
	                        // Avvio UploadFermata.
	                        new UploadFermata().execute();  
                    	else  Toast.makeText(context, "Completare il nome della fermata.", Toast.LENGTH_LONG).show();
                	else Toast.makeText(context, "La lunghezza massima del nome è di 30 caratteri.", Toast.LENGTH_LONG).show();
                else Toast.makeText(context, getString(R.string.controlloConessione), Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * Il compito di questa classe è fare l'upload di una nuova fermata
     * da aggiungere ai databases locale e sul server. Richiede solo il
     * nome della fermata perchè il codice è autoincrement. Per fare in
     * modo che il codice del database sul server sia uguale al codice
     * del database locale senza dover scaricare il record appena caricato,
     * si fa restituire dallo script php l'id dell'ultimo record inserito.
     * Questo dato è contenuto nel TAG_SUCCESS del json, infatti se un
     * inserimento non va a buon fine il valore del TAG_SUCCESS sarà 0,
     * valore che non può essere assunto da un autoincrement.
     */
    class UploadFermata extends AsyncTask<Void, Void, String> {

        private ProgressDialog pDialog = null;

        // Tag usato nei log.
        private final String TAG = UploadFermata.class.getSimpleName();

        // URL dello script php.
        private String NUOVA_FERMATA = GlobalClass.getDominio() + "Upload/caricaFermata.php";
        
        JSONObject json = null;

        /**
         * Metodo che viene eseguito prima di doInBackground.
         * Avvio il ProgressDialog.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            // Imposto e mostro il ProgressDialog.
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Caricamento fermata...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Questo metodo esegue l'upload.
         * @param voids
         * @return
         */
        @Override
        protected String doInBackground(Void... voids) {
        	        
            JSONParser jsonParser = new JSONParser();
            try {
                // Creazione dei parametri.
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("NomeFermata", edtConfermaAddFermata.getText().toString()));

                // Richiesta della pagina .php.
                json = jsonParser.makeHttpRequest(
                        NUOVA_FERMATA, "POST", params);
                
                return json.getString(GlobalClass.getTagMessage());
            } catch (JSONException e) {
                Toast.makeText(context, "Errore nell'upload della fermata.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error " + e.toString());
                return null;
            }
        }


        /**
         * Metodo eseguito dopo doInBackground.
         * @param valoreRitornato Json response.
         */
        @Override
        protected void onPostExecute(String valoreRitornato) {
            super.onPostExecute(valoreRitornato);

            // Chiudo il ProgressDialog.
            pDialog.dismiss();
            
            // Se l'upload non ha avuto errori.
            if(valoreRitornato != null){
            	
	            // Aggiorno il database locale.
	            try {
	
	            	// Se la linea è stata aggiunta con successo il
	            	// valore del TAG_SUCCESS è l'id della fermata.
	                if(json.getInt(GlobalClass.getTagSuccess()) != 0){
	                	DatabaseLocale db = new DatabaseLocale(context);
	                    SQLiteDatabase dbScrivibile = db.getWritableDatabase();
	                    
	                    ContentValues valori = new ContentValues();
	                    valori.put(DatabaseLocale.getTagCodiceFermata(), json.getInt(GlobalClass.getTagSuccess()));
	                    valori.put(DatabaseLocale.getTagNomeFermata(), edtConfermaAddFermata.getText().toString());
	                    dbScrivibile.insert(DatabaseLocale.getTableNameFermata(), null, valori);
	                    
	                    dbScrivibile.close();
	                    db.close();
	                }
	            } catch (JSONException e) {
		            Toast.makeText(context, "Errore nell'inserimento della fermata nel database locale", Toast.LENGTH_LONG).show();
	                Log.e(TAG, "Error " + e.toString());
	            }
	
	            // Aggiorno l'utente circa il risultato.
	            Toast.makeText(context, valoreRitornato, Toast.LENGTH_LONG).show();
	            Log.d(TAG, valoreRitornato);
	
	            // Svuoto la EditText.
	            edtConfermaAddFermata.setText("");
            }
        }
    }

}
