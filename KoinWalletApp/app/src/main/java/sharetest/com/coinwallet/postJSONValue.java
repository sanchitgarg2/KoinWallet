package sharetest.com.coinwallet;

import Coinclasses.WalletSection;
import okhttp3.MediaType;
import okhttp3.Request;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.Optional;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by guptapc on 09/01/18.
 */

// Class with extends AsyncTask class

public class postJSONValue extends AsyncTask<String, Void, String> {

    // Required initialization

    private Context mContext;
    private final OkHttpClient client = new OkHttpClient();
    private String Content;
    private String Error = null;
    private ProgressDialog dialog ;
    String data ="";
    int sizeData = 0;




    protected void onPreExecute() {
        // NOTE: You can call UI Element here.

        //Start Progress Dialog (Message)

        dialog=new ProgressDialog(mContext);
        dialog.setMessage("Please wait..");
        dialog.show();

        try{
            // Set Request parameter
            data +="&" + URLEncoder.encode("data", "UTF-8") + "=";

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public postJSONValue (Context context){
        mContext = context;
    }

    // Call after onPreExecute method
    protected String doInBackground(String... urls) {

        final MediaType MEDIA_TYPE = MediaType.parse("application/json");
        JSONObject postdata = new JSONObject();
        try {
            if(urls.length>1 && urls[1]!=null) {
                postdata.put("userID", urls[1]);
            }
            if(urls.length>2 && urls[2]!=null) {
                postdata.put("currencyCode", urls[2]);
            }
            if(urls.length>3 && urls[3]!=null) {
                postdata.put("price", urls[3]);
            }
            if(urls.length>4 && urls[4]!=null) {
                postdata.put("quantity", urls[4]);
            }
        } catch(JSONException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        final Request request = new Request.Builder()
                .url(urls[0])
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Your Token")
                .addHeader("cache-control", "no-cache")
                .build();

        try {

            String message=null;
            Response response = null;

            if(isNetworkConnected()&&isInternetAvailable()){
                response=client.newCall(request).execute();
            }

            if(response!=null&&response.body()!=null){
                message=response.body().string();
                Log.d("RESPONSE FROM NET", message);
                return message;
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Failed to  CONNECT","NET problem"+e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onPostExecute(s);


    }

    private boolean isNetworkConnected() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE));
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
