package utente;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.utente.R;

import fragments.FragmentHome;
import fragments.FragmentOrari;
import fragments.FragmentRicarica;

public class Main extends Activity implements CreateNdefMessageCallback{
	
	// File in cui viene salvato se il cliente � loggato o meno insieme al suo nickname.
	// File in which is stored the nickname and if the user is logged or not.
	public static final String PREFS_NAME = "MyPrefsFile";
	
	//dichiaro l'intent per cambiare l'activity
	private Intent intent=null;	
	
	// Adapter for Nfc.
	private NfcAdapter mNfcAdapter;

    /**
     * Metodo chiamato all'apertura dell'activity.
     * Method called at the creation of the activity.
     * @param savedInstanceState se si creasse un punto di ripristino dell'activity quando si chiude.
     *                           if there has been created a reset point of the activity when it's closed.
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Se non ce internet manda un alert dialog con l'avviso.
		// If there is no internet is alerts the user.
		if(!isNetworkAvailable()){
			AlertDialog.Builder miaAlert = new AlertDialog.Builder(this);
			miaAlert.setTitle("Attenzione!");
			miaAlert.setMessage("Non sei collegato ad internet. Alcune funzionalit� del programma potrebbero non essere disponibili. Controlla la tua connessione.");
			miaAlert.setNeutralButton("OK", null);
			AlertDialog alert = miaAlert.create();
			alert.show();
		}
		
		//comincia la parte nfc di invio
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
        }else{
        	// Register callback
            mNfcAdapter.setNdefPushMessageCallback(this, this);
        }
        //finisce qui
        
        //dichiaro l'action bar che � quella principale di tutto il programma
		
        ActionBar actionBar = getActionBar();
        
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
        //qui inserisco tutti i nuovi tab dentro all'action bar
        
        String label1 = getResources().getString(R.string.home);
        Tab tab = actionBar.newTab();
        //tab.setText(label1);
        tab.setIcon(R.drawable.casa);
        TabListener<FragmentHome> tl = new TabListener<FragmentHome>(this,
                label1, FragmentHome.class);
        tab.setTabListener(tl);
        actionBar.addTab(tab);
 
        String label2 = getResources().getString(R.string.orari);
        tab = actionBar.newTab();
        //tab.setText(label2);
        tab.setIcon(R.drawable.orario);
        TabListener<FragmentOrari> tl2 = new TabListener<FragmentOrari>(this,
                label2, FragmentOrari.class);
        tab.setTabListener(tl2);
        actionBar.addTab(tab);
        
        String label3 = getResources().getString(R.string.ricarica);
        tab = actionBar.newTab();
        //tab.setText(label3);
        tab.setIcon(R.drawable.cash);
        TabListener<FragmentRicarica> tl3 = new TabListener<FragmentRicarica>(this,
                label3, FragmentRicarica.class);
        tab.setTabListener(tl3);
        actionBar.addTab(tab);
        
        
        //qua finisce la dichiarazione dell'action bar
             				
	}
	
	
	//uso questo metodo perch� l'utente ogni volta che apre l'app deve sapere che non ce internet
	@Override
	protected void onResume() {
		super.onResume();
		//se non ce internet manda un alert dialog con l'avvertenza
		if(!isNetworkAvailable()){
			AlertDialog.Builder miaAlert = new AlertDialog.Builder(this);
			miaAlert.setTitle("Attenzione!");
			miaAlert.setMessage("Non sei collegato ad internet. Alcune funzionalita del programma potrebbero non essere disponibili. Controlla la tua connessione.");
			miaAlert.setNeutralButton("OK", null);
			AlertDialog alert = miaAlert.create();
			alert.show();
		}
	}

	private class TabListener<T extends Fragment> implements ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

	    /**
	     * Constructor used each time a new tab is created.
	     *
	     * @param activity
	     *            The host Activity, used to instantiate the fragment
	     * @param tag
	     *            The identifier tag for the fragment
	     * @param clz
	     *            The fragment's Class, used to instantiate the fragment
	     */
	    public TabListener(Activity activity, String tag, Class<T> clz) {
	        mActivity = activity;
	        mTag = tag;
	        mClass = clz;
	    }

