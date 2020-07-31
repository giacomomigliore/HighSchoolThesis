package fragments;

import java.util.ArrayList;

import tesina.gestionelinea.Query;
import tesina.gestionelinea.R;
import tesina.gestionelinea.BottoneCliccato;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Questa classe ha il compito di gestire tutte le linee
 * della ListView, e quindi il loro comportamento. Ogni linea 
 * ha 4 Views, di cui ne mostra solo 2 per volta.
 * All'inizio mostra il nome della fermata ed il bottone per 
 * eliminarla. Se si clicca sul nome della linea queste due
 * Views vengono nascoste e viene mostrata una EditText
 * per inserire la nuova fermata ed un bottone per confermare.
 * @author giacomotb
 *
 */
public class AdapterModificaLinea extends ArrayAdapter<String> {
	
	// SUL TABLET NON FUNZIONA, SU GEANYMOTION S�	
	/**
	 * Classe statica che permette di evitare ripetuti
	 * findViewById(), e risparmia il 15% del tempo.
	 * http://www.vogella.com/tutorials/AndroidListView/article.html#adapterperformance
	 * http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
	 *
	 */
	/*static class ViewHolder {
	    TextView txtCambiaFermata;
		AutoCompleteTextView actModificaFermata;
		Button btnEliminaFermataModificaLinea;
		Button btnModificaFermataInModificaLinea;
	}*/
	
	private Context context = null;
	private LayoutInflater li = null;
	private ArrayList<String> fermateDellaLinea = null;
	// ViewHolder holder = null;
	
	// Variabile che indica se in almeno una riga
	// si sta modificando la fermata(è mostrata 
	// l'EditText.
	private String testoDiQuellaCheSiStaModificando = null;
	private int posizioneDellaFermataCheSiModifica = -1;
	
	// Il layout che viene usato per ogni riga.
	private int layoutResourceId = 0; 
	
	// Oggetto che contiene query al db locale.
	private Query richiediAlDatabase = null;

	/**
	 * Costruttore.
	 * @param context
	 * @param resource
	 * @param fermate
	 */
	public AdapterModificaLinea(Context context, int resource, ArrayList<String> fermate) {
		super(context, resource, fermate);
		this.context = context;
		layoutResourceId = resource;
		this.fermateDellaLinea = fermate;	
		li = LayoutInflater.from(context);
		richiediAlDatabase = new Query(context);
	}
	
	/**
	 * Metodo che crea la row quando si disegna la ListView.
	 * Assegna anche i listener ai suoi elementi.
	 */
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
		
		
		// Tutte le fermare nel database locale.
		final ArrayList<String> tutteLeFermate = richiediAlDatabase.caricaFermate();
		
		// Permette di riutilizzare le View che sono fuori dallo schermo
		// per risparmiare tempo.
		// http://www.vogella.com/tutorials/AndroidListView/article.html#adapterperformance
		if(convertView == null){
			convertView = li.inflate(layoutResourceId, null);
		}

		// Trovo gli elementi dall'xml.
		final TextView txtCambiaFermata = (TextView) convertView.findViewById(R.id.txtCambiaFermata);
		final AutoCompleteTextView actModificaFermata = (AutoCompleteTextView) convertView.findViewById(R.id.actModificaFermata);
		final Button btnEliminaFermataModificaLinea = (Button) convertView.findViewById(R.id.btnEliminaFermataModificaLinea);
		final Button btnModificaFermataInModificaLinea = (Button) convertView.findViewById(R.id.btnModificaFermataInModificaLinea);
		
		if(posizioneDellaFermataCheSiModifica != position){
			btnModificaFermataInModificaLinea.setVisibility(Button.GONE);
			actModificaFermata.setVisibility(AutoCompleteTextView.GONE);
			btnEliminaFermataModificaLinea.setVisibility(Button.VISIBLE);
			txtCambiaFermata.setVisibility(TextView.VISIBLE);
		}else{
			btnModificaFermataInModificaLinea.setVisibility(Button.VISIBLE);
			actModificaFermata.setVisibility(AutoCompleteTextView.VISIBLE);
			btnEliminaFermataModificaLinea.setVisibility(Button.GONE);
			txtCambiaFermata.setVisibility(TextView.GONE);
			actModificaFermata.setText(testoDiQuellaCheSiStaModificando);
		}

