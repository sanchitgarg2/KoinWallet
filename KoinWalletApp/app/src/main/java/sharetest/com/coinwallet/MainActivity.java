package sharetest.com.coinwallet;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import Coinclasses.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public String bitcoinURL ="https://api.cryptonator.com/api/full/btc-usd";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */


        ObjectMapper mapper = new ObjectMapper();
        User sanchit= new User();
        String user=null;
        try {
           user =new getCurrentValue().execute(bitcoinURL).get().toString();
            sanchit = mapper.readValue(user, User.class);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        EditText email = (EditText) findViewById(R.id.editText1);
        EditText cash =(EditText) findViewById(R.id.editText2);
        EditText phone = (EditText) findViewById(R.id.editText3);
        EditText wallet = (EditText) findViewById(R.id.editText4);

        email.setText(sanchit.getEmailID());
        cash.setText(String.valueOf(sanchit.getLiquidCashInWallet()));
        phone.setText(sanchit.getPhoneNumber());
        //email.setText(sanchit.);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Class with extends AsyncTask class

    private class getCurrentValue  extends AsyncTask<String, Void, String> {

        // Required initialization


        private final OkHttpClient client = new OkHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(MainActivity.this);
        String data ="";
        int sizeData = 0;



      /*
        protected void onPreExecute() {
            // NOTE: You can call UI Element here.

            //Start Progress Dialog (Message)

            Dialog.setMessage("Please wait..");
            Dialog.show();

            try{
                // Set Request parameter
                data +="&" + URLEncoder.encode("data", "UTF-8") + "=";

            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        */

        // Call after onPreExecute method
        protected String doInBackground(String... urls) {
            String user="{\"wallet\":{\"sections\":{\"XRP\":{\"currency\":{\"name\":\"Ripple\",\"currencyCode\":\"XRP\"},\"currentBalance\":75.0,\"cashInvested\":15000.0,\"cashRedeemed\":0.0},\"INR\":{\"currency\":{\"name\":\"Rupee\",\"currencyCode\":\"INR\"},\"currentBalance\":75.0,\"cashInvested\":75.0,\"cashRedeemed\":0.0}},\"transactionList\":null},\"phoneNumber\":\"8147325346\",\"emailID\":\"sanchitgarg2@gmail.com\",\"liquidCashInWallet\":10.0,\"userid\":10}\n";
            Request.Builder builder = new Request.Builder();
            builder.url(urls[0]);
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    return null;
                }
                String message=response.body().string();

                return user;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Log.d("Response", s);

                try {

                    JSONObject json = new JSONObject(s);

                    //basecurrency.setText(json.getJSONObject("ticker").getString("base"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }



    }


}

