package sharetest.com.coinwallet;;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;

import AdapterClasses.WatchlistAdapter;


public class AddCurrencyWatchlist extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView mRecyclerView;
    public EditText search;
    private List<String> list = new ArrayList<String>();
    public WatchlistAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_currency_watchlist);

        search = (EditText) findViewById( R.id.search);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        countryList();  // in this method, Create a list of items.

        // call the adapter with argument list of items and context.
        mAdapter = new WatchlistAdapter(list,AddCurrencyWatchlist.this);
        mRecyclerView.setAdapter(mAdapter);

        addTextListener();
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
                mAdapter = new WatchlistAdapter(filteredList, AddCurrencyWatchlist.this);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed
            }
        });
    }
}