		// Aggiungo il testo alla TextView
	    txtCambiaFermata.setText(fermateDellaLinea.get(position));
	    // Aggiungo l'adapter all'AutoCompleteTextView.
	    actModificaFermata.setAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line, tutteLeFermate));
	    
	    // Se si preme sulla textview la scambio con una all'AutoCompleteTextView per cambiare fermata.
	    txtCambiaFermata.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				

				posizioneDellaFermataCheSiModifica = position;
				// Se non sto già modificando un'altra fermata.
				if(posizioneDellaFermataCheSiModifica > -1){
					// Dichiaro che sto modificando una fermata.
					testoDiQuellaCheSiStaModificando = txtCambiaFermata.getText().toString();
					
					// Assegno all'AutoComplete.. il testo della TextView nascosta.
				    actModificaFermata.setText(txtCambiaFermata.getText().toString());
				    // Cambio visibilità agli elementi.
				    txtCambiaFermata.setVisibility(TextView.GONE);
				    btnEliminaFermataModificaLinea.setVisibility(Button.GONE);
				    actModificaFermata.setVisibility(AutoCompleteTextView.VISIBLE);
				    btnModificaFermataInModificaLinea.setVisibility(Button.VISIBLE);
				    
				    		
				}else Toast.makeText(context, "Confermate la fermata che si sta modificando."
						, Toast.LENGTH_LONG).show();
			}
		});
	    

	    // Listener al bottone che conferma la modifica della fermata.
	    btnModificaFermataInModificaLinea.setOnTouchListener( new BottoneCliccato( btnModificaFermataInModificaLinea.getAlpha() ));
	    btnModificaFermataInModificaLinea.setOnClickListener(new View.OnClickListener() {						
			@Override
			public void onClick(View v) {
				
				// Non sto più modificando alcuna riga.
				posizioneDellaFermataCheSiModifica = -1;
				
				// Se la fermata è una di quelle consigliate.
				if(tutteLeFermate.contains(actModificaFermata.getText().toString()))
					// Se non è presente tra quelle già inserite
					if(!fermateDellaLinea.contains(actModificaFermata.getText().toString())
							// Ma la posizione non è la stessa di quella che si sta modificando,
							// nel caso in cui si reinserisca la fermata che si voleva modificare.
							|| fermateDellaLinea.indexOf(actModificaFermata.getText().toString()) == position){
						
						// Cambio la fermata nella lista a cui fa riferimento la ListView.
						fermateDellaLinea.set(position, actModificaFermata.getText().toString());
						// Notifico i cambiamenti.
						AdapterModificaLinea.this.notifyDataSetChanged();
						
						// Cambio le visibilità.
						btnModificaFermataInModificaLinea.setVisibility(Button.GONE);
						actModificaFermata.setVisibility(AutoCompleteTextView.GONE);
						btnEliminaFermataModificaLinea.setVisibility(Button.VISIBLE);
						txtCambiaFermata.setVisibility(TextView.VISIBLE);
						
					}else Toast.makeText(context, "Fermata già presente: prima di inserirla eliminare la medesima fermata dalla lista"
								, Toast.LENGTH_LONG).show();
				else Toast.makeText(context, "La fermata deve assumere uno dei valori suggeriti."
							, Toast.LENGTH_LONG).show();
			}
		});		
	    
	    
	    // Bottone che permette di eliminare una data riga e la sua fermata.
	    btnEliminaFermataModificaLinea.setOnTouchListener( new BottoneCliccato( btnEliminaFermataModificaLinea.getAlpha() ));
	    btnEliminaFermataModificaLinea.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				fermateDellaLinea.remove(position);
				AdapterModificaLinea.this.notifyDataSetChanged();
			}
		});
	   
	    return convertView;
    }
	
	public int getPosizioneDellaFermataCheSiModifica() {
		return posizioneDellaFermataCheSiModifica;
	}


}
