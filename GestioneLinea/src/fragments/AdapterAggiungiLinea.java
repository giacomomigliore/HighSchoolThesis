package fragments;

import java.util.ArrayList;

import tesina.gestionelinea.Query;
import tesina.gestionelinea.R;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

/**
 * Questo adapter gestisce la listview presente in aggiungi linea,
 * e tutte le fermate da inserire.
 * @author giacomotb
 *
 */
public class AdapterAggiungiLinea extends ArrayAdapter<String> {

	private Context context = null;
	private LayoutInflater li = null;
	
	// Tutte le righe della listView, aggiornata ad ogni
	// carattere inserito per evitare che chiamando
	// la funzione getView() della ListView venga perso
	// qualche dato.
	private ArrayList<String> fermateDellaLinea = null;
	
	// Il layout che viene usato per ogni riga.
	private int layoutResourceId = 0; 
	
	// Oggetto che contiene query al db locale.
	private Query richiediAlDatabase = null;
	
	/**
	 * Costruttore
	 * @param context
	 * @param resource
	 * @param fermateDellaLinea
	 */
	public AdapterAggiungiLinea(Context context, int resource, ArrayList<String> fermateDellaLinea) {
		super(context, resource);
		this.context = context;
		li = LayoutInflater.from(context);
		layoutResourceId = resource;
		richiediAlDatabase = new Query(context);
		this.fermateDellaLinea = fermateDellaLinea;
	}
	
	/**
	 * Metodo che crea la row quando si disegna la ListView.
	 * Assegna anche i listener ai suoi elementi: ogni AutoCompleteTextView 
	 * ha un listener che ogni volta che viene modificata salva 
	 * il nuovo valore dentro a fermateDellaLinea.
	 */
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
		
	
		
		// Tutte le fermare nel database locale.
		final ArrayList<String> tutteLeFermate = richiediAlDatabase.caricaFermate();
		
		// Se riutilizzassi le righe come ho fatto con gli
		// altri adapter, i valori delle EditText subirebbero
		// delle modifiche inaspettate.
		convertView = li.inflate(R.layout.nuova_fermata_intermedia, null);

		// Trovo l'elemento dall'xml.
		final AutoCompleteTextView actFermataIntermedia = (AutoCompleteTextView) convertView.findViewById(layoutResourceId);

	    // Aggiungo l'adapter all'AutoCompleteTextView.
		actFermataIntermedia.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line, tutteLeFermate));
	    	   
		// Inserisco il testo perendendolo da quello già salvato.
		actFermataIntermedia.setText(fermateDellaLinea.get(position));
		
		// Listener avviato per ogni carattere inserito.
		actFermataIntermedia.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                    int before, int count) {
            	Log.i("Posizione + fermata", position + ", " + s.toString());
                fermateDellaLinea.set(position, s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                    int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });
		
	    return convertView;
    }
	
	/**
	 * Metodo che ritorna il numero di righe, ovvero il numero
	 * di fermate contenute in fermateDellaLinea.
	 */
	@Override
	public int getCount() {
		super.getCount();
		
		return fermateDellaLinea.size();
	}

	// Controllo se l'ultima EditText è vuota. E' esattamente come
	// controllare l'ultimo elemento di fermateDellaLinea.
	public boolean isLastEmpty(){
		if (this.getCount() == 0) return false;
		return this.getItem(this.getCount() - 1).toString().matches("");
	}
}

