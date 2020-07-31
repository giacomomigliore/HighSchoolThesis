package fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utente.JSONParser;

import com.utente.R;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * La classe FragmentHome è un Fragment che ha per layout il file
 * fragment_home.xml. In esso sono rappresentate le informazioni
 * principali.
 * --
 * FragmentHome class is a Fragment that has as layout the file
 * fragment_home.xml. Here there are the main informations.
 */
public class FragmentHome extends Fragment{
	
	//dichiaro la prigress bar
	private ProgressBar mProgressBar;
	
	// File delle preferenze dell'applicazione.
	// Preferences file.
	public static final String PREFS_NAME = "MyPrefsFile";
	
	// Dichiaro tutti gli oggetti presenti nella schermata Home.
	// UI objects.
	private TextView txtUtente=null;
	private TextView txtBiglietto=null;
	private TextView txtDataUltimaConvalida=null;
	private TextView txtOraUltimaConvalida=null;
	private TextView txtTempoRimasto=null;

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
 
        return (LinearLayout) inflater.inflate(R.layout.fragment_home, container, false);
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
		super.onViewCreated(view, savedInstanceState);  
		
		//blocco rotazione
		this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Inizializzo tutti gli oggetti della schermata Home
		txtUtente=(TextView)getActivity().findViewById(R.id.txtUtente);
		txtBiglietto=(TextView)getActivity().findViewById(R.id.txtBigliettiRimasti);
		txtDataUltimaConvalida=(TextView)getActivity().findViewById(R.id.DataUltimaConvalida);
		txtOraUltimaConvalida=(TextView)getActivity().findViewById(R.id.oraUltimaConvalida);
		txtTempoRimasto=(TextView)getActivity().findViewById(R.id.txtTempoRimasto);
		
		//inizializzo la progress bar
		mProgressBar=(ProgressBar)getActivity().findViewById(R.id.progressBar);
		
		// Restore preferences
		SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
		// Leggo dal file delle preferenze il nome dell'utente e lo scrivo nella TextView della Home.
		// Read the name of the user from the preferencee file and write it in the Home.
		txtUtente.setText("Ciao, " + settings.getString("username", "sconosciuto"));  
		
		if(isNetworkAvailable()){
			new DownloadHome(settings.getString("username", "sconosciuto")).execute();
		}
	}
	
	//controllo se la connessione ad internet � disponibile
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		         = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
    
	/**
	 * Il compito di questa classe è fare l'inserire nel database i dati
	 * relativi all'acquisto di biglietti o abbonamento da parte 
	 * dell'utente.
	 */
	public class DownloadHome extends AsyncTask<Void, Void, String> {
		
		private String username;
	    private String DOWNLOAD_HOME;
		
	    public DownloadHome(String username){
	    	this.username=username;
	    }
		
		private ProgressDialog pDialog;	
		
	    // Tag usato nei log.
	    private final String TAG = DownloadHome.class.getSimpleName();	
	
	    // Tag(ID) del responso Json restituito dallo script php.
	    private static final String TAG_MESSAGE = "message";
	    
	    //dichiaro le variabili da scaricare
	    private String biglietto, durata, data, ora, tempoRimasto;
	
	    /**
	     * Metodo che viene eseguito prima di doInBackground.
	     * Avvio il ProgressDialog.
	     */
		@Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        pDialog = new ProgressDialog(getActivity());
	        pDialog.setMessage("Caricamento home...");
	        // Dura un tempo indeterminato.
	        pDialog.setIndeterminate(false);
	        // E' cancellabile dal tasto Back.
	        pDialog.setCancelable(true);
	        pDialog.show();
	        
	        DOWNLOAD_HOME = "http://www.letsmove.altervista.org/Download/homePage.php";
	    }
	
	    @Override
	    protected String doInBackground(Void... voids) {
	    	//dichiaro l'oggetto json
	    	JSONParser jsonParser = new JSONParser();
	    	
	    	// Creazione dei parametri.
            // Building Parameters.
            List<NameValuePair> params = new ArrayList<NameValuePair>();  //http://stackoverflow.com/a/17609232/2337094 Namevaluepair
            params.add(new BasicNameValuePair("username", username));
            
            // Avvio richiesta.
            // Start the request.
            JSONObject json = jsonParser.makeHttpRequest(DOWNLOAD_HOME, "POST", params);
	    	
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
                    biglietto = jobj.getString("biglietti");
                    durata = jobj.getString("durata");
                    try{
                    	data = jobj.getString("data").split(" ")[0];
                        ora = jobj.getString("data").split(" ")[1];
                    }catch(ArrayIndexOutOfBoundsException e){
        				data = "-";
        				ora = "-";
        			}
                    
                    tempoRimasto = jobj.getString("tempoRimasto");

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
                Toast.makeText(getActivity().getApplicationContext(), "Errore nella connessione con il Database.", Toast.LENGTH_LONG).show();
               Log.e(TAG, "Errore nella connessione con il Database.");
            } else {
                txtBiglietto.setText(biglietto);
                txtDataUltimaConvalida.setText(data);
                txtOraUltimaConvalida.setText(ora);
                txtTempoRimasto.setText("  "+tempoRimasto+"  ");
                
                mProgressBar.setMax(Integer.parseInt(durata));
                mProgressBar.setProgress(Integer.parseInt(tempoRimasto));
                
                Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_LONG).show();
                Log.e(TAG, response);
            }
	
	        
	    }
	}
	
}