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
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Lo scopo di questo fragment è quello di aggiungere una linea
 * ai databases(locale e sul server). La richiesta dei dati è
 * complicata, viene spiegata all'interno della classe.
 */
public class FragmentAggiungiLinea extends Fragment {
	
	// TODO: questa classe funziona, migliorare il codice: lavorare su fermateDellaLinea e non sul layout.
	
    private Context context = null;
    private Button btnNuovaFermata = null;
    private Button btnEliminaFermata = null;
    private Button btnConfermaLinea = null;
    private Button btnInserisciOrari = null;
    private EditText edtNomeLinea = null;
    private EditText edtCodiceLinea = null;
    private String orariAndata = null;
    private String orariRitorno = null;
    
    // ListView che mostra le fermate, il suo adapter
    // ed il container delle fermate stesse.
    private ListView listViewAggiungi = null;
    private AdapterAggiungiLinea adapterListView = null;
    private ArrayList<String> fermateDellaLinea = null;
    
    // Per effettuare i dovuti controlli i parametri da passare
    // al php vengono creati nel main thread(nel listener) e non nell'AsyncTask.
    // Per questo motivo questa variabile è globale.
    List<NameValuePair> params = null;
    
    // Inflater: oggetto usato per creare il codice delle view a
    // partire dall'xml. View che vengono inserite dinamicamente.
    private LayoutInflater li = null;
    
    // Contiene tutte le fermate del database ed il loro codice concatenati.
    private ArrayList<String> tutteLeFermate = null;
	
	// Oggetto che contiene query al db locale.
	private Query richiediAlDatabase = null;

