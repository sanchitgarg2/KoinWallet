package AdapterClasses;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import Coinclasses.Currency.*;
import sharetest.com.coinwallet.R;

/**
 * Created by guptapc on 27/02/18.
 */

public class WatchlistRecyclerAdapter extends RecyclerView.Adapter<WatchlistRecyclerAdapter.ViewHolder> {




    HashMap<String, CurrencySnapShot> watchlists;
    private String[] mKeys;
    private static LayoutInflater inflater=null;
    Context mContext;
    public RecyclerViewClickListener mListener;

    public WatchlistRecyclerAdapter(Context context, HashMap<String, CurrencySnapShot> watchlists, RecyclerViewClickListener mListener) {


        mContext=context;
        this.watchlists=watchlists;
        this.mListener=mListener;
        mKeys = this.watchlists.keySet().toArray(new String[watchlists.size()]);
    }

    // Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    @Override
    public WatchlistRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        // create a layout
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.currencyitem_listview, null);

        ViewHolder ViewHolder = new ViewHolder(view,mListener);
        return ViewHolder;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position ) {


        String key = mKeys[position];
        CurrencySnapShot value= watchlists.get(key);

        viewHolder.currency_name.setText(key);
        viewHolder.currency_exchange.setText("Global average INR/USD");
        viewHolder.currency_value.setText(String.valueOf(value.getValueInUSD()));

        String currencyName= value.getCurrencyCode().toLowerCase();
        Integer x=mContext.getResources().getIdentifier(currencyName, "drawable", mContext.getPackageName());
        viewHolder.currency_image.setImageResource(x);


    }


    // initializes textview in this class
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public RelativeLayout currency_box;
        public TextView currency_name;
        public  TextView currency_exchange;
        public TextView net;
        public TextView currency_value;
        public ImageView currency_image;
        public RecyclerViewClickListener mListener;



        public ViewHolder(View itemLayoutView, RecyclerViewClickListener listener) {
            super(itemLayoutView);

            //currency_box = (RelativeLayout) itemLayoutView.findViewById(R.id.currency_box);
            //currency_name=(TextView)itemLayoutView.findViewById(R.id.currencylist_name);
            //currency_code=(TextView)itemLayoutView.findViewById(R.id.currencylist_code);

             currency_name = (TextView)itemLayoutView.findViewById(R.id.currencyName);
             currency_exchange = (TextView)itemLayoutView.findViewById(R.id.global_average);
             net=(TextView)itemLayoutView.findViewById(R.id.Net);
             currency_value = (TextView)itemLayoutView.findViewById(R.id.net_value_body);
             currency_image=(ImageView)itemLayoutView.findViewById(R.id.WatchlistCurrencyImage);
             mListener = listener;
             itemLayoutView.setOnClickListener(this);


        }
        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
    //Returns the total number of items in the data set hold by the adapter.
    @Override
    public int getItemCount() {
        return watchlists.size();
    }

}
