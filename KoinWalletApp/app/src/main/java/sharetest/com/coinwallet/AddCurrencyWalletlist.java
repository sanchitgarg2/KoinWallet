package sharetest.com.coinwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import AdapterClasses.SearchCurrencyAdapter;
import AdapterClasses.SearchCurrencyWatchlistAdapter;
import Coinclasses.Currency;
import Coinclasses.CurrencyCode;
import Coinclasses.Currency.*;
import Coinclasses.WalletSection;

import static SupportingClasses.Helper.CURRENCYLIST;
import static SupportingClasses.Helper.WALLETSECTION;

/**
 * Created by guptapc on 04/02/18.
 */

public class AddCurrencyWalletlist extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {


    private RecyclerView mRecyclerView;
    private EditText search;
    private ListView listView;

    private List<CurrencyCode> list = new ArrayList<CurrencyCode>();
    public SearchCurrencyAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_walletlist);

        try {
            getCurrencyList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        search = (EditText) findViewById( R.id.search2);
        listView = (ListView) findViewById(R.id.listview_wallet_coinSearch);

        final SearchCurrencyWatchlistAdapter adapter= new SearchCurrencyWatchlistAdapter(AddCurrencyWalletlist.this,list);
        listView.setAdapter((ListAdapter) adapter);

        addTextListener();
        addItemListener();

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public void getCurrencyList() throws JSONException {

        Set entrySet = CURRENCYLIST.entrySet();
        Iterator it = entrySet.iterator();

        while(it.hasNext()){
            Map.Entry me = (Map.Entry)it.next();
            CurrencyCode currencyCode=new CurrencyCode();

            CurrencySnapShot snapShot=(CurrencySnapShot)(me).getValue();

            currencyCode.setCurrencyCode(me.getKey().toString());
            currencyCode.setCurrencyName(snapShot.getCurrencyName());
            list.add(currencyCode);
        }

    }

    public void addItemListener() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CurrencyCode code = (CurrencyCode) parent.getItemAtPosition(position);
                WalletSection walletSection= new WalletSection();
                walletSection.setCurrency(new Currency());
                walletSection.getCurrency().setCurrencyCode(code.getCurrencyCode());
                walletSection.getCurrency().setName(code.getCurrencyName());
                WALLETSECTION= walletSection;

                Intent intent= new Intent(AddCurrencyWalletlist.this, AddTransaction.class);
                startActivity(intent);
            }
        });

    }
    public void addTextListener(){

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                final List<CurrencyCode> filteredList = new ArrayList<>();

                for (int i = 0; i < list.size(); i++) {

                    final String currencyCode = list.get(i).getCurrencyCode().toLowerCase();
                    final String currencyName =list.get(i).getCurrencyName().toLowerCase();

                    if (currencyCode.contains(query)||currencyName.contains(query)) {

                        filteredList.add(list.get(i));
                    }
                }
                SearchCurrencyWatchlistAdapter madapter= new SearchCurrencyWatchlistAdapter(AddCurrencyWalletlist.this,filteredList);
                listView.setAdapter( madapter);

            }
        });
    }

    @Override
    public void onClick(View v) {



    }
}
