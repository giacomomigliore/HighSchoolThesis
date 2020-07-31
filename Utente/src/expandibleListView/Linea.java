package expandibleListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che raccoglie una linea con le relative fermate.
 * Class that gather a path with the relative stops.
 *
 */
public class Linea {
	
  public String nomeLinea;
  public final List<String> fermate = new ArrayList<String>();

  public Linea(String nomeLinea) {
    this.nomeLinea = nomeLinea;
  }
} 
