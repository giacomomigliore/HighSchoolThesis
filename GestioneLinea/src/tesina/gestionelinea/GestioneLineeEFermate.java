package tesina.gestionelinea;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import fragments.FragmentAggiungiFermata;
import fragments.FragmentAggiungiLinea;
import fragments.FragmentModificaLinea;


/**
 * Classe che contiene l'activity principale dell'applicazione.
 * Essa viene riepita con dei fragment selezionati dai tab.
 * Il listener dei tab è qui implementato.
 */
public class GestioneLineeEFermate extends Activity {

    // Tag usato nei log.
    private final String TAG = GestioneLineeEFermate.class.getSimpleName();
	
	 /**
     * Metodo chiamato all'apertura dell'activity.
     * Imposto i tab.
     * @param savedInstanceState se si creasse un punto di ripristino dell'activity quando si chiude.
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Si impostano i tab come metodo di navigazione tra i fragments.
		ActionBar actionBar = getActionBar();		 
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
        // Aggiungo ogni tab.
        Tab tab = actionBar.newTab();
        tab.setText("Aggiungi Linea");
        // TabListener è una inner class, si trova al fondo del file.
        tab.setTabListener(new TabListener<FragmentAggiungiLinea>(this,
        		"Aggiungi Linea", FragmentAggiungiLinea.class));
        actionBar.addTab(tab);
 
        tab = actionBar.newTab();
        tab.setText("Aggiungi Fermata");
        tab.setTabListener(new TabListener<FragmentAggiungiFermata>(this,
        		"Aggiungi Fermata", FragmentAggiungiFermata.class));
        actionBar.addTab(tab);
        
        tab = actionBar.newTab();
        tab.setText("Gestione linee");
        tab.setTabListener(new TabListener<FragmentModificaLinea>(this,
        		"Modifica Linea", FragmentModificaLinea.class));
        actionBar.addTab(tab);
        

    	// Inner class, si trova al fondo del file; aggiorna il database.
    	new DownloadLineeTratteEFermate().execute();
	}
	
	/**
     * Popola il menu; aggiunge gli elementI all'action bar se è presente.
     * @param menu il menu dell'applicazione in altro a sinistra.
     * @return boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gestione_linee_e_fermate, menu);
        return true;
    }

    /**
     * Questo metodo viene chiamato quando viene selezionato un
     * elemento dal menù classico in altro a sinistra.
     * @param item l'elemento selezionato.
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       
        if(item.getTitle().equals("Esci")){
        	Toast.makeText(this, getString(R.string.quit), Toast.LENGTH_LONG).show();
            finish();
            return true;
        }else if(item.getTitle().equals("Aggiornamento")){

            // Controllo che ci sia la connessione.
            if(((GlobalClass) getApplication()).isOnline())
	        	// Inner class, si trova al fondo del file; aggiorna il database.
	        	new DownloadLineeTratteEFermate().execute();
            else Toast.makeText(this, getString(R.string.controlloConessione), Toast.LENGTH_LONG).show();
        	return true;
        }
        return false;
    }
    
    
    // Per chiamare quello in FragmentAggiungiLineae FragmentModificaLinea:
    // http://stackoverflow.com/questions/17085729/startactivityforresult-from-a-fragment-and-finishing-child-activity-doesnt-c
    // http://stackoverflow.com/questions/6147884/onactivityresult-not-being-called-in-fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
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
            pDialog = new ProgressDialog(GestioneLineeEFermate.this);
            pDialog.setMessage("Aggiornamento del database...");
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
                DatabaseLocale db = new DatabaseLocale(GestioneLineeEFermate.this);
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
            Toast.makeText(GestioneLineeEFermate.this, "Aggiornamento completato", Toast.LENGTH_LONG).show();
            // Main.this.recreate();
        }
    }
	
    
    /**
     * Classe che si occupa di gestire un tab e gli eventi ad
     * esso associati: quando viene selezionato e viene deselezionato.
     * Si occupa quindi di mostrare o nascondere il relativo 
     * fragment. 
     */
	private class TabListener<T extends Fragment> implements ActionBar.TabListener {

        // Tag usato nei log.
        private final String TAG = TabListener.class.getSimpleName();
		
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;
		
		/**
		 * Costruttore.
		 * @param activity L'activity in cui si mostra il fragment.
		 * @param tag Il tag con coi viene identificato il fragment.
		 * @param classe Classe del fragment.
		 */
		public TabListener(Activity activity, String tag, Class<T> classe) {
		    mActivity = activity;
		    mTag = tag;
		    mClass = classe;
		}
		
		/**
		 * Quando viene selezionato un tab si mostra il relativo fragment.
		 */
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			
			// Se la classe del fragment non è ancora stata instanziata si crea
			// l'oggetto, poi, in ogni caso, lo si aggiunge alla UI.
		    if (mFragment == null) {
		        mFragment = Fragment.instantiate(mActivity, mClass.getName());
		        ft.add(android.R.id.content, mFragment, mTag);
		    } else {
		        ft.attach(mFragment);
		    }
		}
		
		/**
		 * Se viene deselezionato questo fragment, lo si 
		 * toglie dalla UI:
		 * http://developer.android.com/reference/android/app/FragmentTransaction.html#detach%28android.app.Fragment%29
		 */
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			
		    if (mFragment != null) {
		        ft.detach(mFragment);
		    }
		}
		
		/**
		 * Metodo chiamato quando si riselezione il medesimo tab.
		 * Non si fa nessuna operazione.
		 */
		public void onTabReselected(Tab tab, FragmentTransaction ft) {}
		
	}
}
