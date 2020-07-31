package tesina.gestionelinea;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Classe che dichiara il database locale in cui sono salvate le informazioni
 * per il Fragment orari.
 * --
 * Class in which is declared the local database. Into it are stored information
 * used in Fragment orari.
 *
 */
public class DatabaseLocale extends SQLiteOpenHelper {

    // Tag usato nei log.
    // Tag used into logs.
    @SuppressWarnings("unused")
    private static final String TAG = DatabaseLocale.class.getSimpleName();

    public static String getTagOrariAndata() {
		return TAG_ORARI_ANDATA;
	}

	public static String getTagOrariRitorno() {
		return TAG_ORARI_RITORNO;
	}

	// Tag indicanti i nomi usati dal db, utilizzabili anche da classi esterne.
    // Tag that indicate the names used on the db. Can be used also in externals classes.
    private static int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "DatabaseLocale";
    private static final String TABLE_NAME_LINEA = "Linea";
    private static final String TABLE_NAME_TRATTA = "Tratta";
	private static final String TABLE_NAME_FERMATA = "Fermata";
    private static final String TAG_CODICE_LINEA = "CodLinea";
    private static final String TAG_CODICE_FERMATA = "CodFermata";
    private static final String TAG_CODICE_CAPOLINEA = "CodCapolinea";
    private static final String TAG_NOME_LINEA = "NomeLinea";
    private static final String TAG_NOME_FERMATA = "NomeFermata";
    private static final String TAG_SUCCESSIVA = "Successiva";
    private static final String TAG_ORARI_ANDATA = "orariAndata";
    private static final String TAG_ORARI_RITORNO = "orariRitorno";

	// http://stackoverflow.com/a/4276758/2337094
    // Non ho bisogno di mettere utf-8.
    // I don't need to put default character set utf-8.
    
    // Dichiarazione delle tabelle.
    // Declaration of tabels.
    private static final String CREA_LINEA = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_LINEA + "(" +
                                                TAG_CODICE_LINEA + " VARCHAR(5) NOT NULL," +
                                                TAG_NOME_LINEA + " VARCHAR(30) NOT NULL," +
                                                TAG_CODICE_CAPOLINEA + " INTEGER NOT NULL," +
                                                TAG_ORARI_ANDATA + " VARCHAR(200) NOT NULL," +
                                                TAG_ORARI_RITORNO + " VARCHAR(200) NOT NULL," +                                                
                                                "PRIMARY KEY (" + TAG_CODICE_LINEA + ")," +
                                                "FOREIGN KEY (" + TAG_CODICE_CAPOLINEA + ") REFERENCES " + TABLE_NAME_FERMATA + "(" + TAG_CODICE_FERMATA + "))";

    private static final String CREA_TRATTA = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_TRATTA + "(" +
                                                TAG_CODICE_LINEA + " VARCHAR(5) NOT NULL," +
                                                TAG_CODICE_FERMATA + " INTEGER NOT NULL," +
                                                TAG_SUCCESSIVA + " INTEGER," +
                                                "PRIMARY KEY (" + TAG_CODICE_LINEA + ", " +
                                                	TAG_CODICE_FERMATA + ")," +
                                                "FOREIGN KEY (" + TAG_CODICE_LINEA + ") REFERENCES " + TABLE_NAME_LINEA + "(" + TAG_CODICE_LINEA + ")" + "," +
                                                "FOREIGN KEY (" + TAG_SUCCESSIVA + ") REFERENCES " + TABLE_NAME_FERMATA + "(" + TAG_CODICE_FERMATA + ")" + "," +
                                                "FOREIGN KEY (" + TAG_CODICE_FERMATA + ") REFERENCES " + TABLE_NAME_FERMATA + "(" + TAG_CODICE_FERMATA + "))";


    private static final String CREA_FERMATA = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_FERMATA + "(" +
            									TAG_CODICE_FERMATA + " INTEGER NOT NULL," +
                                                TAG_NOME_FERMATA + " VARCHAR(30) NOT NULL," +
                                                "PRIMARY KEY (" + TAG_CODICE_FERMATA + "))";

    /**
     * Constructor
     * @param context
     */
    public DatabaseLocale(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Metodo che crea le tabelle del db in un ordine che non causi
     * errori nelle righe in cui si dichiarano le chiavi esterne
     * causa la mancanza della relativa tabella.
     * --
     * Methods that creates db tables in order to avoid errors in 
     * the lines where are declared the foreigns keys caused by
     * the assence of the relative table.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREA_FERMATA);
        sqLiteDatabase.execSQL(CREA_LINEA);
        sqLiteDatabase.execSQL(CREA_TRATTA);
    }

    /**
     * Viene chiamato quando il database è di una versione obsoleta.
     * --
     * It's called when the db version is deprecated.
     * @param sqLiteDatabase
     * @param i
     * @param i2
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FERMATA);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LINEA);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TRATTA);
        onCreate(sqLiteDatabase);
    }
    
    public static int getDATABASE_VERSION() {
		return DATABASE_VERSION;
	}

	public static String getTableNameTratta() {
		return TABLE_NAME_TRATTA;
	}

	public static String getTableNameFermata() {
		return TABLE_NAME_FERMATA;
	}

	public static String getTagCodiceCapolinea() {
		return TAG_CODICE_CAPOLINEA;
	}

	private static final String TAG_KM = "Km";
    private static final String TAG_PERCORRENZA = "Percorrenza";

    
	public static String getTableNameLinea() {
		return TABLE_NAME_LINEA;
	}

	public String getDatabaseName() {
		return DATABASE_NAME;
	}

	public static String getTagCodiceLinea() {
		return TAG_CODICE_LINEA;
	}

	public static String getTagCodiceFermata() {
		return TAG_CODICE_FERMATA;
	}

	public static String getTagNomeLinea() {
		return TAG_NOME_LINEA;
	}

	public static String getTagNomeFermata() {
		return TAG_NOME_FERMATA;
	}

	public static String getTagSuccessiva() {
		return TAG_SUCCESSIVA;
	}

	public static String getTagKm() {
		return TAG_KM;
	}

	public static String getTagPercorrenza() {
		return TAG_PERCORRENZA;
	}

}
