package sharetest.com.coinwallet;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import Coinclasses.Currency;
import Coinclasses.Currency.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.CURRENCYLIST;
import static SupportingClasses.Helper.CurrencyListURL;
import static SupportingClasses.Helper.USER;

/**
 * Created by guptapc on 17/02/18.
 */

public class CurrencyBackground extends IntentService {

    public CurrencyBackground() {
        super("CurrencyBackground");
    }

    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the task here
        Log.i("Fetching currency List", "-------------------------------");
        String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        Log.i("Alarm",mydate);

        Request.Builder builder = new Request.Builder();
        builder.url(AppURL + CurrencyListURL);
        Request request = builder.build();

        Response response = null;
        try {
            if(isNetworkConnected()&&isInternetAvailable()) {
                response = client.newCall(request).execute();
            }
            if(response!=null&&response.body()!=null){
                String message=response.body().string();
                Log.i("BackgroundCurrency", message);
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(message);

                String reasonCode=json.get("statusCode").toString();
                if (reasonCode.equals("200")) {
                    CURRENCYLIST = getCurrency(json.get("currencyList").toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    public HashMap<String,CurrencySnapShot> getCurrency(String jsoncurrency) throws JSONException {

        HashMap<String, CurrencySnapShot> currencylistmap = new HashMap<String, CurrencySnapShot>();
        if(jsoncurrency!=null) {
            org.json.JSONObject jObject = new org.json.JSONObject(jsoncurrency);
            Iterator<?> keys = jObject.keys();


            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = jObject.getString(key);
                org.json.JSONObject newJObject = new org.json.JSONObject(value);
                Currency.CurrencySnapShot snapshot = new Currency.CurrencySnapShot(Float.parseFloat(newJObject.get("valueInINR").toString()),Float.parseFloat(newJObject.get("valueInUSD").toString())
                        , newJObject.get("refreshTime").toString()
                        ,newJObject.get("currencyCode").toString(),newJObject.get("currencyName").toString());

                currencylistmap.put(key, snapshot);

            }
        }
        return  currencylistmap;
    }

    private boolean isNetworkConnected() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();

    }
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}
