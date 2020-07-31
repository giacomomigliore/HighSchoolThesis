package expandibleListView;

import utente.DatabaseLocale;
import com.utente.R;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class OrarioLinea extends Activity {
	
	private String nomeLinea = null;
	private String primaFermata = null;
	private String ultimaFermata = null;
	private TextView txtAndata = null;
	private TextView txtRitorno = null;
	private ListView lstAndata = null;
	private ListView lstRitorno = null;
	private ArrayAdapter<String> adapterAndata = null;
	private ArrayAdapter<String> adapterRitorno = null;
	private String andata = null;
	private String ritorno = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orario_linea);
		
		Bundle extras = getIntent().getExtras();
	    if(extras != null) {
	    	nomeLinea = extras.getString("nomeLinea");
	    	primaFermata = extras.getString("primaFermata");
	    	ultimaFermata = extras.getString("ultimaFermata");
	    }
	    
	    txtAndata = (TextView) findViewById(R.id.txtAndata);
	    txtRitorno = (TextView) findViewById(R.id.txtRitorno);
	    lstAndata = (ListView) findViewById(R.id.lstAndata);
	    lstRitorno = (ListView) findViewById(R.id.lstRitorno);
	    
	    txtAndata.setText("Da " +  primaFermata + " a " + ultimaFermata);
	    txtRitorno.setText("Da " +  ultimaFermata + " a " + primaFermata);
	    
	    caricaOrari();
	    adapterAndata = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, andata.split(";"));
	    adapterRitorno = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ritorno.split(";"));
	    
	    lstAndata.setAdapter(adapterAndata);
	    lstRitorno.setAdapter(adapterRitorno);
	}
	
	
	private void caricaOrari(){

        // Ottengo il database locale.
        DatabaseLocale db = new DatabaseLocale(this);
        SQLiteDatabase dbLeggibile = db.getReadableDatabase();
        
        // Query che chiede tutte gli orari della linea.
        Cursor orari = dbLeggibile.rawQuery("SELECT " + DatabaseLocale.getTagOrariAndata() + ", " + DatabaseLocale.getTagOrariRitorno() +
    			" FROM " + DatabaseLocale.getTableNameLinea() + " WHERE " + DatabaseLocale.getTagCodiceLinea() + " = " + 
    			" ( SELECT " + DatabaseLocale.getTagCodiceLinea() + " FROM " + DatabaseLocale.getTableNameLinea() + " WHERE " +
    			DatabaseLocale.getTagNomeLinea() + " = '" + nomeLinea + "' )",
    			null);

    	orari.moveToNext();
		andata = orari.getString(0);  
		ritorno = orari.getString(1);  
    	
        // Chiudo la connessione.
    	orari.close();
    	dbLeggibile.close();
        db.close();
		
	}
	

}
