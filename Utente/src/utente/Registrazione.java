package utente;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.utente.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registrazione extends Activity implements OnClickListener{
	//dichiaro tutte le variabili per contenere i dati di registrazione
	private String strCodDocumento;
	private String strUsername;
	private String strPassword;
	private String strEmail;
	private String strCognome;
	private String strNome;
	private String strNascita;
	private String strLocalita;
	private String strIndirizzo;
	private String strProvincia;
	private String strCAP;
	private String strRiPassword;
	
	//dichiaro una variabile per controllare la validita dei dati
	private Boolean errore=false;
	
	private Button btnConferma=null;
	private Intent intent=null;
	private EditText codDocumento, username, password, email, cognome, nome, nascita, indirizzo, localita, provincia, CAP, riPassword;

	//testing on Emulator:
    private static final String REGISTER_URL = "http://www.letsmove.altervista.org/register.php";

    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    
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
		setContentView(R.layout.activity_registrazione);
		intent = new Intent(this, Login.class);
		
		//blocco rotazione
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
        codDocumento = (EditText)findViewById(R.id.edtCodDocumento);
        username = (EditText)findViewById(R.id.edtUsername);
        password = (EditText)findViewById(R.id.edtPassword);
        email = (EditText)findViewById(R.id.edtEmail);
        cognome = (EditText)findViewById(R.id.edtCognome);
        nome = (EditText)findViewById(R.id.edtNome);
        indirizzo = (EditText)findViewById(R.id.edtIndirizzo);
        localita = (EditText)findViewById(R.id.edtLocalita);
        provincia = (EditText)findViewById(R.id.edtProvincia);
        nascita = (EditText)findViewById(R.id.edtNascita);
        CAP = (EditText)findViewById(R.id.edtCAP);
        riPassword = (EditText)findViewById(R.id.edtRiPassword);
		
		btnConferma=(Button) findViewById(R.id.btnRegistrazione);
		btnConferma.setOnTouchListener( new BottoneCliccato( btnConferma.getAlpha() ));
		btnConferma.setOnClickListener(this);
		
		
	}
		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.controllo, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if(isNetworkAvailable()){
			//metto la variabile dell'errore a false
			errore=false;
			
			Log.d("Registrazione", "Prima del asynctaks");
			
			//inizializzo tutte le variabili di registrazione
			strCodDocumento = codDocumento.getText().toString();
			strUsername = username.getText().toString();
			strPassword = password.getText().toString();
			strEmail = email.getText().toString();
			strCognome = cognome.getText().toString();
			strNome = nome.getText().toString();
			strNascita = nascita.getText().toString();
			strLocalita = localita.getText().toString();
			strIndirizzo = indirizzo.getText().toString();
			strProvincia = provincia.getText().toString();
			strCAP = CAP.getText().toString();
			strRiPassword = riPassword.getText().toString();
			
			//controllo se il campo non e vuoto
			if(strCodDocumento.isEmpty()){
				// visualizzo un messaggio definito in strings.xml
			     codDocumento.setError("Compilare il campo");
			     // attivo il controllo
			     codDocumento.requestFocus();
			     
			     //setto a true la variabile che guarda se i dati sono corretti
			     errore=true;
			}
			
			//controllo se il campo non e vuoto
			if(strUsername.isEmpty()){
				// visualizzo un messaggio definito in strings.xml
			     username.setError("Compilare il campo");
			     // attivo il controllo
			     username.requestFocus();
			     //setto a true la variabile che guarda se i dati sono corretti
			     errore=true;
			}
			
			//controllo se il campo non e vuoto
			if(strPassword.isEmpty()){
				// visualizzo un messaggio definito in strings.xml
			     password.setError("Compilare il campo");
			     // attivo il controllo
			     password.requestFocus();
			     //setto a true la variabile che guarda se i dati sono corretti
			     errore=true;
			}
			
			//controllo se il campo non e vuoto
			if(strRiPassword.isEmpty()){
				// visualizzo un messaggio definito in strings.xml
			     riPassword.setError("Compilare il campo");
			     // attivo il controllo
			     riPassword.requestFocus();
			     //setto a true la variabile che guarda se i dati sono corretti
			     errore=true;
			}
			
			//controllo se il campo non e vuoto
			if(strEmail.isEmpty()){
				// visualizzo un messaggio definito in strings.xml
			     email.setError("Compilare il campo");
			     // attivo il controllo
			     email.requestFocus();
			     //setto a true la variabile che guarda se i dati sono corretti
			     errore=true;
			}
			
			//controllo se il campo non e vuoto
			if(strCognome.isEmpty()){
				// visualizzo un messaggio definito in strings.xml
			     cognome.setError("Compilare il campo");
			     // attivo il controllo
			     cognome.requestFocus();
			     //setto a true la variabile che guarda se i dati sono corretti
			     errore=true;
			}
			
			//controllo se il campo non e vuoto
			if(strNome.isEmpty()){
				// visualizzo un messaggio definito in strings.xml
			     nome.setError("Compilare il campo");
			     // attivo il controllo
			     nome.requestFocus();
			     //setto a true la variabile che guarda se i dati sono corretti
			     errore=true;
			}
			
			//controllo se il campo non e vuoto
			if(strNascita.isEmpty()){
				// visualizzo un messaggio definito in strings.xml
			     nascita.setError("Compilare il campo");
			     // attivo il controllo
			     nascita.requestFocus();
			     //setto a true la variabile che guarda se i dati sono corretti
			     errore=true;
			}
			
			//controllo se il campo non e vuoto
			if(strLocalita.isEmpty()){
				// visualizzo un messaggio definito in strings.xml
			     localita.setError("Compilare il campo");
			     // attivo il controllo
			     localita.requestFocus();
			     //setto a true la variabile che guarda se i dati sono corretti
			     errore=true;
			}
			
			//controllo se il campo non e vuoto
			if(strIndirizzo.isEmpty()){
				// visualizzo un messaggio definito in strings.xml
			     indirizzo.setError("Compilare il campo");
			     // attivo il controllo
			     indirizzo.requestFocus();
			     //setto a true la variabile che guarda se i dati sono corretti
			     errore=true;
			}
			
			//controllo se il campo non e vuoto
			if(strProvincia.isEmpty()){
				// visualizzo un messaggio definito in strings.xml
			     provincia.setError("Compilare il campo");
			     // attivo il controllo
			     provincia.requestFocus();
			     //setto a true la variabile che guarda se i dati sono corretti
			     errore=true;
			}
			
			//controllo se il campo non e vuoto
			if(strCAP.isEmpty()){
				// visualizzo un messaggio definito in strings.xml
			     CAP.setError("Compilare il campo");
			     // attivo il controllo
			     CAP.requestFocus();
			     //setto a true la variabile che guarda se i dati sono corretti
			     errore=true;
			}
			
			//controllo se la password soddisfa i requisiti di lunghezza minimi
			if(strPassword.length()<8){
				//visualizzo l'errore
				password.setError("Password troppo corta");
				//attivo il controllo
				password.requestFocus();
				//segnalo l'erore
				errore=true;				
			}
			
			//controllo se password e conferma password coincidono
			if(!strPassword.equals(strRiPassword)){
				//visualizzo il messaggio di errore
				riPassword.setError("Le password non coincidono");
				//attivo il controllo
				riPassword.requestFocus();
				//segnalo l'erore
				errore=true;
			}
			
			//controllo se la lunghezza del codice documento e maggiore di 9
			if(strCodDocumento.length()<9){
				//visualizzo il messaggio di errore
				codDocumento.setError("Codice documento non valido");
				//attivo il controllo
				codDocumento.requestFocus();
				//segnalo l'erore
				errore=true;
			}
			
			//controllo se il CAP ha la lunghezza giusta
			if(strCAP.length()!=5){
				//visualizzo il messaggio di errore
				CAP.setError("CAP non valido");
				//attivo il controllo
				CAP.requestFocus();
				//segnalo l'erore
				errore=true;
			}
			
			//controllo se la sigla della provincia ha la lunghezza giusta
			if(strProvincia.length()!=2){
				//visualizzo il messaggio di errore
				provincia.setError("provincia non valida");
				//attivo il controllo
				provincia.requestFocus();
				//segnalo l'erore
				errore=true;
			}
			
			//controllo se l'email è giusta	
			try{
				//controllo se ce la @
				if(strEmail.split("@")[1].equals("")){
					//visualizzo il messaggio di errore
					email.setError("email non valida");
					//attivo il controllo
					email.requestFocus();
					//segnalo l'erore
					errore=true;
				}
				//controllo se ce il .
				if(strEmail.split("\\.")[1].equals("")){
					//visualizzo il messaggio di errore
					email.setError("email non valida");
					//attivo il controllo
					email.requestFocus();
					//segnalo l'erore
					errore=true;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				//visualizzo il messaggio di errore
				email.setError("email non valida!");
				//attivo il controllo
				email.requestFocus();
				//segnalo l'erore
				errore=true;
			}
			
			//controllo la validita della data	
			try{
				//controllo se ce il -
				if(strNascita.split("-")[0].equals("") || strNascita.split("-")[1].equals("") || strNascita.split("-")[2].equals("")){
					//visualizzo il messaggio di errore
					nascita.setError("data non valida");
					//attivo il controllo
					nascita.requestFocus();
					//segnalo l'erore
					errore=true;
				}
				
				//controllo se i vari campi della data solo della lunghezza giusta
				if(strNascita.split("-")[0].length()!=4 || strNascita.split("-")[1].length()>2 || strNascita.split("-")[2].length()>2){
					//visualizzo il messaggio di errore
					nascita.setError("data non valida");
					//attivo il controllo
					nascita.requestFocus();
					//segnalo l'erore
					errore=true;
				}
				
				//controllo se i numeri della data sono accettabili
				if(Integer.parseInt(strNascita.split("-")[0])>2100 || Integer.parseInt(strNascita.split("-")[0])<1900 || Integer.parseInt(strNascita.split("-")[1])>12 || Integer.parseInt(strNascita.split("-")[1])<1 || Integer.parseInt(strNascita.split("-")[2])>31 || Integer.parseInt(strNascita.split("-")[2])<1){
					//visualizzo il messaggio di errore
					nascita.setError("data non valida");
					//attivo il controllo
					nascita.requestFocus();
					//segnalo l'erore
					errore=true;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				//visualizzo il messaggio di errore
				nascita.setError("data non valida!");
				//attivo il controllo
				nascita.requestFocus();
				//segnalo l'erore
				errore=true;
			}catch(NumberFormatException ex){
				//visualizzo il messaggio di errore
				nascita.setError("data non valida!");
				//attivo il controllo
				nascita.requestFocus();
				//segnalo l'erore
				errore=true;
			}
						
			//se non ci sono errori creo l'utente
			if(!errore){
				new CreaUtente().execute();	
			}
			
		}else{
			Toast.makeText(getApplicationContext(), "Controlla la tua connessione ad Internet", Toast.LENGTH_LONG).show();
		}
		
	}
	
	class CreaUtente extends AsyncTask<String, String, String> {

        private ProgressDialog pDialog;

        // JSON parser class
        JSONParser jsonParser = new JSONParser();
		/**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Registrazione.this);
            pDialog.setMessage("Creating User...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
    		Log.d("Registrazione", "doInBackground");
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", strUsername));
                params.add(new BasicNameValuePair("password", strPassword));
                params.add(new BasicNameValuePair("codDoc", strCodDocumento));
                params.add(new BasicNameValuePair("email", strEmail));
                params.add(new BasicNameValuePair("cognome", strCognome));
                params.add(new BasicNameValuePair("nome", strNome));
                params.add(new BasicNameValuePair("nascita", strNascita));
                params.add(new BasicNameValuePair("localita", strLocalita));
                params.add(new BasicNameValuePair("indirizzo", strIndirizzo));
                params.add(new BasicNameValuePair("prov", strProvincia));
                params.add(new BasicNameValuePair("CAP", strCAP));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);

                // full json response
                Log.d("Login attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("User Created!", json.toString());
                    startActivity(intent);
                    finish();
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Login Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(Registrazione.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

    }
}