	    public void onTabSelected(Tab tab, FragmentTransaction ft) {
	        // Check if the fragment is already initialized
	        if (mFragment == null) {
	            // If not, instantiate and add it to the activity
	            mFragment = Fragment.instantiate(mActivity, mClass.getName());
	            ft.add(android.R.id.content, mFragment, mTag);
	        } else {
	            // If it exists, simply attach it in order to show it
	            ft.attach(mFragment);
	        }
	    }

	    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	        if (mFragment != null) {
	            // Detach the fragment, because another one is being attached
	            ft.detach(mFragment);
	        }
	    }

	    public void onTabReselected(Tab tab, FragmentTransaction ft) {
	        // User selected the already selected tab. Usually do nothing.
	    }
	}
	
	//questa � la parte che viene eseguita per mandare il messaggio
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		//prendo l'username dal file delle preferenze
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		//SharedPreferences.Editor editor = settings.edit();
		
		String user=settings.getString("username","errore");
		String pass=settings.getString("password","errore");
		
		String text = (user+"/"+pass);
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { NdefRecord.createMime(
                		"application/vnd.com.example.app", text.getBytes())
         /**
          * The Android Application Record (AAR) is commented out. When a device
          * receives a push with an AAR in it, the application specified in the AAR
          * is guaranteed to run. The AAR overrides the tag dispatch system.
          * You can add it back in to guarantee that this
          * activity starts when receiving a beamed message. For now, this code
          * uses the tag dispatch system.
          */
          //,NdefRecord.createApplicationRecord("com.example.android.beam")
        });
        return msg;
	}
	

	//controllo se la connessione ad internet � disponibile
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}


    /**
     * Popola il menu; aggiunge gli elementI all'action bar se è presente.
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu il menu dell'applicazione in altro a sinistra.
     *             the menu of the application on the top-left.
     * @return boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.controllo, menu);
        return true;
    }

    /**
     * Questo metodo viene chiamato quando viene selezionato un altro
     * elemento dal menù classico in altro a sinistra.
     * --
     * This method is triggered when it is selected an other element
     * from classic menu on the top-left.
     * @param item l'elemento selezionato.
     *             the selected element.
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // A seconda dell'elemento selezionato viene eseguita la rispettiva azione.
        // Depending on the element selected it is executed the respective commands.
        switch(item.getItemId()){

            // Esci, finisce l'activity.
            // Quit, ends the activity.
            case R.id.quit:
                finish();
                return true;
            case R.id.avanzate:
            	startActivity(new Intent(this, ImpostazioniAvanzate.class));
            	return true;
            case R.id.account:
            	startActivity(new Intent(this, GestioneAccount.class));
            	return true;	
            case R.id.aggiornamento:
            	if(isNetworkAvailable()){
            		new DownloadLineeTratteEFermate().execute();
            	}else{
            		Toast.makeText(getApplicationContext(), "Controlla la tua connessione ad internet", Toast.LENGTH_LONG).show();
            	}
            	return true;
            case R.id.logout:
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
            	return true;
        }
        return false;
    }
	
    /**
     * Classe il cui compito è scaricare tutte le linee presenti sul db.
     * --
     * The aim of this class is to download all the paths that are on the db.
     */
    class DownloadLineeTratteEFermate extends AsyncTask<Void, Void, Void>{

        // Tag usato nei log.
        // Tag used into logs.
        private final String TAG = DownloadLineeTratteEFermate.class.getSimpleName();

        //testing on Emulator:
        private String DOWNLOAD_LINEE_URL_LINEE;
        private String DOWNLOAD_LINEE_URL_FERMATE;
        private String DOWNLOAD_LINEE_URL_TRATTE;

        //JSON IDS:
        private static final String TAG_POSTS = "posts";
        private ProgressDialog pDialog;
        private JSONArray arrayJson = null;
        private JSONObject json = null;
        private ContentValues valori = null;
        
        /**
         * Metodo che viene eseguito prima di doInBackground.
         * Avvio il ProgressDialog.
         * --
         * Method executed before of doInBackgroung.
         * I start the ProgressDialog.
         */
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(Main.this);
            pDialog.setMessage("Download orari...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            // Aggiorno DOWNLOAD_LINEE_URL.
            // Update DOWNLOAD_LINEE_URL.
            DOWNLOAD_LINEE_URL_LINEE = "http://www.letsmove.altervista.org//Download/linee.php";
            DOWNLOAD_LINEE_URL_FERMATE = "http://www.letsmove.altervista.org/Download/fermate.php";
            DOWNLOAD_LINEE_URL_TRATTE = "http://www.letsmove.altervista.org/Download/tratte.php";
        }

        /**
         * Questo metodo esegue il download.
         * This method executes the download.
         * @param voids
         * @return
         */
        @Override
        protected Void doInBackground(Void... voids) {

            // Richiesta alla pagina .php.
            // Request to the .php page.
            JSONParser jParser = new JSONParser();

            try {                
                DatabaseLocale db = new DatabaseLocale(Main.this);
                SQLiteDatabase dbScrivibile = db.getWritableDatabase();
                
                // SCARICO LE FERMATE
                json = jParser.getJSONFromUrl(DOWNLOAD_LINEE_URL_FERMATE);
                // Prendo l'array del campo posts.
                // I take the array of posts part.
                arrayJson = json.getJSONArray(TAG_POSTS);

                // Scrivo il codice del db nel try perchè così, se il json scatena
                // un errore nella linea precedente, non perdo tempo ad aprire un
                // db non usato.
                dbScrivibile.delete(DatabaseLocale.getTableNameFermata(), null, null);

                for (int i = 0; i < arrayJson.length(); i++) {
                    // Ottengo la singola corsa.
                    // Obtain the single path.
                    JSONObject jobj = arrayJson.getJSONObject(i);
                    
                    valori = new ContentValues();
                    valori.put(DatabaseLocale.getTagCodiceFermata(), String.valueOf(jobj.getString(DatabaseLocale.getTagCodiceFermata())));
                    valori.put(DatabaseLocale.getTagNomeFermata(), jobj.getString(DatabaseLocale.getTagNomeFermata()));
                    dbScrivibile.insert(DatabaseLocale.getTableNameFermata(), null, valori);
                    Log.d(TAG + "fermate", valori.toString());
                }
            	
            	// SCARICO LE LINEE
                json = jParser.getJSONFromUrl(DOWNLOAD_LINEE_URL_LINEE);
                // Prendo l'array del campo posts.
                // I take the array of posts part.
                arrayJson = json.getJSONArray(TAG_POSTS);

                // Scrivo il codice del db nel try perchè così, se il json scatena
                // un errore nella linea precedente, non perdo tempo ad aprire un
                // db non usato.
                dbScrivibile.delete(DatabaseLocale.getTableNameLinea(), null, null);

                for (int i = 0; i < arrayJson.length(); i++) {
                    // Ottengo la singola corsa.
                    // Obtain the single path.
                    JSONObject jobj = arrayJson.getJSONObject(i);

                    valori = new ContentValues();
                    valori.put(DatabaseLocale.getTagCodiceLinea(), String.valueOf(jobj.getString(DatabaseLocale.getTagCodiceLinea())));
                    valori.put(DatabaseLocale.getTagNomeLinea(), jobj.getString(DatabaseLocale.getTagNomeLinea()));
                    valori.put(DatabaseLocale.getTagCodiceCapolinea(), jobj.getString(DatabaseLocale.getTagCodiceCapolinea()));
                    valori.put(DatabaseLocale.getTagOrariAndata(), jobj.getString(DatabaseLocale.getTagOrariAndata()));
                    valori.put(DatabaseLocale.getTagOrariRitorno(), jobj.getString(DatabaseLocale.getTagOrariRitorno()));
                    dbScrivibile.insert(DatabaseLocale.getTableNameLinea(), null, valori);
                    Log.d(TAG + "linee", valori.toString());
                }
                
                // SCARICO LE TRATTE
                json = jParser.getJSONFromUrl(DOWNLOAD_LINEE_URL_TRATTE);
                // Prendo l'array del campo posts.
                // I take the array of posts part.
                arrayJson = json.getJSONArray(TAG_POSTS);

                // Scrivo il codice del db nel try perchè così, se il json scatena
                // un errore nella linea precedente, non perdo tempo ad aprire un
                // db non usato.
                dbScrivibile.delete(DatabaseLocale.getTableNameTratta(), null, null);

                for (int i = 0; i < arrayJson.length(); i++) {
                    // Ottengo la singola corsa.
                    // Obtain the single path.
                    JSONObject jobj = arrayJson.getJSONObject(i);

                    valori = new ContentValues();
                    valori.put(DatabaseLocale.getTagCodiceLinea(), String.valueOf(jobj.getString(DatabaseLocale.getTagCodiceLinea())));
                    valori.put(DatabaseLocale.getTagCodiceFermata(), String.valueOf(jobj.getString(DatabaseLocale.getTagCodiceFermata())));
                    valori.put(DatabaseLocale.getTagSuccessiva(), jobj.getString(DatabaseLocale.getTagSuccessiva()));
                    dbScrivibile.insert(DatabaseLocale.getTableNameTratta(), null, valori);
                    Log.d(TAG + "Tratte", valori.toString());
                }
                db.close();
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing data " + e.toString());
            }
            return null;
        }

        /**
         * Metodo eseguito dopo doInBackground. Chiudo il ProgressDialog.
         * --
         * Method executed after doInBackground. I Close the ProgressDialog.
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            FragmentOrari.aggiorna();
            Toast.makeText(Main.this, "Aggiornamento completato", Toast.LENGTH_LONG).show();
            // Main.this.recreate();
        }
    }

}
