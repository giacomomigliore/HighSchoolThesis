package utente;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;



public class Controllo extends Activity{
		
	public static final String PREFS_NAME = "MyPrefsFile";
	private Boolean loggato=null; //questa variabile mi dice se sono loggato 

		
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	 // Restore preferences
	 SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	 //leggo dal file delle preferenze e assegno il suo valore alla variabile booleana creata apposta
	 loggato = settings.getBoolean("loggato", false); 

	    if (!loggato) { //controllo se sono loggato 
	    	startActivity(new Intent(this, Login.class));
	    } else {
	    	startActivity(new Intent(this, Main.class));
	    }	
	    finish();
	    // note we never called setContentView()
	}

}
