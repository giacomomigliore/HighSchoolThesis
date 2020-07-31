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
import tesina.gestionelinea.InserimentoOrari;
import tesina.gestionelinea.JSONParser;
import tesina.gestionelinea.Query;
import tesina.gestionelinea.R;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;


/**
 * La classe FragmentModificaLinea è un Fragment che permette di prendere
 * una linea già presente nel database, e modificare le fermate che essa
 * percorre.
 */
public class FragmentModificaLinea extends Fragment {

    private Context context = null;
	private Button btnConfermaModifica = null;
	private Button btnAggiungi = null;
	private Button btnCambiaLinea = null;
    private Button btnInserisciOrari = null;
	private EditText edtCambiaNome = null;
	private EditText edtCambiaCodice = null;
    private ListView listViewModifica = null;
    
    // List view mostra e adapter.
    private ListView listViewMostra = null;
    private ArrayAdapter<String> adapterMostra = null;
    
    // Lista con le linee appartenenti alla fermata e relativo adapter.
    private ArrayList<String> fermateDellaLinea = null;
    private AdapterModificaLinea adapterFermateDellaLinea = null;
    
    // Inflater per il footer della lista
    private LayoutInflater li = null;
	
	// Oggetto che contiene query al db locale.
	private Query richiediAlDatabase = null;

    // Tag usato nei log.
    @SuppressWarnings("unused")
    private static final String TAG = FragmentModificaLinea.class.getSimpleName();
    
    // Salva il codice originale, serve per eliminare la linea.
    private String codiceLinea = null;
    
    // La posizione nella listviewmostra dell'elemento da modificare.
    private int posizione = 0;
    
