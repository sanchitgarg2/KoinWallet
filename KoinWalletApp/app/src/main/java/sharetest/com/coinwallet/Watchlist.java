package sharetest.com.coinwallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import AdapterClasses.RecyclerViewClickListener;
import AdapterClasses.WatchlistAdapter;
import AdapterClasses.WatchlistRecyclerAdapter;
import Coinclasses.Currency;
import Coinclasses.Currency.*;
import Coinclasses.WalletSection;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.USER;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.WALLETSECTION;
import static SupportingClasses.Helper.watchlistURL;
import static SupportingClasses.Helper.SCREEN_PAGE;

public class Watchlist extends Fragment {

    public Watchlist() {
        // Required empty public constructor
    }


    private RecyclerView listView;
    private Context rootContext;
    private View rootView;
    HashMap<String, CurrencySnapShot> watchlistmap = new HashMap<String, CurrencySnapShot>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmen
        View v=inflater.inflate(R.layout.watchlist, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();




        listView = (RecyclerView) v.findViewById(R.id.list_view_watchlist);

        rootContext=v.getContext();
        rootView=v;
        getWatchlist();
        FloatingActionButton myFab = (FloatingActionButton)v.findViewById(R.id.addCurrency);

        if(USER.getWatchList().isEmpty()||watchlistmap.isEmpty()) {
            v = inflater.inflate(R.layout.first_time_watchlist, container, false);
            rootContext = v.getContext();
            myFab = (FloatingActionButton)v.findViewById(R.id.addCurrency3);
        }

        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(rootContext, AddCurrencyWatchlist.class);
                startActivity(intent);
            }
        });
        return v;
    }

    private void setListViewData(final HashMap<String, CurrencySnapShot> watchlists) {


        final String mKeys[] = watchlists.keySet().toArray(new String[watchlists.size()]);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(rootContext));
        listView.addItemDecoration(new DividerItemDecoration(rootContext,LinearLayoutManager.VERTICAL));
        listView.setNestedScrollingEnabled(false);

        RecyclerViewClickListener listener= new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {


                CurrencySnapShot snapshot = (CurrencySnapShot) watchlists.get(mKeys[position]);
                Toast.makeText(rootContext,"currencycliked  "+ snapshot.getCurrencyName(),Toast.LENGTH_SHORT);
                WalletSection walletSection = new WalletSection();
                Currency currency= new Currency();
                currency.setCurrencyCode(snapshot.getCurrencyCode());
                currency.setName(snapshot.getCurrencyName());
                walletSection.setCurrency(currency);
                WALLETSECTION =walletSection;
                SCREEN_PAGE=1;
                Intent intent = new Intent(rootContext, SectionDisplay.class);
                startActivity(intent);
            }
        };


        WatchlistRecyclerAdapter adapter = new WatchlistRecyclerAdapter(rootContext,watchlists,listener);
        listView.setAdapter(adapter);


    }
    public  void getWatchlist(){

        //List<HashMap<String, CurrencySnapShot>> watchlist= new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        String watchlists=null;

        try {
            org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
            obj.put("userID", Integer.toString(AppuserID));

            watchlists =new postJSONValue(rootContext).execute(AppURL+watchlistURL,obj.toJSONString()).get();


            if(watchlists!=null) {

                JSONParser parser = new JSONParser();
                org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(watchlists);

                String reasonCode = json.get("statusCode").toString();

                if (reasonCode.equals("200")) {
                    watchlistmap=getCurrency(json.get("currencyList").toString());
                    setListViewData(watchlistmap);
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
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
                Currency.CurrencySnapShot snapshot = new Currency.CurrencySnapShot(Float.parseFloat(newJObject.get("valueInUSD").toString()),
                        Float.parseFloat(newJObject.get("valueInINR").toString()), newJObject.get("refreshTime").toString()
                        ,newJObject.get("currencyCode").toString(),newJObject.get("currencyName").toString());

                currencylistmap.put(key, snapshot);

            }
        }
        return  currencylistmap;
    }




}
