package sharetest.com.coinwallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import AdapterClasses.WatchlistAdapter;
import Coinclasses.Currency;
import Coinclasses.Currency.*;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.USER;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.watchlistURL;

public class Watchlist extends Fragment {

    public Watchlist() {
        // Required empty public constructor
    }


    private ListView listView;
    private Context rootContext;
    private View rootView;
    HashMap<String, CurrencySnapShot> watchlistmap = new HashMap<String, CurrencySnapShot>();

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