    // Orari della fermata.
    private String orariAndata = null;
    private String orariRitorno = null;

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
        // Inflate the layout for this fragment.
        return inflater.inflate(R.layout.fragment_modifica_linea, container, false);
    }

    /**
     * Metodo chiamato appena dopo che è stato istanziato il layout. Ottengo gli oggetti in esso presenti.
     * @param view Tutte le view nel layout.
     * @param savedInstanceState Se si creasse un punto di ripristino dell'activity quando si chiude.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();		
		richiediAlDatabase = new Query(context);
		li = LayoutInflater.from(context);
		// Carico tutte le linee dal database locale.
        // Imposto la listView che mostra le linee.
        listViewMostra = (ListView) getView().findViewById(R.id.listViewModificaLineaMostraLinee);
        adapterMostra = new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1);
        adapterMostra.addAll(richiediAlDatabase.caricaLinee());
        listViewMostra.setAdapter(adapterMostra);
        listViewMostra.setEmptyView(getView().findViewById(R.id.empty_list_item_modifica));
        // Imposto la ListView; ci aggiungo come footer i bottoni al fondo del layout:
        // aggiungi una fermata e conferma linea.
        listViewModifica = (ListView) getView().findViewById(R.id.listViewModificaLinea);
        LinearLayout footer = (LinearLayout) li.inflate(R.layout.footer_list_view_modifica_linea, null);
        btnConfermaModifica = (Button) footer.findViewById(R.id.btnConfermaModifica);
        btnAggiungi = (Button) footer.findViewById(R.id.btnAggiungi);
        listViewModifica.addFooterView(footer);
        // Aggiungo come header ciò che mi permette di modifica nome, codice linea
        // e di cambiare linea che si sta modificando.
        LinearLayout header = (LinearLayout) li.inflate(R.layout.header_list_view_modifica_linea, null);
        listViewModifica.addHeaderView(header);
        btnCambiaLinea = (Button) header.findViewById(R.id.btnCambiaLinea);
        edtCambiaCodice = (EditText) header.findViewById(R.id.edtCambiaCodice);
        edtCambiaNome = (EditText) header.findViewById(R.id.edtCambiaNome);
        btnInserisciOrari = (Button) header.findViewById(R.id.btnInserisciOrariGestioneLinea);
        
        listViewMostra.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				posizione = position;
				
				String codLinea = ((String) listViewMostra.getItemAtPosition(posizione)).split(" Cod: ")[1];
        		// Setto l'adapter alla ListView.
    			fermateDellaLinea = richiediAlDatabase.caricaFermateDellaLinea(codLinea);
    			String[] orari = new String[2];
    			orari = richiediAlDatabase.caricaOrariDellaLinea(codLinea);
    			orariAndata = orari[0];
    			orariRitorno = orari[1];
    			adapterFermateDellaLinea = new AdapterModificaLinea(context, R.layout.elemento_modifica_linea, fermateDellaLinea);
    			listViewModifica.setAdapter(adapterFermateDellaLinea);
    			// Rendo visibile la ListView.
        		listViewModifica.setVisibility(ListView.VISIBLE);
        		// Inizializzo i valori nell'header: nome e codice.
        		edtCambiaNome.setText(((String) listViewMostra.getItemAtPosition(posizione)).split(" - Cod: ")[0]);
        		edtCambiaCodice.setText(((String) listViewMostra.getItemAtPosition(posizione)).split(" - Cod: ")[1]);
        		// Salvo il codice della linea cos� posso poi eliminarla.
				codiceLinea = ((String) listViewMostra.getItemAtPosition(posizione)).split(" Cod: ")[1];
        		// Disabiliti gli oggetti per la ricerca della linea.
        		listViewMostra.setVisibility(ListView.GONE);
				
			}
		});
        
        btnInserisciOrari.setOnTouchListener( new BottoneCliccato( btnInserisciOrari.getAlpha() ));	    
        btnInserisciOrari.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// Controllo che non si stiano modificando fermate
				if(adapterFermateDellaLinea.getPosizioneDellaFermataCheSiModifica() == -1){
					Intent intent = new Intent(getActivity(), InserimentoOrari.class);
					intent.putExtra("orariAndata", orariAndata == null ? "" : orariAndata);
					intent.putExtra("orariRitorno", orariRitorno == null ? "" : orariRitorno);
					intent.putExtra("nomeLinea", edtCambiaNome.getText().toString());
					intent.putExtra("codiceLinea", edtCambiaCodice.getText().toString());
					intent.putExtra("numFermate", fermateDellaLinea.size());
					for(int indice = 0; indice < fermateDellaLinea.size(); indice++)
	                	intent.putExtra("fermata" + indice, fermateDellaLinea.get(indice));
					startActivityForResult(intent, 2);
				}
				else Toast.makeText(context, "Confermare la fermata che si sta tentando di modificare.", Toast.LENGTH_LONG).show();
			}
		});
        
        
        // Aggiunge una fermata alla ListView.
        btnAggiungi.setOnTouchListener( new BottoneCliccato( btnAggiungi.getAlpha() ));
        btnAggiungi.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {

				// Controllo che non stia modificando già un'altra fermata.
				if(adapterFermateDellaLinea.getPosizioneDellaFermataCheSiModifica() == -1)
					// Se fermateDellaLinea fosse vuoto andrebbe in errore la prossima if,
					// quindi si aggiunge senza ulteriori controlli.
					if(!fermateDellaLinea.isEmpty())
						// Se l'ultima riga è ancora da inserire.
						if(!fermateDellaLinea.get(fermateDellaLinea.size() - 1).equals("Nuova fermata")){
							// Codice uguale all'else sottostante.
							fermateDellaLinea.add("Nuova fermata");
							adapterFermateDellaLinea.notifyDataSetChanged();
						}else Toast.makeText(context, "Inserire prima la fermata appena aggiunta.", Toast.LENGTH_LONG).show();
					else{
						fermateDellaLinea.add("Nuova fermata");
						adapterFermateDellaLinea.notifyDataSetChanged();					
					}
				else Toast.makeText(context, "Confermare la fermata che si sta tentando di modificare.", Toast.LENGTH_LONG).show();
			}
		});
        
        
        // Bottone che permette di caricare la linea aggiornata.
        btnConfermaModifica.setOnTouchListener( new BottoneCliccato( btnConfermaModifica.getAlpha() ));
        btnConfermaModifica.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				

                GlobalClass gc= (GlobalClass)getActivity().getApplication();
                // Controllo che ci sia la connessione.
                if(gc.isOnline())
					// Se l'ultima fermata è già stata inserita.
					if(!fermateDellaLinea.get(fermateDellaLinea.size() - 1).equals("Nuova fermata")){
						// Se non si sta tentando di modificare una fermata.
						if(adapterFermateDellaLinea.getPosizioneDellaFermataCheSiModifica() == -1)
							// Permette di caricare la linea.
							new UpdateLinea().execute();
						else Toast.makeText(context, "Confermare la fermata che si sta tentando di modificare.", Toast.LENGTH_LONG).show();
					}else Toast.makeText(context, "Inserire l'ultima fermata con un suggerimento.", Toast.LENGTH_LONG).show();
                else Toast.makeText(context, getString(R.string.controlloConessione), Toast.LENGTH_LONG).show();	 
				
			}
		});
        
        
        // Questo bottone nasconde la listView e abilita la ricerca di una nuova linea da modificare.
        btnCambiaLinea.setOnTouchListener( new BottoneCliccato( btnCambiaLinea.getAlpha() ));
        btnCambiaLinea.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Rendo visibigli gli elementi per la ricerca.
        		listViewMostra.setVisibility(ListView.VISIBLE);
        		
        		// Nascondo la ListView.
        		listViewModifica.setVisibility(ListView.GONE);
        		
        		// Ripulisco le EditText.
        		edtCambiaCodice.setText("");
        		edtCambiaNome.setText("");
			}
		});
        
        
        listViewMostra.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view,
                    int position, long id) {
				
				posizione = position;
				AlertDialog.Builder alertDialogBuilder = null;

                // Controllo che la connessione sia attiva.
                GlobalClass gc= (GlobalClass)getActivity().getApplication();
                if(gc.isOnline()){
                			
        			alertDialogBuilder = new AlertDialog.Builder(context);                	 
    				alertDialogBuilder.setTitle("Attenzione");
    				
    				alertDialogBuilder
    					.setMessage("Sei sicuro di voler eliminare questa linea?")
    					.setCancelable(false)
    					.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
    						public void onClick(DialogInterface dialog,int id) {
                    			
                    			// Elimino la linea.
    	                		new DeleteLinea().execute();
    						}
    					  })
    					.setNegativeButton("No",new DialogInterface.OnClickListener() {
    						public void onClick(DialogInterface dialog,int id) {
    							// if this button is clicked, just close
    							// the dialog box and do nothing
    							dialog.cancel();
    						}
    					});
    	 
    					// create alert dialog
    					AlertDialog alertDialog = alertDialogBuilder.create();
    	 
    					// show it
    					alertDialog.show();
                }else Toast.makeText(context, getString(R.string.controlloConessione), Toast.LENGTH_LONG).show();
				return true;	 
			}
        }); 
    }
    
    
    // Per ricevere gli orari.
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 2) {

			if(resultCode == InserimentoOrari.RESULT_OK){      
				orariAndata = data.getStringExtra("orariAndata");  
				orariRitorno = data.getStringExtra("orariRitorno");  
				
				
				// Riinserisco i campi gi� inseriti prima di passare
				// all'activity InserimentoOrari.
				edtCambiaNome.setText(data.getStringExtra("nomeLinea"));
				edtCambiaCodice.setText(data.getStringExtra("codiceLinea"));
				// Rimuovo tutte le fermate
            	fermateDellaLinea.clear();
				for(int indice = 0; indice < data.getIntExtra("numFermate", 0); indice++){
					fermateDellaLinea.add(data.getStringExtra("fermata" + indice));
					adapterFermateDellaLinea.notifyDataSetChanged();
            	}
			}
		}
	}
    

    /**
     * Il compito di questa classe è quello di aggiornare la linea. Per far
     * ciò prima la cancella dai database e poi la ricarica con i nuovi dati.
     * Non si usano due AsyncTask diversi per evitare il caso in cui, non essendo
     * sincronizzati, si inseriscano dei record prima che vengano eliminati.
     */
    class UpdateLinea extends AsyncTask<Void, Void, Void>{

        private ProgressDialog pDialog;

        // Tag usato nei log.
        private final String TAG = UpdateLinea.class.getSimpleName();

        private String ELIMINA_LINEA = GlobalClass.getDominio() + "Upload/eliminaLinea.php";
        private String CARICA_LINEA = GlobalClass.getDominio() + "Upload/caricaLinea.php";
        
        private String nomeLineaScelta = edtCambiaNome.getText().toString();
        private String codiceLineaScelta = edtCambiaCodice.getText().toString();
        
        JSONObject jsonElimina = null;
        JSONObject jsonCarica = null;
        List<NameValuePair> paramsCarica = null;
        
        /**
         * Metodo che viene eseguito prima di doInBackground.
         * Avvio il ProgressDialog.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            // Creo e mostro il ProgressDialog.
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Aggiornamento Fermata...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Questo metodo esegue il download.
         * @param voids
         * @return
         */
		@Override
		protected Void doInBackground(Void... params) {

            JSONParser jsonParser = new JSONParser();
            String codLinea = codiceLinea;
            // Creazione dei parametri.
			List<NameValuePair> paramsElimina = new ArrayList<NameValuePair>();
			paramsElimina.add(new BasicNameValuePair("CodLinea", codLinea));

			// Richiesta della pagina .php.
			jsonElimina = jsonParser.makeHttpRequest(
			        ELIMINA_LINEA, "POST", paramsElimina);


			jsonParser = new JSONParser();
			// Creazione dei parametri.
			// A differenza della classe FragmentAggiungiLinea, i parametri
			// li creo dentro all'AsyncTask perchè i controlli li faccio
			// già durante l'inserimento, e non quando si conferma l'invio
			// dei dati.
			paramsCarica = new ArrayList<NameValuePair>();
			paramsCarica.add(new BasicNameValuePair("NomeLinea", nomeLineaScelta));
			paramsCarica.add(new BasicNameValuePair("CodLinea", codiceLineaScelta));
			paramsCarica.add(new BasicNameValuePair("orariAndata", orariAndata));
			paramsCarica.add(new BasicNameValuePair("orariRitorno", orariRitorno));
			for (String fermata : fermateDellaLinea) {
				paramsCarica.add(new BasicNameValuePair("Fermate[]", fermata.split(" - Cod: ")[1]));
			}

			// Richiesta della pagina .php.
			jsonCarica = jsonParser.makeHttpRequest(
					CARICA_LINEA, "POST", paramsCarica);
            
            return null;
		}
		
        /**
         * Metodo eseguito dopo doInBackground. Chiudo il ProgressDialog.
         * @param risultato E' il risultato del .php.
         */
        @Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

            // Chiudo il ProgressDialog.
            pDialog.dismiss();
            
            // Aggiorno il database locale.
            try {

            	// Se la linea è sia stata eliminata che aggiunta con successo.
                if(jsonElimina.getInt(GlobalClass.getTagSuccess()) != 0)
                	if(jsonCarica.getInt(GlobalClass.getTagSuccess()) != 0){
                		

                	// Ottengo il database locale.
                	DatabaseLocale db = new DatabaseLocale(context);
                    SQLiteDatabase dbScrivibile = db.getWritableDatabase();
                    
                    // Cancello da linea e tratta.
                    dbScrivibile.delete(DatabaseLocale.getTableNameLinea(), 
                    		DatabaseLocale.getTagCodiceLinea() + " = '" + codiceLineaScelta + "'", null);                    
                    dbScrivibile.delete(DatabaseLocale.getTableNameTratta(), 
                    		DatabaseLocale.getTagCodiceLinea() + " = '" + codiceLineaScelta + "'", null);
                    
                    /// AGGIUNGO LA LINEA
                    ContentValues valori = new ContentValues();
                    valori.put(DatabaseLocale.getTagCodiceLinea(), codiceLineaScelta);
                    valori.put(DatabaseLocale.getTagNomeLinea(), nomeLineaScelta);
                    valori.put(DatabaseLocale.getTagCodiceCapolinea(), fermateDellaLinea.get(0).split(" - Cod: ")[1]);
                    valori.put(DatabaseLocale.getTagOrariAndata(), orariAndata);
                    valori.put(DatabaseLocale.getTagOrariRitorno(), orariRitorno);
                    dbScrivibile.insertOrThrow(DatabaseLocale.getTableNameLinea(), null, valori);
                    
                    // E la tratta.
					for (int i = 0; i < paramsCarica.size(); i++) {
                    	BasicNameValuePair valore = ((BasicNameValuePair) paramsCarica.get(i));
						if(valore.getName() == "Fermate[]" && i != (paramsCarica.size() - 1)){							
		                    valori = new ContentValues();
		                    valori.put(DatabaseLocale.getTagCodiceLinea(), codiceLineaScelta);
		                    valori.put(DatabaseLocale.getTagCodiceFermata(), valore.getValue());
		                    valori.put(DatabaseLocale.getTagSuccessiva(), ((BasicNameValuePair) paramsCarica.get(i + 1)).getValue());
		                    dbScrivibile.insert(DatabaseLocale.getTableNameTratta(), null, valori);
						}else if(i == (paramsCarica.size() - 1)){							
		                    valori = new ContentValues();
		                    valori.put(DatabaseLocale.getTagCodiceLinea(), codiceLineaScelta);
		                    valori.put(DatabaseLocale.getTagCodiceFermata(), valore.getValue());
		                    valori.put(DatabaseLocale.getTagSuccessiva(), 0);
		                    dbScrivibile.insert(DatabaseLocale.getTableNameTratta(), null, valori);
						}
					}
                        
					// Chiudo le connessioni.
                    dbScrivibile.close();
                    db.close();
                    
                    // Spezzone di codice dell'onClick di CambiaLinea,
                    // Torna alla ListViewMostra.
            		listViewMostra.setVisibility(ListView.VISIBLE);
            		listViewModifica.setVisibility(ListView.GONE);
            		edtCambiaCodice.setText("");
            		edtCambiaNome.setText("");
                    
                    Toast.makeText(context, "Linea modificata con successo.", Toast.LENGTH_LONG).show();
                	}else Toast.makeText(context, "Errore nel caricamento della linea", Toast.LENGTH_LONG).show();
                else Toast.makeText(context, "Errore eliminando la linea", Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Log.e(TAG, "Error " + e.toString());
                Toast.makeText(context, "Errore durante l'aggiornamento della linea nel database locale.", Toast.LENGTH_LONG).show();
            }
		}
    	
    }
    
    
    

    
    
    /**
     * Il compito di questa classe è eliminare una linea sia dal
     * server che dal database locale. Per eliminare una linea si devono
     * cancellare tutti i record che contengono il suo codice sia 
     * nella tabella linea che nella tabella tratta.
     */
    class DeleteLinea extends AsyncTask<Void, Void, String>{

        private ProgressDialog pDialog = null;

        // Tag usato nei log.
        private final String TAG = DeleteLinea.class.getSimpleName();

        private String ELIMINA_LINEA = GlobalClass.getDominio() + "Upload/eliminaLinea.php";
        
        JSONObject json = null;
        
        /**
         * Metodo che viene eseguito prima di doInBackground.
         * Avvio il ProgressDialog.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            // Creo e mostro il ProgressDialog.
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Eliminando la linea...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            JSONParser jsonParser = new JSONParser();
            String codLinea = ((String) listViewMostra.getItemAtPosition(posizione)).split(" - Cod: ")[1];
            try {

                // Creazione dei parametri.
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("CodLinea", codLinea));

                // Richiesta della pagina .php.
                json = jsonParser.makeHttpRequest(
                		ELIMINA_LINEA, "POST", params);

                return json.getString(GlobalClass.getTagMessage());
            } catch (JSONException e) {
                Log.e(TAG, "Error " + e.toString());
                Toast.makeText(context, "Errore nell'eliminazione della linea.", Toast.LENGTH_LONG).show();
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
            
            if(valoreRitornato != null){
	            // Aggiorno il database locale.
	            try {
	                if(json.getInt(GlobalClass.getTagSuccess()) != 0){
	                	DatabaseLocale db = new DatabaseLocale(context);
	                    SQLiteDatabase dbScrivibile = db.getWritableDatabase();
	                    
	                    // Elimino dalla tabella linea.
	                    dbScrivibile.delete(DatabaseLocale.getTableNameLinea(), 
	                    		DatabaseLocale.getTagCodiceLinea() + " = '" + ((String) listViewMostra.getItemAtPosition(posizione)).split(" - Cod: ")[1] + "'", null);
	                    
	                    // Elimino dalla tabella tratta.
	                    dbScrivibile.delete(DatabaseLocale.getTableNameTratta(), 
	                    		DatabaseLocale.getTagCodiceLinea() + " = '" + ((String) listViewMostra.getItemAtPosition(posizione)).split(" - Cod: ")[1] + "'", null);
	                    
	                    dbScrivibile.close();
	                    db.close();
	                    
	                    // Rimuovo dalla listview.
	                    adapterMostra.remove(adapterMostra.getItem(posizione));
	                    adapterMostra.notifyDataSetChanged();
	                }
	            } catch (JSONException e) {
		            Toast.makeText(context, "Errore nell'eliminazione della linea nel database locale", Toast.LENGTH_LONG).show();
	                Log.e(TAG, "Error " + e.toString());
	            }
	
	            // Aggiorno l'utente circa il risultato.
	            Toast.makeText(context, valoreRitornato, Toast.LENGTH_LONG).show();
            }
        }
    	
    }
    
}
