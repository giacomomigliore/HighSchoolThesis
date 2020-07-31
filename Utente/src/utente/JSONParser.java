package utente;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class JSONParser {

    // L'oggetto che contiene ci� che viene ricevuto dalla pagina php.
    // The object that contains what will be returned from the php page.
    static InputStream is = null;

    // L'oggetto che viene restituito alla fine dai metodi.
    // The object that is returned at the end by the mathods.
    static JSONObject jObj = null;

    // E' la stringa passata a jObj.
    // Is the string passed to jObj.
    static String json;

    // Tag usato nei log.
    // Tag used into logs.
    private static final String TAG = JSONParser.class.getName();

    /**
     * Funzione che fa una richiesta ad una pagina php senza passare parametri.
     * --
     * Fuction that starts a php page without passing params.
     * @param url
     * @return
     */
    public JSONObject getJSONFromUrl(final String url) {

        // Richiesta HTTP.
        // Making HTTP request.
        try {

            // Metodi per velocizzare la richiesta
            // Way for make the request speedier
            // http://stackoverflow.com/a/1565243/2337094
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            // Costruzione del client e richiesta HTTP.
            // Construct the client and the HTTP request.
            DefaultHttpClient httpClient = new DefaultHttpClient(params);
            HttpPost httpPost = new HttpPost(url);

            // Esecuzione della richiesta POST e memorizzazione del resposo localmente.
            // Execute the POST request and store the response locally.
            HttpResponse httpResponse = httpClient.execute(httpPost);
            // Estrazione dei dati dal responso.
            // Extract data from the response.
            HttpEntity httpEntity = httpResponse.getEntity();
            // Apertura di un inputStream con il contenuto dei dati.
            // Open an inputStream with the data content.
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error " + e.toString());
        } catch (ClientProtocolException e) {
            Log.e(TAG, "Error " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Error " + e.toString());
        }

        try {
            // Creazione di un BufferedReader per l'analisi dell'inputStream.
            // Create a BufferedReader to parse through the inputStream.
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            // Dichiarazione di uno StringBuilder per aiutare con l'analisi.
            // Declare a StringBuilder to help with the parsing.
            StringBuilder sb = new StringBuilder();
            // Dichiarazione di una stringa per salvare l'oggetto JSON.
            // Declare a string to store the JSON object data in string form.
            String line;

            // Costruzione della stringa finch� non son finiti i dat.
            // Build the string until null.
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            // Chiusura dell'inputStream.
            // Close the input stream.
            is.close();
            // Conversione dello StringBuilder in una String.
            // Convert the StringBuilder data to an actual string.
            json = sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error converting result " + e.toString());
        }

        // Prova a trasformare la stringa in JSON.
        // Try to parse the string to a JSON object.
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }
        
        // Return the JSON Object.
        return jObj;
    }

    /**
     * Funzione che preleva un json da un url facendo una richiesta
     * passando parametri in POST o GET.
     * --
     * Function get json from url by making HTTP POST or GET mehtod.
     * @param url L'indirizzo a cui fare richiesta.
     *            The address from which we retrieve the json.
     * @param method POST o GET.
     * @param params I parametri della funzione.
     *               The params of the function.
     * @return JSONObject
     */
    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {

        // Se mancano dei commenti, andare a quelli del metodo precedente.
        // If there aren't some comments, go to the ones of the previous method.

        // Making HTTP request
        try {

            // Controlla il metodo.
            // Check for request method.
            if(method == "POST"){

                // Metodi per velocizzare la richiesta
                // Way for make the request speedier
                // http://stackoverflow.com/a/1565243/2337094
                HttpParams params2 = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(params2, 3000);
                HttpConnectionParams.setSoTimeout(params2, 5000);

                //params2.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                HttpClient httpClient = new DefaultHttpClient(params2);
                //DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                // Invio dei parametri.
                // Passing the params.
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                // Questo � il metodo pi� lento.
                // This is the slowest method.
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            }else if(method == "GET"){
                DefaultHttpClient httpClient = new DefaultHttpClient();
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error " + e.toString());
        } catch (ClientProtocolException e) {
            Log.e(TAG, "Error " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Error " + e.toString());
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error converting result " + e.toString());
        }

        // Prova a trasformare la stringa in JSON.
        // Try to parse the string to a JSON object.
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }
}
