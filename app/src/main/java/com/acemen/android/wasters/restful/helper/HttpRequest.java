package com.acemen.android.wasters.restful.helper;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Audrik ! on 20/04/2016.
 */
public class HttpRequest {
    private static final String LOG_TAG = HttpRequest.class.getSimpleName();

    private URL url;
    private HttpURLConnection con;
    private OutputStream os;

    //Can be instantiated with java.net.URL object, which checked for MalformedURLException by caller, so no need to declare here
    public HttpRequest(URL url) {
        this.url = url;
    }

    //Can be instantiated with String representation of url, force caller to check for MalformedURLException which can be thrown by URL's constructor
    public HttpRequest(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    //used by all sending methods: send(), sendAndReadString(), sendAndReadJson() - to close open resources
    private void done() throws IOException {
        con.disconnect();
        closeOutputStream();
    }

    private void closeOutputStream() throws IOException {
        if (os != null) {
            os.close();
            os = null;
        }
    }

    /**
     * Sending connection and opening an output stream to server by pre-defined instance variable url
     *
     * @param isPost boolean - indicates whether this request should be sent in POST method
     * @throws IOException - should be checked by caller
     */
    private void prepareAll(boolean isPost) throws IOException {
        con = (HttpURLConnection) url.openConnection();
        if (isPost) {
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            os = con.getOutputStream();
        } else {
            con.setRequestMethod("GET");
        }
        con.setConnectTimeout(1000);
    }

    //prepare request in GET method
    //@return HttpRequest this instance -> for chaining method @see line 22
    public HttpRequest prepare() throws IOException {
        prepareAll(false);
        return this;
    }

    //prepare request in POST method
    //@return HttpRequest this instance -> for chaining method @see line 22
    public HttpRequest preparePost() throws IOException {
        prepareAll(true);
        return this;
    }

    /**
     * Writes query to open stream to server
     *
     * @param query String params in format of key1=v1&key2=v2 to open stream to server
     * @return HttpRequest this instance -> for chaining method @see line 22
     * @throws IOException - should be checked by caller
     */
    public HttpRequest withData(String query) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(query);
        writer.close();
        return this;
    }

    /**
     * Builds query on format of key1=v1&key2=v2 from given hashMap structure
     * for map: {name=Bubu, age=29} -> builds "name=Bubu&age=29"
     * for map: {Iam=Groot} -> builds "Iam=Groot"
     * <p/>
     * //@param params HashMap consists of key-> value pairs to build query from
     *
     * @return HttpRequest this instance -> for chaining method @see line 22
     * @throws IOException - should be checked by caller
     */
    public HttpRequest withData(HashMap<String, String> params) throws IOException {
        String result = "", and = "";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result += and + entry.getKey() + "=" + entry.getValue();//concats: key=value (for first param) OR &key=value(second and more)
            if (and.isEmpty())
                and = "&";//& between params, except first so added after first concatenation
        }
        withData(result);
        return this;
    }

    //When caller only need to send, and don't need String response from server
    public boolean send() throws IOException {
        boolean status = con.getResponseCode() == HttpURLConnection.HTTP_OK;//Http OK == 200
        done();
        return status; //boolean return to indicate whether it successfully sent
    }

    /**
     * Sending request to the server and pass to caller String as it received in response from server
     *
     * @return String printed from server's response
     * @throws IOException - should be checked by caller
     */
    public String sendAndReadString() throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            done();
            return response.toString();
        } catch (IOException ioe) {
            throw new UnsupportedOperationException(ioe);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ioe) {
                    Log.e(LOG_TAG, "RestfulRequest: ", ioe);
                }
            }
            done();
        }
    }

    //JSONObject representation of String response from server
    public JSONObject sendAndReadJSON() throws JSONException, IOException {
        return new JSONObject(sendAndReadString());
    }
}
