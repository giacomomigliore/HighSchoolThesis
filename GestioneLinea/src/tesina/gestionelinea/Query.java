package tesina.gestionelinea;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Query {
	
	private Context context = null;
	
	public Query(Context context){
		this.context = context;
	}
	
	public String[] caricaOrariDellaLinea(String codLinea){
		String[] orari = new String[2];

    	// Ottengo il database locale.
        DatabaseLocale db = new DatabaseLocale(context);
        SQLiteDatabase dbLeggibile = db.getReadableDatabase();
        
        // Chiedo gli orari della linea.
        Cursor caricaOrari = dbLeggibile.rawQuery("SELECT " + DatabaseLocale.getTagOrariAndata() + ", " + DatabaseLocale.getTagOrariRitorno() +
    			" FROM " + DatabaseLocale.getTableNameLinea() +
				" WHERE " + DatabaseLocale.getTagCodiceLinea() + " = '" + codLinea + "'", null);        
        caricaOrari.moveToNext();    	
        
    	orari[0] = caricaOrari.getString(0);    
    	orari[1] = caricaOrari.getString(1);    	
    	
    	
    	// Chiusura delle connessioni.
    	caricaOrari.close();
    	dbLeggibile.close();
    	db.close();
		
		return orari;
	}
	
	/**
     * Metodo che restituisce un ArrayList che contiene il nome 
     * concatenato al codice di tutte le fermate contenute nel
     * database locale.
     * @return
     */
	public ArrayList<String> caricaFermate(){
    	// Qui dentro inserirò i dati.
    	ArrayList<String> listaDelleFermate = new ArrayList<String>();

        // Ottengo il database locale.
        DatabaseLocale db = new DatabaseLocale(context);
        SQLiteDatabase dbLeggibile = db.getReadableDatabase();
        
        // Query che chiede tutte le fermate.
        Cursor fermate = dbLeggibile.rawQuery("SELECT " + DatabaseLocale.getTagNomeFermata() + " || ' - Cod: ' || " + DatabaseLocale.getTagCodiceFermata() +
    			" FROM " + DatabaseLocale.getTableNameFermata(),
    			null);
        
        // Aggiungo ogni fermata alla lista.
    	while(fermate.moveToNext()){
    		listaDelleFermate.add(fermate.getString(0));    	
    	}
    	
        // Chiudo la connessione.
    	fermate.close();
    	dbLeggibile.close();
        db.close();
    	
    	return listaDelleFermate;
    }
	

    /**
     * Metodo che restituisce un ArrayList che contiene il nome 
     * concatenato al codice di tutte le linee contenute nel
     * database locale.
     * @return
     */
	public ArrayList<String> caricaLinee(){
    	// Qui dentro inserirò i dati.
    	ArrayList<String> listaDelleLinee = new ArrayList<String>();

        // Ottengo il database locale.
        DatabaseLocale db = new DatabaseLocale(context);
        SQLiteDatabase dbLeggibile = db.getReadableDatabase();

        // Eseguo la query.
        Cursor cursore = dbLeggibile.rawQuery("SELECT " + DatabaseLocale.getTagNomeLinea() + " || ' - Cod: ' || " + DatabaseLocale.getTagCodiceLinea()
        		+ " AS linea FROM " + DatabaseLocale.getTableNameLinea(), null);
        while(cursore.moveToNext()){
        	listaDelleLinee.add(cursore.getString(0));
        }

        // Chiudo le connessioni.
        cursore.close();
        dbLeggibile.close();
        db.close();
        
        return listaDelleLinee;
    }
    

    /**
     * Metodo che restituisce un ArrayList che contiene le 
 	 * fermate della linea in ordine di percorrenza.
 	 * Infatti non possono essere caricate in ordine casuale
 	 * ma devono essere messe in ordine.
     * @return
     */
	public ArrayList<String> caricaFermateDellaLinea(String codLinea){    	
    	
    	// Ottengo il database locale.
        DatabaseLocale db = new DatabaseLocale(context);
        SQLiteDatabase dbLeggibile = db.getReadableDatabase();
        
        // Chiedo la prima fermata della linea.
        Cursor caricaCapolinea = dbLeggibile.rawQuery("SELECT " + DatabaseLocale.getTagCodiceCapolinea() +
    			" FROM " + DatabaseLocale.getTableNameLinea() +
				" WHERE " + DatabaseLocale.getTagCodiceLinea() + " = '" + codLinea + "'", null);        
        caricaCapolinea.moveToNext();    	
    	int capolinea = caricaCapolinea.getInt(0);    	
    	
    	// Chiedo le fermate.
    	
    	// Questa map ha come chiave il codice della fermta, e poi un vettore
    	// che contiene la fermata successiva come primo elemento e poi
    	// il nome della fermata.
    	Map<Integer, Object[]> listaDelleFermateDaOrdinare = new HashMap<Integer, Object[]>();
    	// Chiedo al db: CodFermata - Successiva - NomeFermata
    	Cursor fermate = dbLeggibile.rawQuery("SELECT f." + DatabaseLocale.getTagCodiceFermata() + ", " + DatabaseLocale.getTagSuccessiva() + ", " + DatabaseLocale.getTagNomeFermata() + 
    			" FROM " + DatabaseLocale.getTableNameTratta() + " t JOIN " + DatabaseLocale.getTableNameFermata() + " f "
				+ "ON f." + DatabaseLocale.getTagCodiceFermata() + " = t." + DatabaseLocale.getTagCodiceFermata()
				+ " WHERE " + DatabaseLocale.getTagCodiceLinea() + " = '" + codLinea + "'", null);    	
    	while(fermate.moveToNext()){
    		// getInt(0) = CodFermata, getInt(1) = Successiva, getString(2) = NomeFermata.
    		listaDelleFermateDaOrdinare.put(fermate.getInt(0), new Object[]{fermate.getInt(1), fermate.getString(2)});
    	}
    	
    	// Questo oggetto sarà quello restituito.
    	ArrayList<String> listaDelleFermateOrdinata = new ArrayList<String>();
    	// Metto nella lista le fermate in ordine di percorrenza, a partire dalla
    	// prima fermata fino a quando la fermata non vale 0, ovvero l'ultima fermata.
    	int codFermata = capolinea;
    	while(codFermata != 0){
    		listaDelleFermateOrdinata.add(listaDelleFermateDaOrdinare.get(codFermata)[1].toString() +  " - Cod: " + codFermata);
    		codFermata = (Integer)listaDelleFermateDaOrdinare.get(codFermata)[0] == 0 ? 0 : (Integer)(listaDelleFermateDaOrdinare.get(codFermata)[0]);
    	}
    	
    	// Chiusura delle connessioni.
    	caricaCapolinea.close();
    	dbLeggibile.close();
    	db.close();
    	
    	return listaDelleFermateOrdinata;
    }

}
