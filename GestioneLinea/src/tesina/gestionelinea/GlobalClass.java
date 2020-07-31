package tesina.gestionelinea;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Questa classe è stata creata per avere delle veriabili o dei metodi
 * raggiungibili da qualunque classe dell'applicazione. Ciò che è
 * dichiarato qui risulta globale. Per accedere a questi dati da un
 * altra classe ci si deve comportare come se getApplication() o
 * getApplicationContext() fossero delle classi e da essi si chiamassero
 * metodi o attributi statici.  Oppure si può utilizzare queata classe
 * come se fosse un Singleton.
 */
public class GlobalClass extends Application {
	
	// URL del server.
    private static final String dominio = "http://www.letsmove.altervista.org/";

    // Tag(ID) del responso Json restituito dagli script php.
    // I tag sono uguali per tutti gli AsyncTask: abbiamo
    // standardizzato tag di tutti gli script php.
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_POSTS = "posts";

    public static String getDominio() {
        return dominio;
    }

    public static String getTagPosts() {
		return TAG_POSTS;
	}

	public static String getTagMessage() {
		return TAG_MESSAGE;
	}

	public static String getTagSuccess() {
		return TAG_SUCCESS;
	}

	/**
     * Metodo usato per controllare che la connessione del device
     * sia attiva.
     * --
     * Method used to check if the internet conenction is on.
     * http://stackoverflow.com/a/4009133/2337094
     * @return boolean
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}