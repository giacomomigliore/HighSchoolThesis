package com.example.app;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.ActionBar.Tab;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.app.Fragment.FragmentControllaCorsa;
import com.example.app.Fragment.FragmentGestioneCorsa;
import com.example.app.Fragment.FragmentNuovaCorsa;


/**
 * Classe che contiene l'activity principale dell'applicazione.
 * Essa viene riempita con dei fragment selezionati dal tab menu.
 * Il listener del tab menu è qui implementato.
 */
public class Controllore extends Activity{
	
	// File delle preferenze dell'applicazione in cui verra salvata la tratta scelta dall'autista.
	public static final String PREFS_NAME = "MyPrefsFileControllore";
	
	// Dichiaro due variabili dove verranno salvati i dati passati con NFC.
	private String username;
	private String password;
	
	// Dichiaro la durata del biglietto.
	private String durataBiglietto="90";

    // Tag usato nei log.
    @SuppressWarnings("unused")
    private static final String TAG = Controllore.class.getSimpleName();

    /**
     * Metodo chiamato all'apertura dell'activity.
     * @param savedInstanceState se si creasse un punto di ripristino dell'activity quando si chiude.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Dichiaro l'action bar e la modalità di navigazione.
        ActionBar actionBar = getActionBar();        
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
 
        // Inserisco tutti i nuovi tab dentro all'action bar.        
        String label1 = getResources().getString(R.string.titleNuovaCorsa);
        Tab tab = actionBar.newTab();
        tab.setText(label1);
        TabListener<FragmentNuovaCorsa> tl = new TabListener<FragmentNuovaCorsa>(this,
                label1, FragmentNuovaCorsa.class);
        tab.setTabListener(tl);
        actionBar.addTab(tab);
        
        String label2 = getResources().getString(R.string.titleControlloCorsa);
        tab = actionBar.newTab();
        tab.setText(label2);
        TabListener<FragmentControllaCorsa> tl2 = new TabListener<FragmentControllaCorsa>(this,
                label2, FragmentControllaCorsa.class);
        tab.setTabListener(tl2);
        actionBar.addTab(tab);
        
        String label3 = getResources().getString(R.string.titleGestioneCorsa);
        tab = actionBar.newTab();
        tab.setText(label3);
        TabListener<FragmentGestioneCorsa> tl3 = new TabListener<FragmentGestioneCorsa>(this,
                label3, FragmentGestioneCorsa.class);
        tab.setTabListener(tl3);
        actionBar.addTab(tab);

   	 	// Leggo dal file delle preferenze e setto la linea selezionata precedentemente, se
   	 	// nel file non ce nulla setta null.
   	 	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);        

        /*
        Mi serve per prevenire errori lato .php. Se l'autista vende un biglietto tramite
        il Fragment NuovaCorsa senza aver prima settatto questa variabile, l'sql
        nella pagina .php andrebbe in errore. Facendo così posso fare il controlli che
        questa variabile sia null o no prima che il Fragment GestioneCorsa(dove si
        inizializza questa variabile) venga creato. Dopo che viene creato non vi sono più
        problemi simili.
        */
        FragmentGestioneCorsa.setLineaSelezionata(settings.getString("codLinea", null));
        
    }

    /**
     * Questo metodo viene chiamato quando viene selezionato un altro
     * elemento dal menu tab.
     * @param position posizione dell'elemento selezionato.
     * @param id id dell'elemento selezionato.
     * @return boolean.
     */
    public boolean onNavigationItemSelected(int position, long id) {

        // A seconda dell'elemento selezionato viene impostato un nuovo
        // fragment all'activity.
        switch (position){

            // FragmentControllaCorsa
            case 0:
                getFragmentManager().beginTransaction()
                        // fragment_container è il layout dell'activity disposto a contenere i fragments.
                        .replace(R.id.fragment_container, new FragmentControllaCorsa())
                        .commit();
                return true;

            // FragmentNuovaCorsa
            case 1:
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FragmentNuovaCorsa())
                        .commit();
                return true;

            // FragmentGestioneCorsa
            case 2:
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FragmentGestioneCorsa())
                        .commit();
                return true;
        }

        return false;
    }

    /**
     * Popola il menu; aggiunge gli elementi all'action bar se è presente.
     * @param menu il menu dell'applicazione in altro a sinistra.
     * @return boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.controllo_convalida, menu);
        return true;
    }

    /**
     * Questo metodo viene chiamato quando viene selezionato un altro
     * elemento dal menù classico in altro a sinistra.
     * @param item l'elemento selezionato.
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // A seconda dell'elemento selezionato viene eseguita la rispettiva azione.
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
    
    /**
     * Classe il cui compito è scaricare tre tabelle dal database sul 
     * server e salvarle nelle 3 classi identiche nel database locale.
     * Le tre tabelle sono Linea, Tratta e Fermata.
     * Questo AsyncTask viene chiamato dal menu.
     */
    class DownloadLineeTratteEFermate extends AsyncTask<Void, Void, String>{

        // Tag usato nei log.
        private final String TAG = DownloadLineeTratteEFermate.class.getSimpleName();

        // Aggiorno DOWNLOAD_LINEE_URL.
        private String DOWNLOAD_LINEE_URL_LINEE = GlobalClass.getDominio() + "Download/linee.php";
        private String DOWNLOAD_LINEE_URL_FERMATE = GlobalClass.getDominio() + "Download/fermate.php";
        private String DOWNLOAD_LINEE_URL_TRATTE = GlobalClass.getDominio() + "Download/tratte.php";

        // Dialog che impone l'attesa da parte dell'utente.
        private ProgressDialog pDialog = null;
        
        /**
         * Metodo che viene eseguito prima di doInBackground.
         * Avvio il ProgressDialog.
         */
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            
            // Imposto il ProgressDialog e lo avvio.
            pDialog = new ProgressDialog(Controllore.this);
            pDialog.setMessage("Aggiornamento database locale...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Questo metodo esegue il download.
         */
        @Override
        protected String doInBackground(Void... voids) {
        	
        	// Dichiaro quest quattro variabili all'inizio perchè 
        	// vengono utilizzate 3 volte ciascuna, ona volta per tabella.
            JSONArray arrayJson = null;
            JSONObject json = null;
            ContentValues valori = null;
            JSONParser jParser = new JSONParser();

            try {                
            	
            	// Apro la connessione con il database locale.
                DatabaseLocale db = new DatabaseLocale(Controllore.this);
                SQLiteDatabase dbScrivibile = db.getWritableDatabase();
                
                
                // SCARICO LE FERMATE
                json = jParser.getJSONFromUrl(DOWNLOAD_LINEE_URL_FERMATE);
                // Cancello la tabella fermata in locale.
                dbScrivibile.delete(DatabaseLocale.getTableNameFermata(), null, null);
                
                if(json.getJSONArray(GlobalClass.getTagPosts()).length() > 0){
                    arrayJson = json.getJSONArray(GlobalClass.getTagPosts());

                    // Aggingo un record alla volta.
                    for (int i = 0; i < arrayJson.length(); i++) {
                        // Ottengo la singola fermata.
                        JSONObject jobj = arrayJson.getJSONObject(i);
                        // Inserimento nel db locale della tabella.
                        valori = new ContentValues();
                        valori.put(DatabaseLocale.getTagCodiceFermata(), String.valueOf(jobj.getString(DatabaseLocale.getTagCodiceFermata())));
                        valori.put(DatabaseLocale.getTagNomeFermata(), jobj.getString(DatabaseLocale.getTagNomeFermata()));
                        dbScrivibile.insert(DatabaseLocale.getTableNameFermata(), null, valori);
                    }                	
                }else return "Nessuna fermata nel database";  // Se non ci sono fermate non ci sono neanche linee.
                
            	
            	// SCARICO LE LINEE
                json = jParser.getJSONFromUrl(DOWNLOAD_LINEE_URL_LINEE);
                dbScrivibile.delete(DatabaseLocale.getTableNameLinea(), null, null);
                
                if(json.getJSONArray(GlobalClass.getTagPosts()).length() > 0){
                    arrayJson = json.getJSONArray(GlobalClass.getTagPosts());

                    for (int i = 0; i < arrayJson.length(); i++) {
                        JSONObject jobj = arrayJson.getJSONObject(i);
                        valori = new ContentValues();
                        valori.put(DatabaseLocale.getTagCodiceLinea(), String.valueOf(jobj.getString(DatabaseLocale.getTagCodiceLinea())));
                        valori.put(DatabaseLocale.getTagNomeLinea(), jobj.getString(DatabaseLocale.getTagNomeLinea()));
                        valori.put(DatabaseLocale.getTagCodiceCapolinea(), jobj.getString(DatabaseLocale.getTagCodiceCapolinea()));
                        dbScrivibile.insert(DatabaseLocale.getTableNameLinea(), null, valori);
                        Log.d(TAG, valori.toString());
                    }                	
                }else return "Nessuna linea nel database";  // Se non ci sono linee non ci sono neanche tratte.
                
                
                // SCARICO LE TRATTE
                json = jParser.getJSONFromUrl(DOWNLOAD_LINEE_URL_TRATTE);
                dbScrivibile.delete(DatabaseLocale.getTableNameTratta(), null, null);
                
                if(json.getJSONArray(GlobalClass.getTagPosts()).length() > 0){
                    arrayJson = json.getJSONArray(GlobalClass.getTagPosts());

                    for (int i = 0; i < arrayJson.length(); i++) {
                        JSONObject jobj = arrayJson.getJSONObject(i);
                        valori = new ContentValues();
                        valori.put(DatabaseLocale.getTagCodiceLinea(), String.valueOf(jobj.getString(DatabaseLocale.getTagCodiceLinea())));
                        valori.put(DatabaseLocale.getTagCodiceFermata(), String.valueOf(jobj.getString(DatabaseLocale.getTagCodiceFermata())));
                        valori.put(DatabaseLocale.getTagSuccessiva(), jobj.getString(DatabaseLocale.getTagSuccessiva()));
                        dbScrivibile.insert(DatabaseLocale.getTableNameTratta(), null, valori);
                        Log.d(TAG, valori.toString());
                    }                	
                }else return "Nessuna tratta nel database";
                
                // Chiudo la connessione con il database locale.
                dbScrivibile.close();
                db.close();
            } catch (JSONException e) {
            	
            	// Se viene scatenato un errore lo riporto all'utente e nei log.
                Log.e(TAG, "Error parsing data " + e.toString());
            	return "Errore nell'aggiornamento dei dati";
            }
            return "Aggiornamento completato.";
        }

        /**
         * Metodo eseguito dopo doInBackground. 
         * Chiudo il ProgressDialog.
         */
        @Override
        protected void onPostExecute(String valoreRitornato) {
            super.onPostExecute(valoreRitornato);
            pDialog.dismiss();
        	Toast.makeText(Controllore.this, valoreRitornato , Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Controlla se l'app è partita per l'arrivo di un messaggio NFC.
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }
    
    // Intent che prende il messaggio nfc.
    @Override
    public void onNewIntent(Intent intent) {
    	// Dopo questo comando viene chiamato onResume.
        setIntent(intent);
    }

    /**
     * Stampa il messaggio dell'NFC in un Toast.
     */
    void processIntent(Intent intent) {
    	String text="messaggio non � arrivato";
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // Solo un messaggio inviato.
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // Il record 0 contiene il MIME type, il record 1 è l' AAR, se c'è.
        text=new String(msg.getRecords()[0].getPayload());
        username=text.split("/")[0];
        password=text.split("/")[1];
        // Avvio l'AsyncTask.
        new UploadNuovaCorsa(this, username, password, FragmentGestioneCorsa.getLineaSelezionata(), durataBiglietto, false).execute();
    }
	
    
    /**
     * Classe che si occupa di gestire un tab e gli eventi ad
     * esso associati: quando viene selezionato e viene deselezionato.
     * Si occupa quindi di mostrare o nascondere il relativo 
     * fragment. 
     */
    private class TabListener<T extends Fragment> implements ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

        // Tag usato nei log.
        private final String TAG = TabListener.class.getSimpleName();
		
		
		/**
		 * Costruttore.
		 * @param activity L'activity in cui si mostra il fragment.
		 * @param tag Il tag con coi viene identificato il fragment.
		 * @param classe Classe del fragment.
		 */
	    public TabListener(Activity activity, String tag, Class<T> clz) {
	        mActivity = activity;
	        mTag = tag;
	        mClass = clz;
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
	            // Detach the fragment, because another one is being attached
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


