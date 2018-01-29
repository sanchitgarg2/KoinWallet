package sharetest.com.coinwallet;;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import AdapterClasses.SearchCurrencyAdapter;
import AdapterClasses.WatchlistAdapter;
import Coinclasses.Currency;
import Coinclasses.User;
import Coinclasses.WalletSection;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.CurrencyListURL;
import static SupportingClasses.Helper.UserURL;
import static SupportingClasses.Helper.updatewatchlistURL;


public class AddCurrencyWatchlist extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView mRecyclerView;
    public EditText search;
    private List<String> list = new ArrayList<String>();
    public SearchCurrencyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_currency_watchlist);

        getCurrencyList();
        search = (EditText) findViewById( R.id.search);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //countryList();  // in this method, Create a list of items.

        // call the adapter with argument list of items and context.
        mAdapter = new SearchCurrencyAdapter(list,AddCurrencyWatchlist.this);
        mRecyclerView.setAdapter(mAdapter);

        addTextListener();
    }



    public void getCurrencyList(){

        ObjectMapper mapper = new ObjectMapper();
        String currencylists=null;

        try {
            currencylists =new getJSONValue(AddCurrencyWatchlist.this).execute(AppURL+CurrencyListURL).get();

            if(currencylists!=null) {
                JSONObject jObject = new JSONObject(currencylists);
                Iterator<?> keys = jObject.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    list.add(key);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
    // this method is used to create list of items.
    public void countryList(){

        list.add("Bitcoin");
        list.add("Etherium");
        list.add("Ripple");
        list.add("NEO");
        list.add("Litecoin");
        list.add("NEO");
        list.add("NEM");
        list.add("DASH");
        list.add("EOS");
        list.add("IOTA");
        list.add("RaiBlocks");
        list.add("Tether");
        list.add("Bytecoin");
        list.add("Walton");
        list.add("MonaCoin");
        list.add("Power Ledger");
    }

    public void addTextListener(){

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                final List<String> filteredList = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {

                    final String text = list.get(i).toLowerCase();
                    if (text.contains(query)) {

                        filteredList.add(list.get(i));
                    }
                }

                mRecyclerView.setLayoutManager(new LinearLayoutManager(AddCurrencyWatchlist.this));
                mAdapter = new SearchCurrencyAdapter(filteredList, AddCurrencyWatchlist.this);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });
    }
}
