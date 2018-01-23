package sharetest.com.coinwallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import AdapterClasses.CoinAdapter;
import Coinclasses.CoinWallet;
import Coinclasses.Currency;
import Coinclasses.User;
import Coinclasses.WalletSection;
import SupportingClasses.BottomNavigationViewBehavior;

/**
 * Created by guptapc on 15/01/18.
 */

public class Watchlist extends AppCompatActivity{

    private TextView headerText;
    private ListView listView;
    private View headerView;
    private View headerSpace;
    public String bitcoinURL ="https://api.cryptonator.com/api/full/btc-usd";
    //"http://192.168.0.109:8080/Koinwallet/KoinWallet/getUser?userID=10";
    //"http://192.168.43.122:8080/Koinwallet/KoinWallet/getUser?userID=10";
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.watchlist);

        listView = (ListView) findViewById(R.id.list_view_watchlist);
        setBottomView();

        FloatingActionButton myFab = (FloatingActionButton)findViewById(R.id.addCurrency);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Watchlist.this, AddCurrencyWatchlist.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ObjectMapper mapper = new ObjectMapper();
        User sanchit= new User();
        String user=null;
        try {
            user =new getJSONValue(this).execute(bitcoinURL).get();
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

        Currency.CURRENCYSTATE = new HashMap<String, Currency>();
        for(WalletSection section : sanchit.getWallet().getSections().values())
        {
            Currency c = section.getCurrency();
            Currency.makeNewCurrency(c);
        }

        //TextView currentValue = (TextView) findViewById(R.id.CurrentValue);
        //currentValue.setText("WALLET :" + String.valueOf(sanchit.getLiquidCashInWallet()));
        setListViewData(sanchit.getWallet());

    }
    private void setListViewData(CoinWallet coinWallet) {

        if(coinWallet.getSections()!=null) {
            HashMap<String, WalletSection> sections = coinWallet.getSections();

            CoinAdapter adapter = new CoinAdapter(this, new ArrayList<WalletSection>(sections.values()));
            //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_listview, R.id.item, modelList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WalletSection walletSection = (WalletSection) parent.getItemAtPosition(position);

                    ObjectMapper mapper = new ObjectMapper();
                    String walletJSONString = null;
                    try {
                        walletJSONString = mapper.writeValueAsString(walletSection);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(Watchlist.this, SectionDisplay.class);
                    intent.putExtra("section", walletJSONString);
                    startActivity(intent);

                }
            });




        }

    }
    private void setBottomView() {


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView .getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        bottomNavigationView.setSelectedItemId(R.id.action_watchlist);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Context context= Watchlist.this;
                        Intent intent=null;
                        switch (item.getItemId()) {
                            case R.id.action_watchlist:break;

                            case R.id.action_wallet:
                                intent= new Intent(context, MainActivity.class);
                                startActivity(intent);break;

                            case R.id.action_exchange:
                                intent  = new Intent(context, CurrencyExchange.class);
                                startActivity(intent);break;

                            case R.id.action_settings:
                                intent  = new Intent(context, Settings.class);
                                startActivity(intent);break;

                        }
                        return true;
                    }
                });

    }

}
