package fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utente.DatabaseLocale;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.utente.R;

import expandibleListView.Linea;
import expandibleListView.MyExpandableListAdapter;

/**
 * La classe FragmentGestioneOrari è un Fragment che ha per layout il file
 * fragment_orari.xml. Indica all'utente le diverse linee con le relative 
 * fermate, e se l'utente preme sul bottone legato ad una linea vengono
 * visualizzati gli orari.
 * --
 * FragmentGestioneOrari class is a Fragment that has as layout the file
 * fragment_orari.xml. It shows the paths and the stops, and if the user 
 * pushes a button bounded to the path are shown the schedules.
 */
public class FragmentOrari extends Fragment{
	
	// Oggetto che contiene tutte le linee con le relative fermate.
	// Object that contains all the path and their stops.
	private static ArrayList<Linea> contenitoreLinee = null;
	private EditText edtCercaOrari = null;
	private static MyExpandableListAdapter adapter = null;
	private ExpandableListView listView;
	private static Context context;
	

    // Tag usato nei log.
    // Tag used into logs.
    private static final String TAG = FragmentOrari.class.getName();

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
		return (LinearLayout) inflater.inflate(R.layout.fragment_orari, container, false);
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
		
		// Ottengo gli oggetti dall'xml.
		// I get the objects from xml.
		edtCercaOrari = (EditText) getView().findViewById(R.id.edtCercaOrari);
		context = getActivity();
		edtCercaOrari.addTextChangedListener(new TextWatcher() {			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				adapter.filtraListView(edtCercaOrari.getText().toString());
			}
		});
		
		// Scarico le linee del db in un oggetto ArrayList.
		// Put the pths from the database in an ArrayList.
	    ottieniLinee();
	    
	    // Ottengo la ListView contenuta nel fragment e vi associo l'adapter.
	    // I get the ListView contained in the fragment and I bound the adapter.
	    listView = (ExpandableListView) getActivity().findViewById(R.id.listView);
	    adapter = new MyExpandableListAdapter(getActivity(), contenitoreLinee);	
	    listView.setAdapter(adapter);
	}
	
	public static void aggiorna(){
		if(adapter != null){
			ottieniLinee();
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * Metodo che carica le linee dal db all'ArrayList.
	 * Composto da due cicli: si chiede ogni linea e per ognuna di essa
	 * si scaricano tutte le fermate.
	 * --
	 * Method that charges the path from the db to the ArrayList.
	 * Composed by 2 iterations: ask paths and for all of them 
	 * ask the stops.
	 */
	@SuppressLint("UseSparseArrays")
	public static void ottieniLinee() {	
		contenitoreLinee = new ArrayList<Linea>();
		
        // Ottengo il database locale.
	    // I get the local database.
	    DatabaseLocale db = new DatabaseLocale(context);
	    SQLiteDatabase dbLeggibile = db.getReadableDatabase();
	
	    // Eseguo la query.
	    // Execute the query.
	    Cursor linee = dbLeggibile.rawQuery("SELECT * FROM " + DatabaseLocale.getTableNameLinea(), null);
	    while(linee.moveToNext()){ 
	    	
	    	// Creo l'oggetto Linea.
	    	// I create the Linea object.
	    	Linea group = new Linea(linee.getString(1));
	    	int capolinea = linee.getInt(2);
	    	String codLinea = linee.getString(0);
	    	
	    	
	    	Map<Integer, Object[]> listaDelleFermate = new HashMap<Integer, Object[]>();
	    	// Chiedo al db: CodFermata - Successiva - NomeFermata
	    	Cursor fermate = dbLeggibile.rawQuery("SELECT f." + DatabaseLocale.getTagCodiceFermata() + ", " + DatabaseLocale.getTagSuccessiva() + ", " + DatabaseLocale.getTagNomeFermata() + 
	    			" FROM " + DatabaseLocale.getTableNameTratta() + " t JOIN " + DatabaseLocale.getTableNameFermata() + " f "
					+ "ON f." + DatabaseLocale.getTagCodiceFermata() + " = t." + DatabaseLocale.getTagCodiceFermata()
					+ " WHERE " + DatabaseLocale.getTagCodiceLinea() + " = '" + codLinea + "'", null);
	    	
	    	// Metto il risultato in un oggetto Map<Integer, Object[]> nell'ordine:
	    	// Map<CodFermata, Object[]{Successiva, NomeFermata}>
	    	// --
	    	// I put the restult in an Map<Integer, Object[]> object in the order:
	    	// Map<CodFermata, Object[]{Successiva, NomeFermata}>
	    	while(fermate.moveToNext()){
	    		// Successiva - NomeFermata
	    		listaDelleFermate.put(fermate.getInt(0), new Object[]{fermate.getInt(1), fermate.getString(2)});
	    	}

	    	//Log.i(TAG, "Nome linea: " + linee.getString(1));
	    	/*
	    	 * Fino a quando la fermata successiva non è uguale a 0, si aggiunge
	    	 * il nome della fermata a quelle previste dalla linea. Se non vi è 
	    	 * un campo successiva uguale a 0 va in NullPointException.
	    	 * --
	    	 * Till the next stop is different from 0, the name of the stop is
	    	 * added to the one of the path. If there is no next stop equals to
	    	 * 0 it's risen a NPE.
	    	 */
	    	int codFermata = capolinea;
	    	while(codFermata != 0){
		    	Log.i(TAG, "Cod fermata: " + codFermata);
		    	Log.i(TAG, "lista: " + listaDelleFermate);
		    	Log.i(TAG, "Successiva, NomeFermata: " + listaDelleFermate.get(codFermata));
		    	Log.i(TAG, "Nome fermata: " + listaDelleFermate.get(codFermata)[1].toString());
	    		group.fermate.add(listaDelleFermate.get(codFermata)[1].toString());
	    		codFermata = (Integer)listaDelleFermate.get(codFermata)[0] == 0 ? 0 : (Integer)(listaDelleFermate.get(codFermata)[0]);
	    	}
	    	
	    	fermate.close();
	    	contenitoreLinee.add(group);
	    }
	
	    // Close connections.
	    linee.close();
	    db.close();
    }
}
