package utente;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.utente.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GestioneAccount extends Activity {
	// File delle preferenze dell'applicazione.
	// Preferences file.
	public static final String PREFS_NAME = "MyPrefsFile";
	
	//dichiaro una variabile per controllare la validita dei dati
	private Boolean errore=false;
	
	//dichiaro tutti gli oggetti usati in questa classe
	private TextView txtEmail, txtCognome, txtNome, txtNascita, txtIndirizzo, txtLocalita, txtProv, txtCAP;
	private EditText edtEmail, edtCognome, edtNome, edtNascita, edtIndirizzo, edtLocalita, edtProv, edtCAP;
	private Button btnConferma=null;
	
	
	//dichiaro tutte le variabili per salvare i dati di input e output
	private String email,cognome,nome,nascita,indirizzo,localita,prov,CAP;
	
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
		setContentView(R.layout.activity_gestione);
		
		//blocco rotazione
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//inizializzo tutti gli oggetti dichiarati nella classe
		txtEmail = (TextView) findViewById(R.id.txtGestioneEmail); 
		txtCognome = (TextView) findViewById(R.id.txtGestioneCognome); 
		txtNome = (TextView) findViewById(R.id.txtGestioneNome); 
		txtNascita = (TextView) findViewById(R.id.txtGestioneNascita);
		txtIndirizzo = (TextView) findViewById(R.id.txtGestioneIndirizzo); 
		txtLocalita = (TextView) findViewById(R.id.txtGestioneLocalita);
		txtProv = (TextView) findViewById(R.id.txtGestioneProvincia); 
		txtCAP = (TextView) findViewById(R.id.txtGestioneCAP); 
		
		edtEmail = (EditText) findViewById(R.id.edtGestioneEmail); 
		edtCognome = (EditText) findViewById(R.id.edtGestioneCognome); 
		edtNome = (EditText) findViewById(R.id.edtGestioneNome); 
		edtNascita = (EditText) findViewById(R.id.edtGestioneNascita);
		edtIndirizzo = (EditText) findViewById(R.id.edtGestioneIndirizzo); 
		edtLocalita = (EditText) findViewById(R.id.edtGestioneLocalita);
		edtProv = (EditText) findViewById(R.id.edtGestioneProvincia); 
		edtCAP = (EditText) findViewById(R.id.edtGestioneCAP); 
		
		btnConferma = (Button) findViewById(R.id.btnCambiaDati);
		
		
		//metto il listener a tutte le txt contenenti informazioni e le sostituisco con delle edit text
		txtEmail.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				edtEmail.setText(txtEmail.getText().toString());
				txtEmail.setVisibility(TextView.GONE);
				edtEmail.setVisibility(EditText.VISIBLE);				
			}
		});
		
		txtCognome.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				edtCognome.setText(txtCognome.getText().toString());
				txtCognome.setVisibility(TextView.GONE);
				edtCognome.setVisibility(EditText.VISIBLE);				
			}
		});
		
		txtNome.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				edtNome.setText(txtNome.getText().toString());
				txtNome.setVisibility(TextView.GONE);
				edtNome.setVisibility(EditText.VISIBLE);				
			}
		});
		
		txtNascita.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				edtNascita.setText(txtNascita.getText().toString());
				txtNascita.setVisibility(TextView.GONE);
				edtNascita.setVisibility(EditText.VISIBLE);				
			}
		});
		
		txtIndirizzo.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				edtIndirizzo.setText(txtIndirizzo.getText().toString());
				txtIndirizzo.setVisibility(TextView.GONE);
				edtIndirizzo.setVisibility(EditText.VISIBLE);				
			}
		});
		
		txtLocalita.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				edtLocalita.setText(txtLocalita.getText().toString());
				txtLocalita.setVisibility(TextView.GONE);
				edtLocalita.setVisibility(EditText.VISIBLE);				
			}
		});
		
		txtProv.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				edtProv.setText(txtProv.getText().toString());
				txtProv.setVisibility(TextView.GONE);
				edtProv.setVisibility(EditText.VISIBLE);				
			}
		});
		
		txtCAP.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				edtCAP.setText(txtCAP.getText().toString());
				txtCAP.setVisibility(TextView.GONE);
				edtCAP.setVisibility(EditText.VISIBLE);				
			}
		});
		
		//metto il listener al pulsante conferma
		btnConferma.setOnTouchListener( new BottoneCliccato( btnConferma.getAlpha() ));
		btnConferma.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//metto la variabile di errore inserimento a false
				errore=false;
				
				//assegno alle variabili i valori delle txt e delle edt
				if(edtEmail.getText().toString().equals("")){
					email=txtEmail.getText().toString();
				}else{
					email=edtEmail.getText().toString();
				}
				
				if(edtCognome.getText().toString().equals("")){
					cognome=txtCognome.getText().toString();
				}else{
					cognome=edtCognome.getText().toString();
				}
				
				if(edtNome.getText().toString().equals("")){
					nome=txtNome.getText().toString();
				}else{
					nome=edtNome.getText().toString();
				}
				
				if(edtNascita.getText().toString().equals("")){
					nascita=txtNascita.getText().toString();
				}else{
					nascita=edtNascita.getText().toString();
				}
				
				if(edtIndirizzo.getText().toString().equals("")){
					indirizzo=txtIndirizzo.getText().toString();
				}else{
					indirizzo=edtIndirizzo.getText().toString();
				}
				
				if(edtLocalita.getText().toString().equals("")){
					localita=txtLocalita.getText().toString();
				}else{
					localita=edtLocalita.getText().toString();
				}
				
				if(edtProv.getText().toString().equals("")){
					prov=txtProv.getText().toString();
				}else{
					prov=edtProv.getText().toString();
				}
				
				if(edtCAP.getText().toString().equals("")){
					CAP=txtCAP.getText().toString();
				}else{
					CAP=edtCAP.getText().toString();
				}
				
				//controllo se il campo non e vuoto
				if(email.isEmpty()){
					// visualizzo un messaggio definito in strings.xml
				     edtEmail.setError("Compilare il campo");
				     // attivo il controllo
				     edtEmail.requestFocus();
				     //setto a true la variabile che guarda se i dati sono corretti
				     errore=true;
				}
				
				//controllo se il campo non e vuoto
				if(cognome.isEmpty()){
					// visualizzo un messaggio definito in strings.xml
				     edtCognome.setError("Compilare il campo");
				     // attivo il controllo
				     edtCognome.requestFocus();
				     //setto a true la variabile che guarda se i dati sono corretti
				     errore=true;
				}
				
				//controllo se il campo non e vuoto
				if(nome.isEmpty()){
					// visualizzo un messaggio definito in strings.xml
				     edtNome.setError("Compilare il campo");
				     // attivo il controllo
				     edtNome.requestFocus();
				     //setto a true la variabile che guarda se i dati sono corretti
				     errore=true;
				}
				
				//controllo se il campo non e vuoto
				if(nascita.isEmpty()){
					// visualizzo un messaggio definito in strings.xml
				     edtNascita.setError("Compilare il campo");
				     // attivo il controllo
				     edtNascita.requestFocus();
				     //setto a true la variabile che guarda se i dati sono corretti
				     errore=true;
				}
				
				//controllo se il campo non e vuoto
				if(indirizzo.isEmpty()){
					// visualizzo un messaggio definito in strings.xml
				     edtIndirizzo.setError("Compilare il campo");
				     // attivo il controllo
				     edtIndirizzo.requestFocus();
				     //setto a true la variabile che guarda se i dati sono corretti
				     errore=true;
				}
				
				//controllo se il campo non e vuoto
				if(localita.isEmpty()){
					// visualizzo un messaggio definito in strings.xml
				     edtLocalita.setError("Compilare il campo");
				     // attivo il controllo
				     edtLocalita.requestFocus();
				     //setto a true la variabile che guarda se i dati sono corretti
				     errore=true;
				}
				
				//controllo se il campo non e vuoto
				if(prov.isEmpty()){
					// visualizzo un messaggio definito in strings.xml
				     edtProv.setError("Compilare il campo");
				     // attivo il controllo
				     edtProv.requestFocus();
				     //setto a true la variabile che guarda se i dati sono corretti
				     errore=true;
				}
				
				//controllo se il campo non e vuoto
				if(CAP.isEmpty()){
					// visualizzo un messaggio definito in strings.xml
				     edtCAP.setError("Compilare il campo");
				     // attivo il controllo
				     edtCAP.requestFocus();
				     //setto a true la variabile che guarda se i dati sono corretti
				     errore=true;
				}
				
				//controllo se il CAP ha la lunghezza giusta
				if(CAP.length()!=5){
					//visualizzo il messaggio di errore
					edtCAP.setError("CAP non valido");
					//attivo il controllo
					edtCAP.requestFocus();
					//segnalo l'erore
					errore=true;
				}
				
				//controllo se la sigla della provincia ha la lunghezza giusta
				if(prov.length()!=2){
					//visualizzo il messaggio di errore
					edtProv.setError("provincia non valida");
					//attivo il controllo
					edtProv.requestFocus();
					//segnalo l'erore
					errore=true;
				}
				
				//controllo se l'email � giusta	
				try{
					//controllo se ce la @
					if(email.split("@")[1].equals("")){
						//visualizzo il messaggio di errore
						edtEmail.setError("email non valida");
						//attivo il controllo
						edtEmail.requestFocus();
						//segnalo l'erore
						errore=true;
					}
					//controllo se ce il .
					if(email.split("\\.")[1].equals("")){
						//visualizzo il messaggio di errore
						edtEmail.setError("email non valida");
						//attivo il controllo
						edtEmail.requestFocus();
						//segnalo l'erore
						errore=true;
					}
					
				}catch(ArrayIndexOutOfBoundsException e){
					//visualizzo il messaggio di errore
					edtEmail.setError("email non valida!");
					//attivo il controllo
					edtEmail.requestFocus();
					//segnalo l'erore
					errore=true;
				}
				
				//controllo la validita della data	
				try{
					//controllo se ce il -
					if(nascita.split("-")[0].equals("") || nascita.split("-")[1].equals("") || nascita.split("-")[2].equals("")){
						//visualizzo il messaggio di errore
						edtNascita.setError("data non valida");
						//attivo il controllo
						edtNascita.requestFocus();
						//segnalo l'erore
						errore=true;
					}
					
					//controllo se i vari campi della data solo della lunghezza giusta
					if(nascita.split("-")[0].length()!=4 || nascita.split("-")[1].length()>2 || nascita.split("-")[2].length()>2){
						//visualizzo il messaggio di errore
						edtNascita.setError("data non valida");
						//attivo il controllo
						edtNascita.requestFocus();
						//segnalo l'erore
						errore=true;
					}
					
					//controllo se i numeri della data sono accettabili
					if(Integer.parseInt(nascita.split("-")[0])>2100 || Integer.parseInt(nascita.split("-")[0])<1900 || Integer.parseInt(nascita.split("-")[1])>12 || Integer.parseInt(nascita.split("-")[1])<1 || Integer.parseInt(nascita.split("-")[2])>31 || Integer.parseInt(nascita.split("-")[2])<1){
						//visualizzo il messaggio di errore
						edtNascita.setError("data non valida");
						//attivo il controllo
						edtNascita.requestFocus();
						//segnalo l'erore
						errore=true;
					}
					
				}catch(ArrayIndexOutOfBoundsException e){
					//visualizzo il messaggio di errore
					edtNascita.setError("data non valida!");
					//attivo il controllo
					edtNascita.requestFocus();
					//segnalo l'erore
					errore=true;
				}catch(NumberFormatException ex){
					//visualizzo il messaggio di errore
					edtNascita.setError("data non valida!");
					//attivo il controllo
					edtNascita.requestFocus();
					//segnalo l'erore
					errore=true;
				}
				
				//se non ci sono errori creo l'utente
				if(!errore){
					new ModificaDati().execute();	
				}
			}
		});
		
		if(isNetworkAvailable()){
			//faccio partire il download dei dati dell'utente
			new DownloadDati().execute();
		}else{
			Toast.makeText(getApplicationContext(), "Controlla la tua connessione ad internet", Toast.LENGTH_LONG).show();
		}
	}
	
	class ModificaDati extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;
        
        // Tag usato nei log.
	    // Tag used into logs.
	    private final String TAG = ModificaDati.class.getSimpleName();
	    
	    // Tag(ID) del responso Json restituito dallo script php:
	    // JSON element ids from response of php script:
	    private static final String TAG_MESSAGE = "message";
        
        //percorso della pagina php
        private String MODIFICA_DATI;

        // JSON parser class
        JSONParser jsonParser = new JSONParser();
		/**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
        	 super.onPreExecute();
 	         pDialog = new ProgressDialog(GestioneAccount.this);
             pDialog.setMessage("Aggiornamento in corso...");
             pDialog.setIndeterminate(false);  //tempo indeterminato, undefined long time
             pDialog.setCancelable(true);
             pDialog.show();
 	        
             MODIFICA_DATI = "http://www.letsmove.altervista.org/Upload/aggiornaDatiUtente.php";
        }

        @Override
        protected String doInBackground(String... args) {
        	// Restore preferences
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			
			String valoreRitornato = null;
	        JSONParser jsonParser = new JSONParser();
	        
	        try {
        	
    		Log.d("modifica dati", "doInBackground");
            // Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", settings.getString("username", "sconosciuto")));
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("cognome", cognome));
			params.add(new BasicNameValuePair("nome", nome));
			params.add(new BasicNameValuePair("nascita", nascita));
			params.add(new BasicNameValuePair("indirizzo", indirizzo));
			params.add(new BasicNameValuePair("localita", localita));
			params.add(new BasicNameValuePair("prov", prov));
			params.add(new BasicNameValuePair("CAP", CAP));

			// Richiesta della pagina .php.
			// Request to the .php page.
			JSONObject json = jsonParser.makeHttpRequest(
					MODIFICA_DATI, "POST", params);

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
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String response) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            
            // Aggiorno l'utente circa il risultato.
	        // Tell to the user the response.
	        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
	        Log.d(TAG, response);
            
            //chiudo l'actitivy di update dati utente
            finish();
        }

    }
	
public class DownloadDati extends AsyncTask<Void, Void, String> {
		
	    private String DOWNLOAD_DATI;
		
		private ProgressDialog pDialog;	
		
	    // Tag usato nei log.
	    private final String TAG = DownloadDati.class.getSimpleName();	
	
	    // Tag(ID) del responso Json restituito dallo script php.
	    private static final String TAG_MESSAGE = "message";
	    
	    //dichiaro le variabili da scaricare
	    //la lettera I indica che sono variabili della inner class, per essere differenziate dalle altre
	    private String Iemail, Icognome, Inome, Inascita, Iindirizzo, Ilocalita, Iprov, ICAP;
	    
	
	    /**
	     * Metodo che viene eseguito prima di doInBackground.
	     * Avvio il ProgressDialog.
	     */
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        pDialog = new ProgressDialog(GestioneAccount.this);
	        pDialog.setMessage("Download dati...");
	        // Dura un tempo indeterminato.
	        pDialog.setIndeterminate(false);
	        // E' cancellabile dal tasto Back.
	        pDialog.setCancelable(true);
	        pDialog.show();
	        
	        DOWNLOAD_DATI = "http://www.letsmove.altervista.org/Download/datiUtente.php";
	    }
	
	    @Override
	    protected String doInBackground(Void... voids) {
	    	// Restore preferences
	    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    	
	    	//dichiaro l'oggetto json
	    	JSONParser jsonParser = new JSONParser();
	    	
	    	// Creazione dei parametri.
            // Building Parameters.
            List<NameValuePair> params = new ArrayList<NameValuePair>();  //http://stackoverflow.com/a/17609232/2337094 Namevaluepair
            params.add(new BasicNameValuePair("username", settings.getString("username", "sconosciuto")));
            
            // Avvio richiesta.
            // Start the request.
            JSONObject json = jsonParser.makeHttpRequest(DOWNLOAD_DATI, "POST", params);
	    	
            try {

                /*
                Controllo che il download sia avvenuto con successo,
                altrimenti il codice da errore perchè cerca json.getJSONArray(TAG_POSTS)
                che non esiste.
                --
                Check that the download has been successful, elsewhere the
                code seeks for json.getJSONArray(TAG_POSTS) that does not exists.
                */
                if(Integer.parseInt(json.getString("success")) == 1){

                    // Ottengo l'array dei dati scricati.
                    // I get an array of the downloaded data.
                    JSONArray home = json.getJSONArray("posts");

                    // Siccome nella query vi è un LIMIT 1 non serve il ciclo, e prendo solo
                    // l'elemento in posizione 0.
                    // --
                    // Because in the query there is LIMIT 1, I don't need a for, and I take
                    // just the first element
                    JSONObject jobj = home.getJSONObject(0);

                    // Inserisco i valori nelle variabili.
                    // Set the variables with the values.
                    Iemail = jobj.getString("email");
                    Icognome = jobj.getString("cognome");
                    Inome = jobj.getString("nome");
                    Inascita = jobj.getString("nascita");
                    Iindirizzo = jobj.getString("indirizzo");
                    Ilocalita = jobj.getString("localita");
                    Iprov = jobj.getString("provincia");
                    ICAP = jobj.getString("CAP");

                    Log.d(TAG, json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d(TAG, json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing data " + e.toString());
            }
            return null;
	        
	    }
	
	
	    /**
	     * Metodo eseguito dopo doInBackground.
	     * @param response Json response.
	     */
	    @Override
	    protected void onPostExecute(String response) {
	        super.onPostExecute(response);
	
	        // Chiudo il ProgressDialog.
	        pDialog.dismiss();

	        if(response == null){
                Toast.makeText(getApplicationContext(), "Errore nella connessione con il Database.", Toast.LENGTH_LONG).show();
               Log.e(TAG, "Errore nella connessione con il Database.");
            } else {
                txtEmail.setText(Iemail);
                txtCognome.setText(Icognome);
                txtNome.setText(Inome);
                txtNascita.setText(Inascita);
                txtIndirizzo.setText(Iindirizzo);
                txtLocalita.setText(Ilocalita);
                txtProv.setText(Iprov);
                txtCAP.setText(ICAP);
                
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                Log.e(TAG, response);
            }
	
	        
	    }
	}
	
}
