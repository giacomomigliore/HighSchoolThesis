package com.example.app.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.app.DatabaseLocale;
import com.example.app.R;

/**
 * La classe FragmentGestioneCorsa è un Fragment che ha per layout il file
 * fragment_gestione_corsa.xml. Serve per selezionare la linea che l'autista
 * sta percorrendo. Il dato è utilizzato ogni volta che si vende una corsa.
 */
public class FragmentGestioneCorsa extends Fragment{
	
	// File delle preferenze dell'applicazione in cui verra salvata la tratta scelta dall'autista.
	public static final String PREFS_NAME = "MyPrefsFileControllore";

    // Tag usato nei log.
    private static final String TAG = FragmentGestioneCorsa.class.getSimpleName();

    private static String lineaSelezionata;
    private Context context;
    
    // Database che contiene tutte le linee scaricate dal server.
    private DatabaseLocale db;

    // Vector che contiene gli elementi di uno spinner.
    private ArrayAdapter<String> adapter;
    private  Spinner spnLinee;

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
        return inflater.inflate(R.layout.fragment_gestione_corsa, container, false);
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
        
        // Inizializzazione dello Spinner.
        spnLinee = (Spinner) getView().findViewById(R.id.spinnerLinee);
        adapter =new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
        spnLinee.setAdapter(adapter);
        popolamentoSpinner();
        
        spnLinee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemSelected qlks");

                lineaSelezionata = spnLinee.getSelectedItem().toString().split(" ")[0];
                Toast.makeText(context, "Codice della linea selezionata: " + lineaSelezionata, Toast.LENGTH_LONG).show();
                
				// Salvo nel file delle preferenze dell'applicazione il codlinea.
			    SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
			    SharedPreferences.Editor editor = settings.edit();			    
			    editor.putString("codLinea", spnLinee.getSelectedItem().toString().split(" ")[0]);
			    
			    // Salvo i cambiamenti.
			    editor.commit();
            }
            
            public void onNothingSelected(AdapterView<?> adapterView) {}
            
        });
    }

    /**
     * Quando viene chiamato questo metodo viene svuotato lo spinner,
     * e riempito nuovamente con gli elementi presenti nel database.
     */
    private void popolamentoSpinner(){

        // Svuoto lo spinner.
        adapter.clear();

        // Ottengo il database locale.
        db = new DatabaseLocale(context);
        SQLiteDatabase dbLeggibile = db.getReadableDatabase();

        // Eseguo la query.
        String[] colonne = {DatabaseLocale.getTagCodiceLinea(), DatabaseLocale.getTagNomeLinea()};
        String tabella = DatabaseLocale.getTableNameLinea();
        Cursor cursore = dbLeggibile.query(tabella, colonne, null, null, null, null, null);
        while(cursore.moveToNext()){
            adapter.add(cursore.getString(0) + " " + cursore.getString(1));
        }

        // Chiudo le connessioni.
        cursore.close();
        dbLeggibile.close();
        db.close();
    }

    /**
     * Serve per settare la variabile a null all'inizio del programma. Infatti
     * viene chiamato nell'onCreate dell'activity principale. E' statico perchè
     * non vi saranno mai più istanze di questa classe, e la variabile deve essere
     * la stessa da qualunque classe essa venga chiamata.
     * @param lineaSelezionata La variabile da settare.
     */
    public static void setLineaSelezionata(String lineaSelezionata) {
        FragmentGestioneCorsa.lineaSelezionata = lineaSelezionata;
    }

    /**
     * Serve per ottenere da FragmentNuovaCorsa la linea che si sta percorrendo,
     * in modo da caricarla sul db. Il metodo e l'attributo sono static perché
     * non vi saranno più istanze di questa classe, e quindi sarebbe scomodo
     * istanziare un oggetto per richiamare questo metodo.
     * @return String La linea che si sta percorrendo.
     *                The path that is been routed.
     */
    public static String getLineaSelezionata() {
        return lineaSelezionata;
    }
}