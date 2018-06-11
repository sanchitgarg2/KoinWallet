package AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

import Coinclasses.CurrencyCode;
import sharetest.com.coinwallet.MainActivity;
import sharetest.com.coinwallet.R;
import sharetest.com.coinwallet.postJSONValue;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.updatewatchlistURL;

public class SearchCurrencyAdapter extends
        RecyclerView.Adapter<SearchCurrencyAdapter.MyViewHolder> {

    private List<CurrencyCode> list_item ;
    public Context mcontext;



    public SearchCurrencyAdapter(List<CurrencyCode> list, Context context) {

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


        viewHolder.currency_code.setText(list_item.get(position).getCurrencyCode());
        viewHolder.currency_name.setText(list_item.get(position).getCurrencyName());

        viewHolder.currency_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                        JSONObject obj = new JSONObject();
                        obj.put("userID", Integer.toString(AppuserID));
                        obj.put("currencyCode",list_item.get(position).getCurrencyCode());

                    String response =new postJSONValue(mcontext).execute(AppURL+updatewatchlistURL,obj.toJSONString()).get();
                    if(response.equals("true")){
                        Intent intent = new Intent(mcontext, MainActivity.class);
                        mcontext.startActivity(intent);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Toast.makeText(mcontext, list_item.get(position).getCurrencyCode(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    // initializes textview in this class
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout currency_box;
        public TextView currency_name;
        public  TextView currency_code;


        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            currency_box = (RelativeLayout) itemLayoutView.findViewById(R.id.currency_box);
            currency_name=(TextView)itemLayoutView.findViewById(R.id.currencylist_name);
            currency_code=(TextView)itemLayoutView.findViewById(R.id.currencylist_code);
        }
    }

    //Returns the total number of items in the data set hold by the adapter.
    @Override
    public int getItemCount() {
        return list_item.size();
    }

}