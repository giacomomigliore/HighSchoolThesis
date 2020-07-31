package tesina.gestionelinea;

import java.util.ArrayList;
import java.util.Arrays;

import tesina.gestionelinea.R;
import tesina.gestionelinea.R.id;
import tesina.gestionelinea.R.layout;
import tesina.gestionelinea.BottoneCliccato;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class InserimentoOrari extends Activity {

	public final static int RESULT_OK = 1;


	private Button btnAggiungiOrario = null;
	private Button btnEliminaUltimoElemento = null;
	private Button btnConferma = null;
	private Spinner spnVisualizza = null;
	private ListView listViewOrari = null;
	private String orariAndata = null;
	private String orariRitorno = null;
	private static ArrayList<String> listaOrariAndata = null;
	private static ArrayList<String> listaOrariRitorno = null;
	private static ArrayAdapter<String> adapterListView = null;
	private ArrayAdapter<String> adapterSpinner = null;
	private static int selezionatoSpinner = 0;
	private static int oraIniziale = -1;
	private static int minutoIniziale = -1;
	private static String valoreDaAggingere = null;
	
	// Variabili usate per salvare lo stato del fragmentAggiungiLinea.
	private String nomeLinea = null;
	private String codiceLinea = null;
	private int numFermate = 0;
	private String[] fermate = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inserimento_orari);
		
		// Imposto lo spinner.
		spnVisualizza = (Spinner) findViewById(R.id.spnVisualizza);
		adapterSpinner = new ArrayAdapter<String>(
        		this,
        		android.R.layout.simple_spinner_item,
        		new String[]{"Orari dell'andata","Orari del ritorno"}
        		);
		spnVisualizza.setAdapter(adapterSpinner);
		spnVisualizza.setSelection(0);
		
		// Ricavo la stringa degli orari se sono già
		// stati precedentemente impostati, e creo l'adapter.
		Bundle extras = getIntent().getExtras();
	    if(extras != null) {
	        orariAndata = extras.getString("orariAndata");
	        orariRitorno = extras.getString("orariRitorno");
	        nomeLinea = extras.getString("nomeLinea");
	        codiceLinea = extras.getString("codiceLinea");
	        numFermate = extras.getInt("numFermate");
	        fermate = new String[numFermate];
	        for(int indice = 0; indice < numFermate; indice++){
	        	fermate[indice] = extras.getString("fermata" + indice);
	        }
	        
	        listaOrariAndata = stringToArray(orariAndata);
	        listaOrariRitorno = stringToArray(orariRitorno);
	        
			adapterListView = new ArrayAdapter<String>(
	        		this,
	        		R.layout.elemento_orario,
	        		R.id.txtElementoOrario
	        		);

	        // Si mostra sempre all'inizio dell'activity gli orari dell'andata.
			if(!listaOrariAndata.isEmpty())
				for (String orario : listaOrariAndata) {
					adapterListView.add(orario);
				}
	    }
		
		listViewOrari = (ListView) findViewById(R.id.listViewOrari);
		View headerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.header_list_view_orari, null);		
		btnAggiungiOrario = (Button) headerView.findViewById(R.id.btnAggiungiOrario);
		btnEliminaUltimoElemento = (Button) headerView.findViewById(R.id.btnEliminaUltimoElemento);
		btnConferma = (Button) headerView.findViewById(R.id.btnConferma);
		listViewOrari.addHeaderView(headerView);
		listViewOrari.setAdapter(adapterListView);
	    
	    
	    // A seconda di cosa si seleziona nello spinner si visualizzano gli orari
	    // dell'andata o del ritorno nella ListView.
	    spnVisualizza.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	
        	public void onItemSelected(AdapterView<?> adapter, View view,int pos, long id) {
        		selezionatoSpinner = adapter.getSelectedItemPosition();
        		
        		adapterListView.clear();
        		if(selezionatoSpinner == 0 && listaOrariAndata != null){
        			adapterListView.addAll(listaOrariAndata);
					oraIniziale = listaOrariAndata.isEmpty() ? -1 : Integer.valueOf(listaOrariAndata.get(listaOrariAndata.size() - 1).split(":")[0]);
					minutoIniziale = listaOrariAndata.isEmpty() ? -1 : Integer.valueOf(listaOrariAndata.get(listaOrariAndata.size() - 1).split(":")[1]);
        		}
        		// else Toast.makeText(InserimentoOrari.this, "Inserire prima gli orari dell'andata", Toast.LENGTH_LONG).show();
        		if(selezionatoSpinner == 1 && listaOrariRitorno != null){
        			adapterListView.addAll(listaOrariRitorno);
					oraIniziale = listaOrariRitorno.isEmpty() ? -1 : Integer.valueOf(listaOrariRitorno.get(listaOrariRitorno.size() - 1).split(":")[0]);
					minutoIniziale = listaOrariRitorno.isEmpty() ? -1 : Integer.valueOf(listaOrariRitorno.get(listaOrariRitorno.size() - 1).split(":")[1]);
        		}
        		// else Toast.makeText(InserimentoOrari.this, "Inserire prima gli orari del ritorno", Toast.LENGTH_LONG).show();
        	}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) { }
			
		});
		
	    btnAggiungiOrario.setOnTouchListener( new BottoneCliccato( btnAggiungiOrario.getAlpha() ));
		btnAggiungiOrario.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
			    DialogFragment newFragment = new TimePickerFragment();
			    newFragment.show(getFragmentManager(), "timePicker");

			}
		}); 
		
		btnEliminaUltimoElemento.setOnTouchListener( new BottoneCliccato( btnEliminaUltimoElemento.getAlpha() ));
		btnEliminaUltimoElemento.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				adapterListView.clear();
				if(selezionatoSpinner == 0){
					if(!listaOrariAndata.isEmpty()){
						listaOrariAndata.remove(listaOrariAndata.size() - 1);
						adapterListView.addAll(listaOrariAndata);
						oraIniziale = listaOrariAndata.isEmpty() ? -1 :  Integer.valueOf(listaOrariAndata.get(listaOrariAndata.size() - 1).split(":")[0]);
						minutoIniziale = listaOrariAndata.isEmpty() ? -1 :  Integer.valueOf(listaOrariAndata.get(listaOrariAndata.size() - 1).split(":")[1]);
					}
				}
				else if (selezionatoSpinner == 1){
					if(!listaOrariRitorno.isEmpty()){
						listaOrariRitorno.remove(listaOrariRitorno.size() - 1);
						adapterListView.addAll(listaOrariRitorno);
						oraIniziale = listaOrariRitorno.isEmpty() ? -1 :  Integer.valueOf(listaOrariRitorno.get(listaOrariRitorno.size() - 1).split(":")[0]);
						minutoIniziale = listaOrariRitorno.isEmpty() ? -1 :  Integer.valueOf(listaOrariRitorno.get(listaOrariRitorno.size() - 1).split(":")[1]);
					}
				}
				adapterListView.notifyDataSetChanged();
				
			}
		});
		
		btnConferma.setOnTouchListener( new BottoneCliccato( btnConferma.getAlpha() ));
		btnConferma.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				if(orariAndata == null) Toast.makeText(InserimentoOrari.this,
						"Prima di confermare bisogna ancora inserire gli orari dell'andata." ,
						Toast.LENGTH_SHORT).show();
				if(orariRitorno == null) Toast.makeText(InserimentoOrari.this,
						"Prima di confermare bisogna ancora inserire gli orari del ritorno." ,
						Toast.LENGTH_SHORT).show();
				
				Intent returnIntent = new Intent();
				returnIntent.putExtra("orariAndata", listaOrariAndata.isEmpty() ? null : arrayToString(listaOrariAndata));
				returnIntent.putExtra("orariRitorno", listaOrariRitorno.isEmpty() ? null : arrayToString(listaOrariRitorno));
				returnIntent.putExtra("nomeLinea", nomeLinea);
				returnIntent.putExtra("codiceLinea", codiceLinea);
				returnIntent.putExtra("numFermate", numFermate);
		        for(int indice = 0; indice < numFermate; indice++){
		        	returnIntent.putExtra("fermata" + indice, fermate[indice]);
		        }
				setResult(RESULT_OK,returnIntent);     
				finish();
				
			}
		}); 
		
	}
	
	
	private String arrayToString(ArrayList<String> array){
		String elenco = new String();
		for (String element : array) {
			elenco = elenco.concat(element + ";");
		}
		return elenco.substring(0, elenco.length() - 1);
	}
	
	
	private ArrayList<String> stringToArray(String elenco){
		if(!elenco.matches("")){
			String[] orari = null;
			if(elenco.contains(";"))  orari = elenco.split(";"); else orari = new String[] {elenco};
			ArrayList<String> array = new ArrayList<String>();
			for (String orario : orari) {
				array.add(orario);			
			}
			return array;
		}else return new ArrayList<String>();
	}
	

	public static class TimePickerFragment extends DialogFragment
    	implements TimePickerDialog.OnTimeSetListener {		
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			// Se è la prima volta che si apre il timePicker.
			if(oraIniziale == -1 && minutoIniziale == -1) return new TimePickerDialog(getActivity(), this, 0, 0, true);
			else return new TimePickerDialog(getActivity(), this, oraIniziale, minutoIniziale, true);
		}	
		

		// Per risolvere un bug dei timepicker: http://stackoverflow.com/questions/19452993/ontimeset-called-also-when-dismissing-timepickerdialog
		int chiamata = 0;
	
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			
			
			if(chiamata == 1){
				
				if(!(hourOfDay < oraIniziale))
					// if(!((hourOfDay == oraIniziale && minute < minutoIniziale) || (hourOfDay == oraIniziale && minute == minutoIniziale))){
					if((hourOfDay == oraIniziale && minute > minutoIniziale) || hourOfDay > oraIniziale){
						oraIniziale = hourOfDay;
						minutoIniziale = minute;
						valoreDaAggingere = padding_str(oraIniziale) + ":" + padding_str(minutoIniziale);
			
						adapterListView.clear();
						if(selezionatoSpinner == 0){
							listaOrariAndata.add(valoreDaAggingere);
							adapterListView.addAll(listaOrariAndata);
						}
						else if (selezionatoSpinner == 1){
							listaOrariRitorno.add(valoreDaAggingere);
							adapterListView.addAll(listaOrariRitorno);
						}
						adapterListView.notifyDataSetChanged();
					} else  Toast.makeText(getActivity(),
							"L'ora da inserire deve essere maggiore della precedente." ,
							Toast.LENGTH_SHORT).show();
				else Toast.makeText(getActivity(),
						"L'ora da inserire deve essere maggiore della precedente." ,
						Toast.LENGTH_SHORT).show();
			}
			chiamata++;
		}
		
		private String padding_str(int c) {
			if (c >= 10)
			   return String.valueOf(c);
			else
			   return "0" + String.valueOf(c);
		}
	}
}
