package AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;

import Coinclasses.Currency.*;
import sharetest.com.coinwallet.R;

/**
 * Created by guptapc on 26/01/18.
 */
public class WatchlistAdapter extends BaseAdapter {

    HashMap<String, CurrencySnapShot> watchlists;
    private String[] mKeys;
    private static LayoutInflater inflater=null;
    Context mContext;

    public WatchlistAdapter(Context context, HashMap<String, CurrencySnapShot> watchlists) {


        mContext=context;
        this.watchlists=watchlists;
        mKeys = this.watchlists.keySet().toArray(new String[watchlists.size()]);
    }


    @Override
    public int getCount() {
        return watchlists.size();
    }

    @Override
    public Object getItem(int position) {
        return watchlists.get(mKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi=convertView;


        if(convertView==null){

            LayoutInflater inflater = LayoutInflater.from(mContext);
            vi = inflater.inflate(R.layout.currencyitem_listview,parent,false);
        }

        TextView currency_name = (TextView)vi.findViewById(R.id.currency_name);
        TextView currency_exchange = (TextView)vi.findViewById(R.id.currency_code);
        TextView net=(TextView)vi.findViewById(R.id.Net);
        TextView currency_value = (TextView)vi.findViewById(R.id.currency_value);

        String key = mKeys[position];
        CurrencySnapShot value= watchlists.get(key);

        currency_name.setText(key);
        currency_exchange.setText("Global average INR/USD");
        currency_value.setText(String.valueOf(value.getValueInUSD()));

        return vi;
    }


}
