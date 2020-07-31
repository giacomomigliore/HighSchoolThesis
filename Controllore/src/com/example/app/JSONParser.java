package com.example.app;

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

    // L'oggetto che contiene ciò che viene ricevuto dalla pagina php.
    static InputStream is = null;

    // L'oggetto che viene restituito alla fine dai metodi.
    static JSONObject jObj = null;

    // E' la stringa passata a jObj.
    static String json = null;

    // Tag usato nei log.
    private static final String TAG = JSONParser.class.getName();

    /**
     * Funzione che fa una richiesta ad una pagina php senza 
     * passare parametri.
     * @param url L'indirizzo della pagina.
     * @return
     */
    public JSONObject getJSONFromUrl(final String url) {

        // Richiesta HTTP.
        try {

            // Metodi per velocizzare la richiesta
            // http://stackoverflow.com/a/1565243/2337094
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 5000);

            // Costruzione del client e richiesta HTTP.
            DefaultHttpClient httpClient = new DefaultHttpClient(params);
            HttpPost httpPost = new HttpPost(url);

            // Esecuzione della richiesta e memorizzazione del resposo localmente.
            HttpResponse httpResponse = httpClient.execute(httpPost);
            // Estrazione dei dati dal responso.
            HttpEntity httpEntity = httpResponse.getEntity();
            // Apertura di un inputStream con il contenuto dei dati.
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            // Dichiarazione di uno StringBuilder per aiutare con l'analisi.
            StringBuilder sb = new StringBuilder();
            // Dichiarazione di una stringa per salvare l'oggetto JSON.
            String line;

            // Costruzione della stringa finchè non son finiti i dati.
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            // Chiusura dell'inputStream.
            is.close();
            // Conversione dello StringBuilder in una String.
            json = sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error converting result " + e.toString());
        }

        // Trasformazione della stringa in JSON.
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }
        
        return jObj;
    }

    /**
     * Funzione che preleva un json da un url facendo una richiesta
     * passando parametri in POST o GET.
     * @param url L'indirizzo a cui fare 
     * @param method POST o GET.
     * @param params I parametri della funzione.
     * @return JSONObject
     */
    public JSONObject makeHttpRequest(String url, String method, List<NameValuePair> params) {

        // Per ulteriori chiarimenti fare affidamento ai commenti
    	// del metodo superiore.

        try {

            // Controlla il metodo.
            if(method == "POST"){

                HttpParams params2 = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(params2, 3000);
                HttpConnectionParams.setSoTimeout(params2, 5000);
                HttpClient httpClient = new DefaultHttpClient(params2);
                HttpPost httpPost = new HttpPost(url);
                // Invio dei parametri.
                httpPost.setEntity(new UrlEncodedFormEntity(params));
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
        
        // Trasformazione del risultato in Json.
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
        
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }

        return jObj;

    }
}
