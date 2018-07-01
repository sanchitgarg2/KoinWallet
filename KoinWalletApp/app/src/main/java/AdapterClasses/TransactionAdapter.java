package AdapterClasses;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import Coinclasses.Transaction;
import sharetest.com.coinwallet.R;

/**
 * Created by guptapc on 26/01/18.
 */

public class TransactionAdapter extends BaseAdapter {

    List<Transaction> transactions = null;
    private static LayoutInflater inflater=null;
    Context mContext;

    public TransactionAdapter(Context context, List<Transaction> transactions) {


        mContext=context;
        this.transactions=transactions;
    }

    public List<Transaction> transactions() {

        return transactions;
    }
    public void setSections(List<Transaction> transactions) {

        this.transactions = transactions;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi=convertView;


        if(convertView==null){

            LayoutInflater inflater = LayoutInflater.from(mContext);
            vi = inflater.inflate(R.layout.transactionitem_listview,parent,false);
        }

        TextView price_type = (TextView)vi.findViewById(R.id.buyprice);
        TextView price_per_value = (TextView)vi.findViewById(R.id.buyprice_value);
        TextView trading_pair_value=(TextView)vi.findViewById(R.id.market_value_body);
        TextView quantity_value = (TextView)vi.findViewById(R.id.net_value_body);
        TextView cost_value = (TextView)vi.findViewById(R.id.costvalue);
        TextView worth_value = (TextView)vi.findViewById(R.id.worth_value);
        TextView net_value = (TextView)vi.findViewById(R.id.netvalue);
        RelativeLayout transactionbox =(RelativeLayout)vi.findViewById(R.id.transaction_box);

        Transaction transaction = transactions.get(position);
        price_per_value.setText(Float.toString(transaction.getRate()));


        trading_pair_value.setText(transaction.getIncomingCurrency().getCurrencyCode()+"/" +transaction.getOutgoingCurrency().getCurrencyCode());


        if(transaction.getPurchaseQuantity()>=0){
            transactionbox.setBackgroundColor(mContext.getResources().getColor(R.color.primary_color));
            quantity_value.setText(Float.toString(transaction.getPurchaseQuantity()));
            cost_value.setText(Float.toString(Float.valueOf(transaction.getRate())*Float.valueOf(transaction.getPurchaseQuantity())));
        }
        else {
            transactionbox.setBackgroundColor(mContext.getResources().getColor(R.color.accent_color));
            price_type.setText("SELL Price");
            quantity_value.setText(Float.toString(transaction.getPurchaseQuantity()*-1));
            cost_value.setText(Float.toString(Float.valueOf(transaction.getRate())*Float.valueOf(transaction.getPurchaseQuantity())*-1));
        }

        return vi;
    }


}
