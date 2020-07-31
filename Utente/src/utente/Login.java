package utente;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.utente.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener{
	//dichiaro una variabile per controllare la validita dei dati
	private Boolean errore=false;
	
	//variabile di successo
	private int success;
	
	private Button btnRegistrati=null;
	private Button btnLogin=null;
	private EditText user = null;
	private EditText pass = null;
	private TextView txtPassLost=null;
	private CheckBox chkRimani=null;
	
	//dichiaro una variabile ke mi dice se ho internet
	boolean erroreInternet=false;
	
	//dichiaro gli intent per aprire le varie Activity collegate
	
	private Intent intentRegistrazione=null;
	private Intent intentEntrato=null;

    // Progress Dialog usato per non lasciare l'app inerme durante il collegamento con il db
    // Progress Dialog used for not to let the application stay not interagible
    private ProgressDialog pDialog;
	
	//file in cui viene salvato se il cliente � loggato o meno insieme al nickname
	public static final String PREFS_NAME = "MyPrefsFile";
	
	//testing from Emulator:
    private static final String LOGIN_URL = "http://www.letsmove.altervista.org/login.php";

    //Tag(ID) del responso Json restituito dallo script php
    //JSON element ids from repsonse of php script:
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//toglie la barra del titolo 
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		//blocco rotazione
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//inizializzo i vari intent per aprire le varie Activity
	    intentRegistrazione = new Intent(this, Registrazione.class);
		intentEntrato = new Intent(this, Main.class);

        //setup input fields
        user = (EditText)findViewById(R.id.edtNickname);
        pass = (EditText)findViewById(R.id.edtCAP);
        chkRimani=(CheckBox)findViewById(R.id.chkRestaCollegato);
        txtPassLost=(TextView)findViewById(R.id.txtPasswordLost);
        
        //imposto il colore della textview PassLost
        txtPassLost.setTextColor(Color.BLUE);
		
		btnRegistrati= (Button) findViewById(R.id.btnRegistrati);
		btnLogin=(Button) findViewById(R.id.btnLogin);
		
		btnRegistrati.setOnClickListener(this);
		btnRegistrati.setOnTouchListener( new BottoneCliccato( btnRegistrati.getAlpha() ));
		btnLogin.setOnClickListener(this);
		btnLogin.setOnTouchListener( new BottoneCliccato( btnLogin.getAlpha() ));
		txtPassLost.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.controllo, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
        //cambia a seconda del bottone premuto
        //changed in order of the pressed button
        switch (v.getId()) {
            case R.id.btnLogin:
            	//setto l'errore a false perche potrebbero non esserci piu problemi
            	errore=false;
            	
            	//controllo se e stato inserito username
            	if(user.getText().toString().isEmpty()){
    				// visualizzo un messaggio definito in strings.xml
            		user.setError("Inserire l'username");
    			     // attivo il controllo
            		user.requestFocus();
    			     //setto a true la variabile che guarda se i dati sono corretti
    			     errore=true;
    			}
            	
            	//controllo se e stato inserito username
            	if(pass.getText().toString().isEmpty()){
    				// visualizzo un messaggio definito in strings.xml
            		pass.setError("Inserire la password");
    			     // attivo il controllo
            		pass.requestFocus();
    			     //setto a true la variabile che guarda se i dati sono corretti
    			     errore=true;
    			}
            	
            	//se non ci sono errore eseguo il login
            	if(!errore){
            		new EsecuzioneLogin().execute(); 
            	}
                break;
            case R.id.btnRegistrati:
				startActivity(intentRegistrazione);
                break;
            case R.id.txtPasswordLost:
            	//setto l'errore a false perche potrebbero non esserci piu problemi
            	errore=false;
            	
            	//controllo se e stato inserito username
            	if(user.getText().toString().isEmpty()){
    				// visualizzo un messaggio definito in strings.xml
            		user.setError("Inserire l'username");
    			     // attivo il controllo
            		user.requestFocus();
    			     //setto a true la variabile che guarda se i dati sono corretti
    			     errore=true;
    			}
            	
            	//se non ci sono errori mando la mail
            	if(!errore){
            		new nuovaPassword(user.getText().toString()).execute();
            	}
            	break;
        }
		
	}
	
	//controllo se la connessione ad internet e disponibile
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	//AsyncTask: classe che permette di effettuare processi in background
    //AsyncTask: This class allows us to perform long/background operations and show its result on the UI thread without having to manipulate threads.
    //http://www.compiletimeerror.com/2013/01/why-and-how-to-use-asynctask.html#.UsLtpfjiB2U
    class EsecuzioneLogin extends AsyncTask<String, String, String> {  	
    	//Avvio dell Progress Dialog prima di cominciare le operazioni
        //Before starting background thread Show Progress Dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Richiesta di login...");
            pDialog.setIndeterminate(false);  //tempo indeterminato, undefined long time
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
        	if(isNetworkAvailable()){
        		// Check for success tag
                String username = user.getText().toString();
                String password = pass.getText().toString();
                JSONParser jsonParser = new JSONParser();
                try {
                    // Building Parameters
                    List<NameValuePair> params = new ArrayList<NameValuePair>();  //http://stackoverflow.com/a/17609232/2337094 Namevaluepair
                    params.add(new BasicNameValuePair("username", username));
                    params.add(new BasicNameValuePair("password", password));

                    Log.d("request!", "starting");  //http://developer.android.com/reference/android/util/Log.html log
                    // getting product details by making HTTP request
                    JSONObject json = jsonParser.makeHttpRequest(
                            LOGIN_URL, "POST", params);

                    // check your log for json response
                    Log.d("Login attempt", json.toString());

                    // json success tag
                    success = json.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        Log.d("Login Successful!", json.toString());
                        return json.getString(TAG_MESSAGE);
                    } else {
                        Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                        return json.getString(TAG_MESSAGE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
     
        	}else{
        		erroreInternet=true;
        	}
        	return null;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // Dismiss the dialog.
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();
            }
            if(erroreInternet){
            	Toast.makeText(Login.this, "Controlla la tua connessione ad Internet", Toast.LENGTH_LONG).show();
            	erroreInternet=false;
            }else{
            	//se le credenziali sono valide entra qua dentro
                if (success == 1) {
    				// We need an Editor object to make preference changes.
    			    // All objects are from android.context.Context
    				// Salvo nel file delle preferenze dell'applicazione che il cliente � loggato
    			    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
    			    SharedPreferences.Editor editor = settings.edit();
    			    //controllo se l'utente vuole rimanere collegato
    			    if(chkRimani.isChecked()==true){
    			    	editor.putBoolean("loggato", true);
    			    }   
    			    editor.putString("username", user.getText().toString());
    			    editor.putString("password", pass.getText().toString());
    	
    			    // Commit the edits!
    			    editor.commit();

    		    
    		    	startActivity(intentEntrato);
    				finish();
                }  
            }     
			
        }

    }
    
    //dichiaro un altra inner class per mandare la nuova password all'utente
    public class nuovaPassword extends AsyncTask<Void, Void, String> {
    	//dichiaro una variabile per contenere l'username
    	private String username;
    	
    	public nuovaPassword(String username){
	    	this.username=username;
	    }
    	
    	private ProgressDialog pDialog;	
	    // Tag usato nei log.
	    // Tag used into logs.
	    private final String TAG = nuovaPassword.class.getSimpleName();
	
	    //percorso della pagina php
	    private String NUOVA_PASSWORD;
	    
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
	        pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Invio in corso...");
            pDialog.setIndeterminate(false);  //tempo indeterminato, undefined long time
            pDialog.setCancelable(true);
            pDialog.show();
	        
	        NUOVA_PASSWORD = "http://www.letsmove.altervista.org/Upload/Password.php";
	    }
		
		@Override
	    protected String doInBackground(Void... voids) {
	
	        String valoreRitornato = null;
	        JSONParser jsonParser = new JSONParser();
	        String user = username;
	
	        try {
	
	            // Creazione dei parametri.
	            // Building Parameters.
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            params.add(new BasicNameValuePair("username", user));
	
	            // Richiesta della pagina .php.
	            // Request to the .php page.
	            JSONObject json = jsonParser.makeHttpRequest(
	            		NUOVA_PASSWORD, "POST", params);
	
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
	    }
    }

}
