package com.example.app.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.GlobalClass;
import com.example.app.JSONParser;
import com.example.app.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.app.BottoneCliccato;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe FragmentControllaCorsa è un Fragment che ha per layout il file
 * fragment_controlla_corsa.xml. Il suo scopo è controllare che un utente
 * abbia convalidato la corsa e che non sia ancora scorso il tempo massimo
 * relativo al tipo di corsa. Se il tempo è scaduto significa che l'utente
 * avrebbe dovuto convalidare. Se il tempo non è scaduto la corsa è valida.
 * Queste informazioni verranno fornite dalla classe. Questa classe viene 
 * usata quando non è possibile usare l'NFC, forse perchè il cellulare
 * è spento.
 */
public class FragmentControllaCorsa extends Fragment {

    private Button controlla;
    private TextView ultimaCorsa, validitaCorsa;
    private EditText codiceDocumento;
    private Context context;

    // Tag usato nei log.
    private static final String TAG = FragmentControllaCorsa.class.getSimpleName();

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
        return inflater.inflate(R.layout.fragment_controlla_corsa, container, false);
    }

    /**
     * Metodo chiamato appena dopo che è stato istanziato il layout. Ottengo gli oggetti in esso presenti.
     * @param view Tutte le view nel layout.
     * @param savedInstanceState Se si creasse un punto di ripristino dell'activity quando si chiude.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ottengo gli elementi dall'.xml e il context.
        ultimaCorsa = (TextView) getView().findViewById(R.id.ultimaCorsaControlloCorsa);
        validitaCorsa = (TextView) getView().findViewById(R.id.validitaCorsaControlloCorsa);
        codiceDocumento = (EditText) getView().findViewById(R.id.codiceDocumentoControllaCorsa);
        context = getActivity();

        // Creo Il bottone controlla e vi associo il listener.
        controlla = (Button) getView().findViewById(R.id.buttonControllaCorsa);
        controlla.setOnTouchListener( new BottoneCliccato(controlla.getAlpha() ));
        controlla.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Controllo che la connessione sia attiva.
                GlobalClass gc= (GlobalClass)getActivity().getApplication();
                if(gc.isOnline()){

                    // Solo se il codice documento è lungo 9 caratteri(come su
                    // ogni carta di identità.
                    if(codiceDocumento.getText().toString().length() == 9){

                        // Avvio DownloadUltimaCorsa.
                        new DownloadUltimaCorsa().execute();
                    } else {
                        Toast.makeText(context, getString(R.string.codiceDocumentoMeno9), Toast.LENGTH_LONG).show();
                        Log.d(TAG, getString(R.string.codiceDocumentoMeno9));
                    }
                } else {
                    Toast.makeText(context, getString(R.string.controlloConessione), Toast.LENGTH_LONG).show();
                    Log.d(TAG, getString(R.string.controlloConessione));
                }
            }
        });
    }


    /**
     * Il compito di questa classe è scaricare l'ultima tratta dell'utente e
     * fare i relativi controlli. E' stata dichiarata all'interno della classe
     * FragmentControllaCorsa perchè viene utilizzata esclusivamente in questa
     * classe e quindi per non creare confusione tra i sorgenti.
     */
    class DownloadUltimaCorsa extends AsyncTask<Void, Void, String>{

        private ProgressDialog pDialog;

        // Tag usato nei log.
        private final String TAG = DownloadUltimaCorsa.class.getSimpleName();

        // Test dall'Emulatore:
        private String ULTIMA_TRATTA_URL;

        // Tag(ID) del responso Json restituito dallo script php:
        // JSON element ids from response of php script:
        private static final String TAG_DATA = "data";
        private static final String TAG_DURATA = "durataconvalida";

        /*
        Siccome in doInBackground non posso modificare le TextView del thread
        principare, e non posso inviare i valori al onPostExecte perchè vi passo
        già il resoconto, inserisco i valori in variabili globali per poi 
        modificarli nell'onPostExecute.
        --
        Error:
         Caused by: android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
         */
        String ultima, durata;

        /**
         * Metodo che viene eseguito prima di doInBackground.
         * Avvio il ProgressDialog.
         */
        @SuppressWarnings("static-access")
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Download ultima convalida...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            // Aggiorno ULTIMA_TRATTA_URL.
            GlobalClass gc= (GlobalClass)getActivity().getApplication();
            ULTIMA_TRATTA_URL = gc.getDominio() + "Download/ultimaTratta.php";
        }

        /**
         * Questo metodo esegue il download.
         * @param voids
         * @return
         */
        @Override
        protected String doInBackground(Void... voids) {

            // Il parametro da passare alla pagina .php.
            String codice = codiceDocumento.getText().toString();
            JSONParser jsonParser = new JSONParser();

            // Creazione dei parametri.
            List<NameValuePair> params = new ArrayList<NameValuePair>();  //http://stackoverflow.com/a/17609232/2337094 Namevaluepair
            params.add(new BasicNameValuePair("codice", codice));

            // Avvio richiesta.
            JSONObject json = jsonParser.makeHttpRequest(ULTIMA_TRATTA_URL, "POST", params);

            try {

                /*
                Controllo che il download sia avvenuto con successo,
                altrimenti il codice da errore perchè cerca json.getJSONArray(TAG_POSTS)
                che non esiste.
                */
                if(Integer.parseInt(json.getString(GlobalClass.getTagSuccess())) == 1){

                    // Ottengo l'array dei dati scricati.
                    JSONArray corsa = json.getJSONArray(GlobalClass.getTagPosts());

                    // Siccome nella query vi è un LIMIT 1 non serve il ciclo, e prendo solo
                    // l'elemento in posizione 0.
                    JSONObject jobj = corsa.getJSONObject(0);

                    // Inserisco i valori nelle variabili.
                    ultima = jobj.getString(TAG_DATA);
                    durata = jobj.getString(TAG_DURATA);

                    return json.getString(GlobalClass.getTagMessage());
                } else {
                    return json.getString(GlobalClass.getTagMessage());
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing data " + e.toString());
                return "Errore nel download della corsa";
            }
        }

        /**
         * Metodo eseguito dopo doInBackground. Chiudo il ProgressDialog.
         * --
         * @param risultato E' il risultato del .php.
         */
        @Override
        protected void onPostExecute(String risultato) {
            super.onPostExecute(risultato);

            // Chiudo il ProgressDialog.
            pDialog.dismiss();

            if(risultato == null) Toast.makeText(context, "Errore nella connessione con il Database.", Toast.LENGTH_LONG).show();
            else {
                ultimaCorsa.setText(ultima);
                validitaCorsa.setText(durata);
                Toast.makeText(context, risultato, Toast.LENGTH_LONG).show();
            }

            // Svuoto la EditText.
            codiceDocumento.setText("");
        }
    }
}
