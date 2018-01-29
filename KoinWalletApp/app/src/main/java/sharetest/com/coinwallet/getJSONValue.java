package sharetest.com.coinwallet;

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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by guptapc on 09/01/18.
 */

// Class with extends AsyncTask class

public class getJSONValue extends AsyncTask<String, Void, String> {

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

    public getJSONValue (Context context){
        mContext = context;
    }

    // Call after onPreExecute method
    protected String doInBackground(String... urls) {
        String user="{\"wallet\":{\"sections\":{\"XRP\":{\"currency\":{\"name\":\"Ripple\",\"currencyCode\":\"XRP\"},\"currentBalance\":75.0,\"cashInvested\":15000.0,\"cashRedeemed\":0.0},\"INR\":{\"currency\":{\"name\":\"Rupee\",\"currencyCode\":\"INR\"},\"currentBalance\":75.0,\"cashInvested\":75.0,\"cashRedeemed\":0.0}},\"transactionList\":null},\"phoneNumber\":\"8147325346\",\"emailID\":\"sanchitgarg2@gmail.com\",\"liquidCashInWallet\":10.0,\"userid\":10}\n";
        user = "{\"wallet\":{\"sections\":{\"SUB\":{\"currency\":{\"name\":\"Substratum\",\"currencyCode\":\"SUB\"},\"currentBalance\":40.0,\"cashInvested\":1600.0,\"cashRedeemed\":0.0},\"XRP\":{\"currency\":{\"name\":\"Ripple\",\"currencyCode\":\"XRP\"},\"currentBalance\":200.0,\"cashInvested\":40000.0,\"cashRedeemed\":0.0},\"ETH\":{\"currency\":{\"name\":\"Etherium\",\"currencyCode\":\"ETH\"},\"currentBalance\":100000.0,\"cashInvested\":1.0E10,\"cashRedeemed\":0.0},\"XLM\":{\"currency\":{\"name\":\"Stellar Lumens\",\"currencyCode\":\"XLM\"},\"currentBalance\":15.0,\"cashInvested\":225.0,\"cashRedeemed\":0.0},\"XVG\":{\"currency\":{\"name\":\"Verge\",\"currencyCode\":\"XVG\"},\"currentBalance\":26.0,\"cashInvested\":676.0,\"cashRedeemed\":0.0},\"TRX\":{\"currency\":{\"name\":\"Tron\",\"currencyCode\":\"TRX\"},\"currentBalance\":6.0,\"cashInvested\":36.0,\"cashRedeemed\":0.0},\"INR\":{\"currency\":{\"name\":\"Rupee\",\"currencyCode\":\"INR\"},\"currentBalance\":1.0,\"cashInvested\":1.0,\"cashRedeemed\":0.0}},\"transactionList\":null},\"phoneNumber\":\"8147325346\",\"emailID\":\"sanchitgarg2@gmail.com\",\"liquidCashInWallet\":10.0,\"userid\":10}";
        Request.Builder builder = new Request.Builder();
        builder.url(urls[0]);
        Request request = builder.build();

        try {

            String message=null;
            Response response = null;

            if(isNetworkConnected()&&isInternetAvailable()){
                response=client.newCall(request).execute();
            }

            if (response!=null && !response.isSuccessful()) {
                return user;
            }
            if(response!=null&&response.body()!=null){
                message=response.body().string();
                Log.d("RESPONSE FROM NET", message);
                return message;
            }
            else{
                Log.d("RESPONSE NO NET", user);
            }

            return user;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Failed to  CONNECT","NET problem"+e.toString());
            return user;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onPostExecute(s);
        if (s != null) {
            try {

                JSONObject json = new JSONObject(s);

                //basecurrency.setText(json.getJSONObject("ticker").getString("base"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

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