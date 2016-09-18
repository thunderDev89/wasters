package com.acemen.android.wasters.restful;

import android.content.Context;
import android.util.Log;

import com.acemen.android.wasters.Adresse;
import com.acemen.android.wasters.event.Event;
import com.acemen.android.wasters.restful.helper.ApiClient;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Audrik ! on 21/08/2016.
 */
public class SendWastesRequest /*implements IRequest*/ {
    /**
     * TODO
     * - Make this class implements Callback from okHttp
     * - Ajouter un Interceptor pour zipper les requêtes d'envoi d'images au serveur
     * - Ajouter un cache
     * -
     */
    private static final String LOG_TAG = SendWastesRequest.class.getSimpleName();
    private static final int HTTP_OK = 200;
    private final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";

    public static final int EVENT_GET_WASTES_ID = 101;

    private final static String ROUTE = "api/v1/waste";
    static final String PARAM_WASTE_TYPE = "type";
    static final String PARAM_FILENAME = "fileName";
    static final String PARAM_ADRESSE = "address";
    static final String PARAM_LATITUDE = "latitude";
    static final String PARAM_LONGITUDE = "longitude";

    private Context context;
    private String filepath;
    private String wasteType;
    private Adresse adresse;

    private OkHttpClient client = null;

    public SendWastesRequest(Context context){ this.context = context; }

    public SendWastesRequest(Context context, String filepath, String wasteType, Adresse adresse) {
        this.context = context;
        this.filepath = filepath;
        this.wasteType = wasteType;
        this.adresse = adresse;
        Log.d(LOG_TAG, adresse.toString());
    }

    private String getFilename() {
        return filepath.substring(filepath.lastIndexOf("/")+1);
    }

    public JSONObject execute() {
        String filename = getFilename();
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", filename,
                        RequestBody.create(MediaType.parse("image/png"), new File(filepath.substring(5))))
                .addFormDataPart("numFiles", "1")
                .addFormDataPart(PARAM_FILENAME, filename)
                .addFormDataPart(PARAM_WASTE_TYPE, wasteType)
                .addFormDataPart(PARAM_ADRESSE, adresse.getAdresse())
                .addFormDataPart(PARAM_LONGITUDE, adresse.getLongitude()+"")
                .addFormDataPart(PARAM_LATITUDE, adresse.getLatitude()+"")
                .addFormDataPart("Authorization", ""+Encrypt.encryptAuthorizationBasic())
                .build();

        Log.d(LOG_TAG, "Params sent ->\n " +
                "filepath="+filepath+
                ", filename="+filename+
                ", type="+wasteType+
                ", long="+adresse.getLongitude()+
                ", lat="+adresse.getLatitude());

        Headers header = new Headers.Builder()
                .add("User-Agent", USER_AGENT)
                .add("Referer", ApiClient.BASE_URL)
                .add("Accept", "application/json")
                .add("Authorization", Encrypt.encryptAuthorizationBasic().replace("\n", ""))
                .build();

        Request request = new Request.Builder()
                .url(Encrypt.encryptUrl("POST", ROUTE))
                .headers(header)
                .post(body)
                .build();

        Log.d(LOG_TAG, "request=" + request.toString());

        Call postCall = getClient().newCall(request);
        postCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(LOG_TAG, "Error when sending request", e);
                EventBus.getDefault().post(new Event("Error", "Error when sending request. Please retry"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == HTTP_OK) {
                    Log.d(LOG_TAG, "Request successfully sent");
                    EventBus.getDefault().post(new Event("Success", "Request sent."
                    +"\n"+response.body().string()));
                } else {
                    Log.e(LOG_TAG, "Response code = "+response.code());
                    EventBus.getDefault().post(new Event("Error", "Response code = "+response.code()+" : "+response.body().toString()));
                }
                response.body().close();
            }
        });
        return null;
    }

    public JSONObject get() {
        Headers header = new Headers.Builder()
                .add("User-Agent", USER_AGENT)
                .add("Referer", ApiClient.BASE_URL)
                .add("Accept", "application/json")
                .add("Authorization", Encrypt.encryptAuthorizationBasic().replace("\n", ""))
                .build();

        Request getRequest = new Request.Builder()
                .url(Encrypt.encryptUrl("GET",ROUTE))
                .headers(header)
                .get()
                .build();

        Call getCall = getClient().newCall(getRequest);
        getCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(LOG_TAG, "Error when getting wastes", e);
                EventBus.getDefault().post(new Event("Error", "Error when getting wastes. Please retry"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == HTTP_OK) {
                    Log.d(LOG_TAG, "Request successfully sent");
                    String body = response.body().string();
                    Log.d(LOG_TAG, "reponse json ->\n" + body);
                    EventBus.getDefault().post(new Event("Success", body, EVENT_GET_WASTES_ID));
                } else {
                    Log.e(LOG_TAG, "Response code = "+response.code());
                    EventBus.getDefault().post(new Event("Error", "Response code = "+response.code()+" : "+response.body().toString()));
                }
                response.body().close();
            }
        });
        return null;
    }

    public static ArrayList<OverlayItem> getWastes(String jsonStr) {
        ArrayList<OverlayItem> wastes = null;
        try {
            JSONArray jsonArray = new JSONObject(jsonStr).getJSONObject("data").getJSONArray("list");
            wastes = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                wastes.add(new OverlayItem(
                        json.getString(PARAM_WASTE_TYPE),
                        json.getString(PARAM_ADRESSE),
                        new GeoPoint(
                                json.getDouble(PARAM_LATITUDE),
                                json.getDouble(PARAM_LONGITUDE)
                        )
                ));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "getWastes() - Error when parsing JSON", e);
        }

        return wastes;
    }

    private OkHttpClient getClient() {
        if (client == null) {
            // Assigning a cache dir
            File myCacheDir = new File(context.getCacheDir(), "OkHttpCache");
            // Creation of cache file
            int cacheSize = 1024 * 1024;
            Cache cacheDir = new Cache(myCacheDir, cacheSize);
            client = new OkHttpClient.Builder()
                    .cache(cacheDir)
//                    .addInterceptor(getInterceptor())
                    .build();
        }
        return client;
    }

    public static class Builder {
        private Context context;
        private String filepath;
        private String wasteType;
        private Adresse adresse;

        public Builder addContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder addFilePath(String filepath) {
            this.filepath = filepath;
            return this;
        }

        public Builder addWasteType(String wasteType) {
            this.wasteType = wasteType;
            return this;
        }

        public Builder addAdresse(Adresse adresse) {
            this.adresse = adresse;
            return this;
        }

        public SendWastesRequest build() {
            if (context == null) {
                throw new UnsupportedOperationException("Context shouldn't be null");
            }

            //TODO check file exists
//            File file = new File(filepath);
//            if (!file.exists()) {
            if (filepath == null) {
                throw new UnsupportedOperationException("Capture file not exists = "+filepath);
            }

            //TODO check wasteType is not null
            if (wasteType == null) {
                throw new UnsupportedOperationException("Waste type shouldn't be null");
            }

            //TODO check adresse is not null
            if (adresse == null) {
                throw new UnsupportedOperationException("Adresse shouldn't be null");
            } else if (adresse.getAdresse() == null) {
                throw new UnsupportedOperationException("Adresse name shouldn't be null");
            }

            return new SendWastesRequest(context, filepath, wasteType, adresse);
        }
    }
}
