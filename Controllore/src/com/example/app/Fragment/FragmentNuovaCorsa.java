package com.example.app.Fragment;

import com.example.app.BottoneCliccato;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.GlobalClass;
import com.example.app.R;
import com.example.app.UploadNuovaCorsa;


/**
 * La classe FragmentNuovaCorsa � un Fragment che ha per layout il file
 * fragment_nuova_corsa.xml. Serve per vendere un biglietto della durata
 * fissa di 90 minuti(corsa semplice) all'utente che ha il cellulare
 * scarico e che quindi non pu� fare uso dell'nfc. L'utente deve fornire
 * username e password. La corsa viene detratta da quelle a sua disposizione.
 * Se quindi l'utente non ha corse a disposizione deve pagare al conducente.
 */
public class FragmentNuovaCorsa extends Fragment {

    private Button nuovaCorsa;
    private EditText username, password;
    private Context context;
    
    // Dichiaro la variabile che mi salva la durata del biglietto.
    private String durataBiglietto= "90";

    // Tag usato nei log.
    private static final String TAG = FragmentNuovaCorsa.class.getSimpleName();

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
        return inflater.inflate(R.layout.fragment_nuova_corsa, container, false);
    }

    /**
     * Metodo chiamato appena dopo che � stato istanziato il layout. Ottengo gli oggetti in esso presenti.
     * @param view Tutte le view nel layout.
     * @param savedInstanceState Se si creasse un punto di ripristino dell'activity quando si chiude.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ottengo il context dell'activity e le due EditText presenti nel file .xml.
        context = getActivity();
        username = (EditText) getView().findViewById(R.id.usernameNuovaCorsa);
        password = (EditText) getView().findViewById(R.id.passwordNuovaCorsa);

        // Creo Il bottone nuovaCorsa e vi associo il listener.
        nuovaCorsa = (Button) getView().findViewById(R.id.buttonNuovaCorsa);
        nuovaCorsa.setOnTouchListener( new BottoneCliccato(nuovaCorsa.getAlpha() ));
        nuovaCorsa.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Controllo che la connessione sia attiva.
                GlobalClass gc= (GlobalClass)getActivity().getApplication();
                if(gc.isOnline()){
                    // Controllo che l'autista abbia gi� selezionato la linea che sta percorrendo.
                    if(FragmentGestioneCorsa.getLineaSelezionata() != null){
                        // Controllo che in non vi siano EditText vuote.
                        if(username.getText().length() > 0 && password.getText().length() > 0){
                            // Dato che sul db la lunghezza massima del campo username � 20, lo limito anche qui.
                            if(username.getText().length() < 21){

                                // Avvio l'AsyncTask.
                                new UploadNuovaCorsa(context, username.getText().toString(), password.getText().toString(), FragmentGestioneCorsa.getLineaSelezionata(), durataBiglietto, true).execute();
                                //pulisco le EditText
                                username.setText("");
                                password.setText("");
                            } else Toast.makeText(context, getString(R.string.lunghezzaUsername), Toast.LENGTH_LONG).show();
                        } else Toast.makeText(context, getString(R.string.ogniCampo), Toast.LENGTH_LONG).show();
                    } else Toast.makeText(context, getString(R.string.dimenticatoSelezioneLinea), Toast.LENGTH_LONG).show();
                } else Toast.makeText(context, getString(R.string.controlloConessione), Toast.LENGTH_LONG).show();
            }
        });
    }
}
