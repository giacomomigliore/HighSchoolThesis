package utente;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.utente.R;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ImpostazioniAvanzate extends Activity{
	//dichiaro l'intent per cambiare l'activity
	private Intent intent=null;	
	
	// File delle preferenze dell'applicazione.
	// Preferences file.
	public static final String PREFS_NAME = "MyPrefsFile";
	
	//dichiaro una variabile per controlla se i dati inseriti sono corretti
	private Boolean errore=false;
	
	//dichiaro gli oggetti presenti nell'activity
	private Button btnModifica=null;
	private Button btnElimina=null;
	
	private EditText edtOldPassword=null;
	private EditText edtNuovaPassword=null;
	private EditText edtConfermaPassword=null;
	
	//dichiaro una variabile per contenere l'username dell'utente loggato
	private String username;
	
	//controllo se la connessione ad internet e disponibile
		private boolean isNetworkAvailable() {
			ConnectivityManager connectivityManager 
		          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//toglie la barra del titolo 
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_avanzate);
		
		//blocco rotazione
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//inizializzo tutti gli oggetti
		edtOldPassword = (EditText) findViewById(R.id.edtOldPassword);
		edtNuovaPassword = (EditText) findViewById(R.id.edtNuovaPassword);
		edtConfermaPassword = (EditText) findViewById(R.id.edtConfermaPassword);
		
		btnModifica = (Button) findViewById(R.id.btnModifica);
		btnModifica.setOnTouchListener( new BottoneCliccato( btnModifica.getAlpha() ));
		btnElimina = (Button) findViewById(R.id.btnElimina);
		btnElimina.setOnTouchListener( new BottoneCliccato( btnElimina.getAlpha() ));
		
		btnModifica.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// Restore preferences
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				// Leggo dal file delle preferenze il nome dell'utente e lo scrivo nella TextView della Home.
				// Read the name of the user from the preferencee file and write it in the Home.
				username=settings.getString("username", "sconosciuto");
				
				//setto l'errore a false perche potrebbero non esserci piu problemi
            	errore=false;
            	
            	//controllo se e stato inserito username
            	if(edtOldPassword.getText().toString().isEmpty()){
    				// visualizzo un messaggio definito in strings.xml
            		edtOldPassword.setError("Inserire la password");
    			     // attivo il controllo
            		edtOldPassword.requestFocus();
    			     //setto a true la variabile che guarda se i dati sono corretti
    			     errore=true;
    			}
            	
            	//controllo se e stato inserito username
            	if(edtNuovaPassword.getText().toString().isEmpty()){
    				// visualizzo un messaggio definito in strings.xml
            		edtNuovaPassword.setError("Inserire la password");
    			     // attivo il controllo
            		edtNuovaPassword.requestFocus();
    			     //setto a true la variabile che guarda se i dati sono corretti
    			     errore=true;
    			}
            	
            	//controllo se e stato inserito username
            	if(edtConfermaPassword.getText().toString().isEmpty()){
    				// visualizzo un messaggio definito in strings.xml
            		edtConfermaPassword.setError("Inserire la password");
    			     // attivo il controllo
            		edtConfermaPassword.requestFocus();
    			     //setto a true la variabile che guarda se i dati sono corretti
    			     errore=true;
    			}
            	
            	//controllo se la password soddisfa i requisiti di lunghezza minimi
    			if(edtNuovaPassword.getText().toString().length()<8){
    				//visualizzo l'errore
    				edtNuovaPassword.setError("Password troppo corta");
    				//attivo il controllo
    				edtNuovaPassword.requestFocus();
    				//segnalo l'erore
    				errore=true;				
    			}
    			
    			//controllo se password e conferma password coincidono
    			if(!edtNuovaPassword.getText().toString().equals(edtConfermaPassword.getText().toString())){
    				//visualizzo il messaggio di errore
    				edtConfermaPassword.setError("Le password non coincidono");
    				//attivo il controllo
    				edtConfermaPassword.requestFocus();
    				//segnalo l'erore
    				errore=true;
    			}
            	
            	//se non ci sono errore eseguo il login
            	if(!errore){
            		if(isNetworkAvailable()){
            			new cambiaPassword(username,edtOldPassword.getText().toString(),edtNuovaPassword.getText().toString()).execute(); 
            		}else{
            			Toast.makeText(getApplicationContext(), "Controlla la tua connessione ad internet", Toast.LENGTH_LONG).show();
            		}
            	}
				
			}
		});
		
		btnElimina.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// Restore preferences
				SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
				// Leggo dal file delle preferenze il nome dell'utente e lo scrivo nella TextView della Home.
				// Read the name of the user from the preferencee file and write it in the Home.
				username=settings.getString("username", "sconosciuto");
				
				//faccio comparire la finestra di conferma dell'acquisto 
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				            //Yes button clicked
				        	if(isNetworkAvailable()){
				        		//eseguo la classe che elimina l'account utente
								new eliminaAccount(username).execute();
				        	}else{
				        		Toast.makeText(getApplicationContext(), "Controlla la tua connessione ad internet", Toast.LENGTH_LONG).show();
				        	}				        	
				            break;
				        case DialogInterface.BUTTON_NEGATIVE:
				            //No button clicked
				            break;
				        }
				    }
				};
				AlertDialog.Builder builder = new AlertDialog.Builder(ImpostazioniAvanzate.this);
				builder.setTitle("Attenzione!");
				builder.setMessage("Sei siruro di voler eliminare definitivamente il tuo account?").setPositiveButton("Si", dialogClickListener)
				    .setNegativeButton("No", dialogClickListener).show();
								
			}
		});
		
	}
	
	//dichiaro un altra inner class per mandare la richiesta al db di cambiare la password
    public class cambiaPassword extends AsyncTask<Void, Void, String> {
    	//dichiaro una variabile per contenere l'username e le password
    	private String username;
    	private String oldPassword;
    	private String nuovaPassword;
    	
    	public cambiaPassword(String username, String oldPassword, String nuovaPassword){
	    	this.username=username;
	    	this.oldPassword=oldPassword;
	    	this.nuovaPassword=nuovaPassword;
	    }
    	
    	private ProgressDialog pDialog;	
	    // Tag usato nei log.
	    // Tag used into logs.
	    private final String TAG = cambiaPassword.class.getSimpleName();
	
	    //percorso della pagina php
	    private String CAMBIA_PASSWORD;
	    
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
	        pDialog = new ProgressDialog(ImpostazioniAvanzate.this);
            pDialog.setMessage("Cambiamento password in corso...");
            pDialog.setIndeterminate(false);  //tempo indeterminato, undefined long time
            pDialog.setCancelable(true);
            pDialog.show();
	        
	        CAMBIA_PASSWORD = "http://www.letsmove.altervista.org/Upload/cambiaPassword.php";
	    }
		
		@Override
	    protected String doInBackground(Void... voids) {
	
	        String valoreRitornato = null;
	        JSONParser jsonParser = new JSONParser();
	
	        try {
	
	            // Creazione dei parametri.
	            // Building Parameters.
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("username", username));
	            params.add(new BasicNameValuePair("oldPassword", oldPassword));
	            params.add(new BasicNameValuePair("newPassword", nuovaPassword));
	
	            // Richiesta della pagina .php.
	            // Request to the .php page.
	            JSONObject json = jsonParser.makeHttpRequest(
	            		CAMBIA_PASSWORD, "POST", params);
	
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
	        
	        edtNuovaPassword.setText("");
	        edtOldPassword.setText("");
	        edtConfermaPassword.setText("");
	        
	        // Aggiorno l'utente circa il risultato.
	        // Tell to the user the response.
	        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
	        Log.d(TAG, response);	        
	    }
    }
    
  //dichiaro un altra inner class per mandare la richiesta al db di cambiare la password
    public class eliminaAccount extends AsyncTask<Void, Void, String> {
    	//dichiaro una variabile per contenere l'username e le password
    	private String username;
    	
    	public eliminaAccount(String username){
	    	this.username=username;
	    }
    	
    	private ProgressDialog pDialog;	
	    // Tag usato nei log.
	    // Tag used into logs.
	    private final String TAG = eliminaAccount.class.getSimpleName();
	
	    //percorso della pagina php
	    private String ELIMINA_ACCOUNT;
	    
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
	        pDialog = new ProgressDialog(ImpostazioniAvanzate.this);
            pDialog.setMessage("Eliminazione in corso...");
            pDialog.setIndeterminate(false);  //tempo indeterminato, undefined long time
            pDialog.setCancelable(true);
            pDialog.show();
	        
            ELIMINA_ACCOUNT = "http://www.letsmove.altervista.org/Upload/eliminaUtente.php";
	    }
		
		@Override
	    protected String doInBackground(Void... voids) {
	
	        String valoreRitornato = null;
	        JSONParser jsonParser = new JSONParser();
	
	        try {
	
	            // Creazione dei parametri.
	            // Building Parameters.
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("username", username));
	
	            // Richiesta della pagina .php.
	            // Request to the .php page.
	            JSONObject json = jsonParser.makeHttpRequest(
	            		ELIMINA_ACCOUNT, "POST", params);
	
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
	        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
	        Log.d(TAG, response);	 
	        
	      //C'è bisogno di un editor per cambiare il file delle preferenze.
			// Segnalo che l'utente non è più loggato.
			// --
			// We need an Editor object to make preference changes.
		    // All objects are from android.context.Context
		    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putBoolean("loggato", false);

		    // Commit the edits!
		    editor.commit();

		    // Cambio Activity.
		    // Change Activity.
			intent = new Intent(getApplicationContext(), Login.class);
			startActivity(intent);
			finish();	
	    }
    }	
}