    // Tag usato nei log.
    @SuppressWarnings("unused")
    private static final String TAG = FragmentAggiungiLinea.class.getSimpleName();

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
        return inflater.inflate(R.layout.fragment_aggiungi_linea, container, false);
    }

    /**
     * Metodo chiamato appena dopo che è stato istanziato il layout. 
     * Ottengo gli oggetti in esso presenti.
     * @param view Tutte le view nel layout.
     * @param savedInstanceState Se si creasse un punto di ripristino dell'activity quando si chiude.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
          
        
        // Ottengo il servizio dell'inflater.        
        context = getActivity();		
        li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Riempo il contenitore di tutte le fermate, e lo uso come adapter per la AutoCompleteTextView.
		richiediAlDatabase = new Query(context);
        tutteLeFermate = richiediAlDatabase.caricaFermate();
        
        // Imposto la ListView.
        fermateDellaLinea = new ArrayList<String>();        
        listViewAggiungi = (ListView) getView().findViewById(R.id.listViewAggiungiLinea);
        LinearLayout header = (LinearLayout) li.inflate(R.layout.header_list_view_aggiungi_linea, null);
        listViewAggiungi.addHeaderView(header);
        btnEliminaFermata = (Button) header.findViewById(R.id.btnEliminaFermata);
        btnNuovaFermata = (Button) header.findViewById(R.id.btnNuovaFermata);
        btnConfermaLinea = (Button) header.findViewById(R.id.btnConfermaLinea);
        btnInserisciOrari = (Button) header.findViewById(R.id.btnInserisciOrari);
        edtNomeLinea = (EditText) header.findViewById(R.id.edtNomeLinea);
        edtCodiceLinea = (EditText) header.findViewById(R.id.edtCodiceLinea);
        adapterListView = new AdapterAggiungiLinea(context, R.id.actFermataIntermedia, fermateDellaLinea);
        listViewAggiungi.setAdapter(adapterListView);
        
        
        // Listener usato per aggiungere gli orari. Passo alla classe
        // tutti i valori da salvare di questo fragment, che una volta
        // ritornati saranno ripristinati. I suddetti dati sono
        // tutte le EditText.
        btnInserisciOrari.setOnTouchListener( new BottoneCliccato( btnInserisciOrari.getAlpha() ));
	    btnInserisciOrari.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), InserimentoOrari.class);
				intent.putExtra("orariAndata", orariAndata == null ? "" : orariAndata);
				intent.putExtra("orariRitorno", orariRitorno == null ? "" : orariRitorno);
				intent.putExtra("nomeLinea", edtNomeLinea.getText().toString());
				intent.putExtra("codiceLinea", edtCodiceLinea.getText().toString());
				intent.putExtra("numFermate", listViewAggiungi.getChildCount() - 1);
				for(int indice = 0; indice < fermateDellaLinea.size(); indice++){
                	intent.putExtra("fermata" + indice, fermateDellaLinea.get(indice));					
				}
				startActivityForResult(intent, 1);
        	}
		});
        
		
        /*
         * Se si richiede una nuova fermata, faccio l'inflate di nuova_fermata_intermedia.xml
         * dentro al layout base, e imposto gli aggetti e relativi adapter.
         */
	    btnNuovaFermata.setOnTouchListener( new BottoneCliccato( btnNuovaFermata.getAlpha() ));
	    btnNuovaFermata.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {

				if(!fermateDellaLinea.isEmpty()){
					LinearLayout ultimaLinea = (LinearLayout) listViewAggiungi.getChildAt(listViewAggiungi.getCount() - 1);
					AutoCompleteTextView actFermata = (AutoCompleteTextView) ultimaLinea.getChildAt(1);
					
					// Controllo che l'ultima fermata aggiunta non sia una EditText ancora vuota.
					if(!actFermata.getText().toString().matches("")){
						
						fermateDellaLinea.add("");						
						adapterListView.notifyDataSetChanged();
						
						// Faccio in modo che l'ultima riga si trovi sopra la tastiera.
						listViewAggiungi.setSelection(adapterListView.getCount() - 1);
					}else{
						Toast.makeText(context, "Completare prima l'ultima fermata", Toast.LENGTH_SHORT).show();
					}
				}else{
					
					// Se fermateDellaLinea è vuoto, il successivo controllo
					// nella if qui sopra darebbe errore.
					fermateDellaLinea.add("");
					adapterListView.notifyDataSetChanged();		
					listViewAggiungi.setSelection(1);
				}
			}
		});
        
	    btnEliminaFermata.setOnTouchListener( new BottoneCliccato( btnEliminaFermata.getAlpha() ));
	    btnEliminaFermata.setOnClickListener(new OnClickListener() {			
        	public void onClick(View v) {
        		
        		if(!fermateDellaLinea.isEmpty()){
	        		// Rimuovo l'ultima View.
	        		fermateDellaLinea.remove(fermateDellaLinea.size() - 1);
	        		adapterListView.notifyDataSetChanged();
        		}
			}
		});
        
        
        // Il suo compito è avviare l'upload della linea, dopo aver effettuato tutti
        // i dovuti controlli.
	    btnConfermaLinea.setOnTouchListener( new BottoneCliccato( btnConfermaLinea.getAlpha() ));
        btnConfermaLinea.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {

                GlobalClass gc= (GlobalClass)getActivity().getApplication();
                // Controllo che ci sia la connessione.
                if(gc.isOnline())
                	// Controllo che il nome della linea non sia più lungo di 30 caratteri.
					if(edtNomeLinea.getText().toString().length() < 31)
						// Controllo che il codice della linea non superi i 5 caratteri.
						if(edtCodiceLinea.getText().toString().length() < 6)
							// Controllo che nome e codice abbiano dei valori.
							if(!edtCodiceLinea.getText().toString().matches(""))
								if(!edtNomeLinea.getText().toString().matches(""))
									if(orariAndata != null && orariRitorno != null){
										// Creo qui i parametri per risolvere il problema:
										// http://stackoverflow.com/questions/23399793/arrayadapter-getposition-item-not-found-it-is-in-moriginalvalues
										// (non ho ancora ricevuto risposte).
						                params = new ArrayList<NameValuePair>();
						                params.add(new BasicNameValuePair("NomeLinea", edtNomeLinea.getText().toString()));
						                params.add(new BasicNameValuePair("CodLinea", edtCodiceLinea.getText().toString()));
						                params.add(new BasicNameValuePair("orariAndata", orariAndata));
						                params.add(new BasicNameValuePair("orariRitorno", orariRitorno));
						                int indice;
						                
						                // Indice = 1 perchè listViewAggiungi.getChildAt(0) è l'header
						                for(indice = 1; indice < listViewAggiungi.getChildCount(); indice++){							                	

											LinearLayout ultimaLinea = (LinearLayout) listViewAggiungi.getChildAt(indice);
											AutoCompleteTextView actFermata = (AutoCompleteTextView) ultimaLinea.getChildAt(1);
						                	
											String fermata = actFermata.getText().toString();
						                	
						                	// Deve avere uno dei valori suggeriti.
						                	if(!tutteLeFermate.contains(fermata)){
						                		Toast.makeText(context, "Le fermate devono assumere uno dei valori suggeriti.", Toast.LENGTH_LONG).show();
						                		break;
						                	}
						                	// Cerco la fermata dentro a params. Se la trova questo è un doppione.
						                	if(trovatoInArrayList(fermata.split(" - Cod: ")[1])){
						                		Toast.makeText(context, "Ogni fermata può essere inserita una volta soltanto.", Toast.LENGTH_LONG).show();
						                		break;
						                	}
						                	// Aggiungo la fermata.
						                    params.add(new BasicNameValuePair("Fermate[]", fermata.split(" - Cod: ")[1]));
						                }
						                // Se sono arrivato in fondo al ciclo for significa che non ho riscontrato errori
						                // e posso procedere con l'upload.
						                if(indice == listViewAggiungi.getChildCount())
						                	new UploadLinea().execute();		
									}else  Toast.makeText(context, "Inserire gli orari.", Toast.LENGTH_LONG).show();
								else Toast.makeText(context, "Inserire il nome della linea.", Toast.LENGTH_LONG).show();
							else Toast.makeText(context, "Inserire il nome della linea.", Toast.LENGTH_LONG).show();
						else Toast.makeText(context, "La lunghezza massima del codice è di 5 caratteri.", Toast.LENGTH_LONG).show();
					else Toast.makeText(context, "La lunghezza massima del nome è di 30 caratteri.", Toast.LENGTH_LONG).show();
				else Toast.makeText(context, getString(R.string.controlloConessione), Toast.LENGTH_LONG).show();	
			}
		});        
    }
    
    
    // Per ricevere gli orari e tutti i valori da ripristinare.
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == 1) {

			if(resultCode == InserimentoOrari.RESULT_OK){      
				orariAndata = data.getStringExtra("orariAndata");  
				orariRitorno = data.getStringExtra("orariRitorno");  
				
				
				// Riinserisco i campi gi� inseriti prima di passare
				// all'activity InserimentoOrari.
				edtNomeLinea.setText(data.getStringExtra("nomeLinea"));
				edtCodiceLinea.setText(data.getStringExtra("codiceLinea"));
				// Rimuovo tutte le fermate
				fermateDellaLinea.clear();
				for(int indice = 0; indice < data.getIntExtra("numFermate", 0); indice++){

					fermateDellaLinea.add(data.getStringExtra("fermata" + indice));					
            	}
				adapterListView.notifyDataSetChanged();
			}
		}
	}

	/**
	 * Metodo che cerca dentro alla lista params se è
	 * già presente una fermata con il codice passato
	 * come parametro.
	 * @param codice Codice della fermata da cercare. 
	 * @return
	 */
    private boolean trovatoInArrayList(String codice){
    	// Itero su tutti gli elementi.
    	for (NameValuePair elemento : params) {
			if(elemento.getValue().toString().contentEquals(codice)) return true;
		}
    	return false;
    }
    

    /**
     * Il compito di questa classe è scaricare uploudare la linea
     * sul server ed inserirla nel database locale.
     */
    class UploadLinea extends AsyncTask<Void, Void, String>{
    	private ProgressDialog pDialog = null;

        // Tag usato nei log.
        private final String TAG = UploadLinea.class.getSimpleName();

        // Test dall'Emulatore:
        private String NUOVA_LINEA = GlobalClass.getDominio() + "Upload/caricaLinea.php" ;
        
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
            pDialog.setMessage("Caricamento linea...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Questo metodo esegue l'upload.
         * @param voids
         * @return
         */
        @Override
        protected String doInBackground(Void... voids) {

            JSONParser jsonParser = new JSONParser();
            try {

                // Richiesta della pagina .php.
                json = jsonParser.makeHttpRequest(
                		NUOVA_LINEA, "POST", params);
                
                return json.getString(GlobalClass.getTagMessage());
            } catch (JSONException e) {
                Toast.makeText(context, "Errore nell'upload della linea.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error " + e.toString());
                return null;
            }
        }


        /**
         * Metodo eseguito dopo doInBackground.
         * Inserisco i dati nel database e resetto 
         * l'interfaccia utente.
         * @param response Json response.
         */
        @Override
        protected void onPostExecute(String valoreRitornato) {
            super.onPostExecute(valoreRitornato);

            // Chiudo il ProgressDialog.
            pDialog.dismiss();
            
            // Se l'upload è andato a buon fine.
            if(valoreRitornato != null){
	        	try {
	
	            	// Se la linea è stata aggiunta con successo.
	                if(json.getInt(GlobalClass.getTagSuccess()) != 0){
	                	DatabaseLocale db = new DatabaseLocale(context);
	                    SQLiteDatabase dbScrivibile = db.getWritableDatabase();
	
	                    // Inserimento nella tabella linea.
	                    ContentValues valori = new ContentValues();
	                    valori.put(DatabaseLocale.getTagCodiceLinea(), edtCodiceLinea.getText().toString());
	                    valori.put(DatabaseLocale.getTagNomeLinea(), edtNomeLinea.getText().toString());
	                    valori.put(DatabaseLocale.getTagOrariAndata(), orariAndata);
	                    valori.put(DatabaseLocale.getTagOrariRitorno(), orariRitorno);
	                    valori.put(DatabaseLocale.getTagCodiceCapolinea(), params.get(4).getValue());
	                    dbScrivibile.insert(DatabaseLocale.getTableNameLinea(), null, valori);
	                    
	                    // Inserimento nella tabella tratta.
						for (int i = 0; i < params.size(); i++) {
	                    	BasicNameValuePair valore = ((BasicNameValuePair) params.get(i));
							if(valore.getName() == "Fermate[]" && i != (params.size() - 1)){							
			                    valori = new ContentValues();
			                    valori.put(DatabaseLocale.getTagCodiceLinea(), edtCodiceLinea.getText().toString());
			                    valori.put(DatabaseLocale.getTagCodiceFermata(), valore.getValue());
			                    valori.put(DatabaseLocale.getTagSuccessiva(), ((BasicNameValuePair) params.get(i + 1)).getValue());
			                    dbScrivibile.insert(DatabaseLocale.getTableNameTratta(), null, valori);
							}else if(i == (params.size() - 1)){							
			                    valori = new ContentValues();
			                    valori.put(DatabaseLocale.getTagCodiceLinea(), edtCodiceLinea.getText().toString());
			                    valori.put(DatabaseLocale.getTagCodiceFermata(), valore.getValue());
			                    valori.put(DatabaseLocale.getTagSuccessiva(), 0);
			                    dbScrivibile.insert(DatabaseLocale.getTableNameTratta(), null, valori);
							}
						}
	                        
						// Chiusura connessioni.
	                    dbScrivibile.close();
	                    db.close();
	                }
		            
		        	if(json.getInt(GlobalClass.getTagSuccess()) > 0){
			        	// Resetto l'interfaccia grafica: elimino tutti i layout inflatati per le fermate e svuoto le edittext.
			            fermateDellaLinea.clear();
			            adapterListView.notifyDataSetChanged();
			            edtCodiceLinea.setText("");
			            edtNomeLinea.setText("");
			            orariAndata = null;
			            orariRitorno = null;
		        	}
	            } catch (JSONException e) {
		            Toast.makeText(context, "Errore nell'inserimento della linea nel database locale", Toast.LENGTH_LONG).show();
	                Log.e(TAG, "Error " + e.toString());
	            }

	            // Aggiorno l'utente circa il risultato.
	            Toast.makeText(context, valoreRitornato, Toast.LENGTH_LONG).show();
	            
            }
        }
    }
    
}
