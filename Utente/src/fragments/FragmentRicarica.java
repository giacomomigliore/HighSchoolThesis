package fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import utente.BottoneCliccato;
import utente.JSONParser;

import com.utente.R;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * La classe FragmentRicarica è un Fragment che ha per layout il file
 * fragment_ricarica.xml. Qui l'utente può comprare nuove corse o
 * abbonamenti.
 * --
 * FragmentRicarica class is a Fragment that has as layout the file
 * fragment_ricarica.xml. Here the user can buy new routes or pass.
 */
public class FragmentRicarica extends Fragment{
	
	//dichiaro la variabile per contenere il codice abbonamento
	private String codiceAbb=null;
	
	//file delle preferenze
	public static final String PREFS_NAME = "MyPrefsFile";
	
	// Dichiaro i radio button.
	// Declare RadioButton.
	private RadioButton rdb1Corsa=null;
	private RadioButton rdb10Corse=null;
	private RadioButton rdb3Giorni=null;
	private RadioButton rdb7Giorni=null;
	private RadioButton rdb1Mese=null;
	private RadioButton rdb1Anno=null;
	
	//dichiaro il pulsante
	private Button btnCompra=null;
	
	//dichiaro la variabile per contenere l'username
	private String username=null;

    /**
     * Primo metodo che viene chiamato quando creo il fragment. Qui associo
     * il layout al fragment.
     * --
     * First method called when I create this fragment. Here I match the
     * layout to the fragment.
     * @param inflater Il layout da utilizzare in questo fragment.
     *                 The layout used in this fragment.
     * @param container Il layout che contiene il fragment.
     *                  The layout that contains the fragment.
     * @param savedInstanceState Se si creasse un punto di ripristino dell'activity quando si chiude.
     *                           If there has been created a reset point of the activity when it's closed.
     * @return View
     */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return (LinearLayout) inflater.inflate(R.layout.fragment_ricarica, container, false);
	}

    /**
     * Metodo chiamato appena dopo che è stato istanziato il layout. Ottengo gli oggetti in esso presenti.
     * Method called just after the layout has been instantiated. I get the object that are there.
     * @param view Tutte le view nel layout.
     *             All the views that are in the layout.
     * @param savedInstanceState Se si creasse un punto di ripristino dell'activity quando si chiude.
     *                           If there has been created a reset point of the activity when it's closed.
     */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		//blocco rotazione
		this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//inizializzo i radio button
		rdb1Corsa= (RadioButton)getActivity().findViewById(R.id.rdb1Corsa);
		rdb10Corse= (RadioButton)getActivity().findViewById(R.id.rdb10Corse);
		rdb3Giorni= (RadioButton)getActivity().findViewById(R.id.rdb3giorni);
		rdb7Giorni= (RadioButton)getActivity().findViewById(R.id.rdb7Giorni);
		rdb1Mese= (RadioButton)getActivity().findViewById(R.id.rdbMese);
		rdb1Anno= (RadioButton)getActivity().findViewById(R.id.rdbAnno);
		
		//inizializzo il bottone Compra
		btnCompra=(Button)getActivity().findViewById(R.id.btnCompra);
		
		// Seleziono un radio button per default.
		// Select a RadioButton as default.
		rdb1Corsa.setChecked(true);
		super.onViewCreated(view, savedInstanceState);
		
		//leggo l'username dal file delle preferenze
		// Restore preferences
		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		//leggo dal file delle preferenze e assegno il suo valore alla variabile booleana creata apposta
		username = settings.getString("username", null); 
		
		btnCompra.setOnTouchListener( new BottoneCliccato( btnCompra.getAlpha() ));
		//assegno il listener al pulsante Carica
		btnCompra.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(rdb1Corsa.isChecked()){
					// Avvio l'AsyncTask.
                    // Start the AsyncTask.
					//new UploadBiglietti(username, "1").execute();
					
					codiceAbb="1";                   
				}else if(rdb10Corse.isChecked()){
					// Avvio l'AsyncTask.
                    // Start the AsyncTask.
                    //new UploadBiglietti(username, "10").execute();
					codiceAbb="10";  
				}else if(rdb3Giorni.isChecked()){
					// Avvio l'AsyncTask.
                    // Start the AsyncTask.
                    //new UploadBiglietti(username, "003").execute();
					codiceAbb="003";  
				}else if(rdb7Giorni.isChecked()){
					// Avvio l'AsyncTask.
                    // Start the AsyncTask.
                    //new UploadBiglietti(username, "007").execute();
					codiceAbb="007";  
				}else if(rdb1Mese.isChecked()){
					// Avvio l'AsyncTask.
                    // Start the AsyncTask.
                    //new UploadBiglietti(username, "030").execute();
					codiceAbb="030";  
				}else if(rdb1Anno.isChecked()){
					// Avvio l'AsyncTask.
                    // Start the AsyncTask.
                    //new UploadBiglietti(username, "365").execute();
					codiceAbb="365";  
				}
				
				//faccio comparire la finestra di conferma dell'acquisto 
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				            //Yes button clicked
				        	if(isNetworkAvailable()){
				        		new UploadBiglietti(username, codiceAbb).execute();
				        	}else{
				        		Toast.makeText(getActivity().getApplicationContext(), "Controlla la tua connessione ad internet", Toast.LENGTH_LONG).show();
				        	}
							break;

				        case DialogInterface.BUTTON_NEGATIVE:
				            //No button clicked
				            break;
				        }
				    }
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Attenzione!");
				builder.setMessage("Sei sicuro?").setPositiveButton("Si", dialogClickListener)
				    .setNegativeButton("No", dialogClickListener).show();
				
			}
		});
		
	}
	
	//controllo se la connessione ad internet ï¿½ disponibile
		private boolean isNetworkAvailable() {
			ConnectivityManager connectivityManager 
			         = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		}

	/**
	 * Il compito di questa classe è fare l'upload dei dati relativi alla vendita
	 * di una nuova tratta all'utente il cui cellulare è scarico e quindi non
	 * può ottenerla tramite nfc. Egli deve conoscere il suo username e la sua
	 * password. Si vende solo la corsa base che quindi dura 90 minuti. Questa
	 * classe è stata dichiarate all'interna della classe FragmentNuovaCorsa
	 * perché viene utilizzata esclusivamente in questa classe e quindi per non
	 * creare confusione tra i sorgenti.
	 *
	 * The purpose of this class is to upload the data related to the sale of
	 * a new ride to a user whose telephone is discharged and so cannot obtain
	 * it through nfc. He should know his username and his password. It's sold
	 * only the base ride that lasts 90 minutes. This class has been declared
	 * as inner class of FragmentNuovaCorsa because it is used just in it, and
	 * so for not to make confusion among sources.
	 */
	public class UploadBiglietti extends AsyncTask<Void, Void, String> {
		
		//dichiaro tutte le variabili che servono per creare una nuova corsa
		private String username;
		private String codAbbonamento;
		
	    public UploadBiglietti(String username, String codAbbonamento){
	    	this.username=username;
	    	this.codAbbonamento=codAbbonamento;	
	    }
		
		private ProgressDialog pDialog;	
	    // Tag usato nei log.
	    // Tag used into logs.
	    private final String TAG = UploadBiglietti.class.getSimpleName();
	
	    // Test dall'Emulatore:
	    // Testing from Emulator:
	    private String AGGIUNGI_BIGLIETTI ;
	
	
	    // Tag(ID) del responso Json restituito dallo script php:
	    // JSON element ids from response of php script:
	    private static final String TAG_MESSAGE = "message";
	
	    /**
	     * Metodo che viene eseguito prima di doInBackground.
	     * Avvio il ProgressDialog.
	     * --
	     * Method executed before of doInBackgroung.
	     * I start the ProgressDialog.
	     */
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        pDialog = new ProgressDialog(getActivity());
	        pDialog.setMessage("Caricamento biglietti...");
	        // Dura un tempo indeterminato.
	        // It lasts a undefined time.
	        pDialog.setIndeterminate(false);
	        // E' cancellabile dal tasto Back.
	        // It's cancelable by the Back key.
	        pDialog.setCancelable(true);
	        pDialog.show();
	        
	        AGGIUNGI_BIGLIETTI = "http://www.letsmove.altervista.org/Upload/caricaBiglietti.php";
	    }
	
	    @Override
	    protected String doInBackground(Void... voids) {
	
	        String valoreRitornato = null;
	        JSONParser jsonParser = new JSONParser();
	        String user = username;
	        String codAbb = codAbbonamento;
	
	        try {
	
	            // Creazione dei parametri.
	            // Building Parameters.
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("username", user));
	            params.add(new BasicNameValuePair("codiceAbb", codAbb));
	
	            // Richiesta della pagina .php.
	            // Request to the .php page.
	            JSONObject json = jsonParser.makeHttpRequest(
	                    AGGIUNGI_BIGLIETTI, "POST", params);
	
	            // Responso json.
	            // Full json response.
	            Log.d(TAG, json.toString());
	
	
	            valoreRitornato = json.getString(TAG_MESSAGE);
	        } catch (JSONException e) {
	            Log.e(TAG, "Error " + e.toString());
	        }
	
	        return valoreRitornato;
	    }
	
	
	    /**
	     * Metodo eseguito dopo doInBackground.
	     * --
	     * Method executed after doInBackground.
	     * @param response Json response.
	     */
	    @Override
	    protected void onPostExecute(String response) {
	        super.onPostExecute(response);
	
	        // Chiudo il ProgressDialog.
	        // Close the ProgressDialog.
	        pDialog.dismiss();
	
	        
	        // Aggiorno l'utente circa il risultato.
	        // Tell to the user the response.
	        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
	        Log.d(TAG, response);
	
	        
	    }
	}

}