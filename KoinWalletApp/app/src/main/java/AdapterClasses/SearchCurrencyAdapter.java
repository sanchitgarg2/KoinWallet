package AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

import sharetest.com.coinwallet.AddTransaction;
import sharetest.com.coinwallet.MainActivity;
import sharetest.com.coinwallet.R;
import sharetest.com.coinwallet.SectionDisplay;
import sharetest.com.coinwallet.postJSONValue;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.updatewatchlistURL;
import static android.support.v4.content.ContextCompat.startActivity;

public class SearchCurrencyAdapter extends
        RecyclerView.Adapter<SearchCurrencyAdapter.MyViewHolder> {

    private List<String> list_item ;
    public Context mcontext;



    public SearchCurrencyAdapter(List<String> list, Context context) {

        list_item = list;
        mcontext = context;
    }

    // Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    @Override
    public SearchCurrencyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a layout
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item, null);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }




    // Called by RecyclerView to display the data at the specified position.
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position ) {


        viewHolder.currency_name.setText(list_item.get(position));

        viewHolder.currency_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String response =new postJSONValue(mcontext).execute(AppURL+updatewatchlistURL,Integer.toString(AppuserID),list_item.get(position)).get();
                    if(response.equals("true")){
                        Intent intent = new Intent(mcontext, MainActivity.class);
                        mcontext.startActivity(intent);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Toast.makeText(mcontext, list_item.get(position),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    // initializes textview in this class
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView currency_name;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            currency_name = (TextView) itemLayoutView.findViewById(R.id.currency_name);

        }
    }

    //Returns the total number of items in the data set hold by the adapter.
    @Override
    public int getItemCount() {
        return list_item.size();
    }

}