package AdapterClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import sharetest.com.coinwallet.R;

/**
 * Created by guptapc on 20/01/18.
 */

public class WatchlistAdapter extends
        RecyclerView.Adapter<WatchlistAdapter.MyViewHolder> {

    private List<String> list_item ;
    public Context mcontext;



    public WatchlistAdapter(List<String> list, Context context) {

        list_item = list;
        mcontext = context;
    }

    // Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    @Override
    public WatchlistAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a layout
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.watchlist_item, null);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    // Called by RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position ) {


        viewHolder.country_name.setText(list_item.get(position));

        viewHolder.country_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mcontext, list_item.get(position),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    // initializes textview in this class
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView country_name;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            country_name = (TextView) itemLayoutView.findViewById(R.id.country_name);

        }
    }

    //Returns the total number of items in the data set hold by the adapter.
    @Override
    public int getItemCount() {
        return list_item.size();
    }

}
