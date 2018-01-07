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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public String bitcoinURL ="https://api.cryptonator.com/api/full/btc-usd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        new getCurrentValue().execute(bitcoinURL);

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
        EditText basecurrency = (EditText) findViewById(R.id.editText1);
        EditText targetcurrency = (EditText) findViewById(R.id.editText2);
        EditText price = (EditText) findViewById(R.id.editText3);
        EditText volume = (EditText) findViewById(R.id.editText4);


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

            Request.Builder builder = new Request.Builder();
            builder.url(urls[0]);
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    return null;
                }
                String message=response.body().string();

                return message;

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
                    basecurrency.setText(json.getJSONObject("ticker").getString("base"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }



    }


}

