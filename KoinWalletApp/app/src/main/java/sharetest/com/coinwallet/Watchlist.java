package sharetest.com.coinwallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import AdapterClasses.CoinAdapter;
import AdapterClasses.WatchlistAdapter;
import Coinclasses.CoinWallet;
import Coinclasses.Currency;
import Coinclasses.Currency.*;
import Coinclasses.User;
import Coinclasses.WalletSection;
import sharetest.com.coinwallet.R;
import sharetest.com.coinwallet.postJSONValue;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.UserURL;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.watchlistURL;

public class Watchlist extends Fragment {

    public Watchlist() {
        // Required empty public constructor
    }


    private ListView listView;
    private Context rootContext;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.watchlist, container, false);

        listView = (ListView)v.findViewById(R.id.list_view_watchlist);

        rootContext=v.getContext();
        rootView=v;

        getWatchlist();

        FloatingActionButton myFab = (FloatingActionButton)v.findViewById(R.id.addCurrency);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(rootContext, AddCurrencyWatchlist.class);
                startActivity(intent);
            }
        });


        return v;
    }

    private void setListViewData(HashMap<String, CurrencySnapShot> watchlists) {

        WatchlistAdapter adapter = new WatchlistAdapter(rootContext,watchlists);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
    public  void getWatchlist(){
        HashMap<String, CurrencySnapShot> watchlistmap = new HashMap<String, CurrencySnapShot>();
        //List<HashMap<String, CurrencySnapShot>> watchlist= new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        String watchlists=null;

        try {
            watchlists =new postJSONValue(rootContext).execute(AppURL+watchlistURL,Integer.toString(AppuserID)).get();


            if(watchlists!=null) {
                JSONObject jObject = new JSONObject(watchlists);
                Iterator<?> keys = jObject.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    String value = jObject.getString(key);
                    JSONObject newJObject = new JSONObject(value);
                    CurrencySnapShot snapshot=new CurrencySnapShot(Float.parseFloat(newJObject.get("valueInUSD").toString()),
                            Float.parseFloat(newJObject.get("valueInINR").toString()),newJObject.get("refreshTime").toString());

                    watchlistmap.put(key, snapshot);

                }

            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if(watchlists!=null){

            setListViewData(watchlistmap);
        }
    }





}
