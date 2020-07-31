package expandibleListView;

import java.util.ArrayList;

import utente.BottoneCliccato;

import com.utente.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;


/**
 * Adapter della ListView in orari.
 * Tutti i metodi sono overraidati, riferirsi alla documentazione ufficiale:
 * --
 * Adapter of the ListVien in Orari.
 * All the methods are overriden, refer to the official documentation:
 * 
 * http://developer.android.com/reference/android/widget/BaseExpandableListAdapter.html
 */
@SuppressLint("DefaultLocale")
public class MyExpandableListAdapter extends BaseExpandableListAdapter {

  private ArrayList<Linea> lineeVisualizzate;
  private final ArrayList<Linea> lineeCompleta;
  public LayoutInflater inflater;
  public Activity activity;

  // Tag usato nei log.
  // Tag used into logs.
  private static final String TAG = MyExpandableListAdapter.class.getName();
  
  /**
  * Contructor
  * @param act Activity.
  * @param linee Tutte le linee e le fermate.
  */
  public MyExpandableListAdapter(Activity act, ArrayList<Linea> linee) {
    activity = act;
    lineeVisualizzate = new ArrayList<Linea>(linee);
    lineeCompleta = new ArrayList<Linea>(linee);
    inflater = act.getLayoutInflater();
  }

  @Override
  public Object getChild(int groupPosition, int childPosition) {
    return lineeVisualizzate.get(groupPosition).fermate.get(childPosition);
  }

  @Override
  public long getChildId(int groupPosition, int childPosition) {
    return 0;
  }

  @Override
  public View getChildView(int groupPosition, final int childPosition,
      boolean isLastChild, View convertView, ViewGroup parent) {
    final String children = (String) getChild(groupPosition, childPosition);
    TextView text = null;
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.listrow_details, null);
    }
    text = (TextView) convertView.findViewById(R.id.txtCAP);
    text.setText(children);
    return convertView;
  }

  @Override
  public int getChildrenCount(int groupPosition) {
    return lineeVisualizzate.get(groupPosition).fermate.size();
  }

  @Override
  public Object getGroup(int groupPosition) {
    return lineeVisualizzate.get(groupPosition);
  }

  @Override
  public int getGroupCount() {
    return lineeVisualizzate.size();
  }

  @Override
  public void onGroupCollapsed(int groupPosition) {
    super.onGroupCollapsed(groupPosition);
  }

  @Override
  public void onGroupExpanded(int groupPosition) {
    super.onGroupExpanded(groupPosition);
  }

  @Override
  public long getGroupId(int groupPosition) {
    return 0;
  }

  @Override
  public View getGroupView(int groupPosition, boolean isExpanded,
      View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = inflater.inflate(R.layout.listrow_group, null);
    }
    final Linea linea = (Linea) getGroup(groupPosition);
    TextView txtLinea = (TextView) convertView.findViewById(R.id.txtLinea);
    txtLinea.setText(linea.nomeLinea);
    Button btnOrario = (Button) convertView.findViewById(R.id.btnOrario);
    btnOrario.setFocusable(false);
    btnOrario.setTag(groupPosition);
    btnOrario.setText("Orario");


	btnOrario.setOnTouchListener( new BottoneCliccato( btnOrario.getAlpha() ));
    btnOrario.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
        	Intent intent = new Intent(activity, OrarioLinea.class);
        	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	intent.putExtra("nomeLinea", linea.nomeLinea);
        	intent.putExtra("primaFermata", linea.fermate.get(0));
        	intent.putExtra("ultimaFermata", linea.fermate.get(linea.fermate.size() - 1));
        	activity.startActivity(intent);
        }
    });
    return convertView;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }

  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return false;
  }
  
  public void filtraListView(String fermata){
	  
	if(fermata.isEmpty()) lineeVisualizzate = new ArrayList<Linea>(lineeCompleta);
	else{
		lineeVisualizzate.clear();
		for (Linea linea : lineeCompleta) {
			for (String fermataLinea : linea.fermate) {
				if(fermataLinea.toLowerCase().contains(fermata.toLowerCase())){
					lineeVisualizzate.add(linea);
					Log.i(TAG, "vero");
					break;
				}else Log.i(TAG, "falso");
			}
		}
	}
	this.notifyDataSetChanged();
  }
} 
