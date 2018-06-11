package AdapterClasses;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import Coinclasses.Currency;
import Coinclasses.CurrencyCode;
import Coinclasses.Transaction;
import sharetest.com.coinwallet.R;

/**
 * Created by guptapc on 04/02/18.
 */

public class SearchCurrencyWatchlistAdapter extends BaseAdapter {

    private  Context mcontext;
    public List<CurrencyCode> currencyCodes;

    public SearchCurrencyWatchlistAdapter(Context context, List<CurrencyCode> list) {
        this.currencyCodes=list;
        this.mcontext=context;
    }

    public List<CurrencyCode> getCurrencyCodes(){
        return currencyCodes;
    }
    public void setCurrencyCodes(List<CurrencyCode> currencyCodes) {

        this.currencyCodes = currencyCodes;
    }

    @Override
    public int getCount() {
        return currencyCodes.size();
    }

    @Override
    public CurrencyCode getItem(int position) {
        return currencyCodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;


        if (convertView == null) {

            LayoutInflater inflater = LayoutInflater.from(mcontext);
            vi = inflater.inflate(R.layout.list_item, parent, false);
        }
        RelativeLayout currency_box;
        TextView currency_name;
        TextView currency_code;


        currency_name=(TextView)vi.findViewById(R.id.currencylist_name);
        currency_code=(TextView)vi.findViewById(R.id.currencylist_code);

        CurrencyCode code= getItem(position);
        currency_name.setText( code.getCurrencyName());
        currency_code.setText(code.getCurrencyCode());

        return vi;
    }
}
