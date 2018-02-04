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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import AdapterClasses.CoinAdapter;
import Coinclasses.CoinWallet;
import Coinclasses.Currency;
import Coinclasses.User;
import Coinclasses.WalletSection;
import sharetest.com.coinwallet.R;
import sharetest.com.coinwallet.SectionDisplay;
import sharetest.com.coinwallet.getJSONValue;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.USER;
import static SupportingClasses.Helper.UserURL;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.WALLETSECTION;

public class WalletList extends Fragment {

    public WalletList() {
        // Required empty public constructor
    }

    private TextView headerText;
    private ListView listView;
    private View headerView;
    private View headerSpace;
    private Context rootContext;
    private View rootView;
    private View walletEmptyView;
    private Context walletEmptyContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            View v;

            v = inflater.inflate(R.layout.fragment_wallet_list, container, false);
            rootContext = v.getContext();
            rootView = v;
            listView = (ListView) v.findViewById(R.id.list_view);
            getUser(rootContext);

        FloatingActionButton myFab = (FloatingActionButton)v.findViewById(R.id.addCurrency4);

        if(USER.getWallet().getTransactionList()==null) {
            v = inflater.inflate(R.layout.first_time_walletlist, container, false);
            walletEmptyView = v;
            walletEmptyContext = v.getContext();
            myFab = (FloatingActionButton)v.findViewById(R.id.addCurrency2);
        }
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(rootContext, AddCurrencyWalletlist.class);
                startActivity(intent);
            }
        });

        return v;
    }

    public void getUser(Context context){

        ObjectMapper mapper = new ObjectMapper();
        //User sanchit= new User();
        String user=null;
        try {
            user =new getJSONValue(context).execute(AppURL+UserURL+Integer.toString(AppuserID)).get();
            if(user!=null) {
                USER = mapper.readValue(user, User.class);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(user!=null) {

            TextView currentValue = (TextView)rootView.findViewById(R.id.CurrentValue);
            currentValue.setText("WALLET :" + String.valueOf(USER.getLiquidCashInWallet()));
            setListViewData(USER.getWallet(), USER.getUSERID());
        }
    }
    /*
    private void setListViewHeader() {
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View listHeader = inflater.inflate(R.layout.listview_header, null, false);
        headerSpace = listHeader.findViewById(R.id.header_space);

        listView.addHeaderView(listHeader);
    }
    */

    private void setListViewData(final CoinWallet coinWallet, final int userid) {

        if(coinWallet.getSections()!=null) {
            HashMap<String, WalletSection> sections = coinWallet.getSections();

            CoinAdapter adapter = new CoinAdapter(rootContext, new ArrayList<WalletSection>(sections.values()));
            //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_listview, R.id.item, modelList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WalletSection walletSection = (WalletSection) parent.getItemAtPosition(position);

                    ObjectMapper mapper = new ObjectMapper();
                    String walletsectionJSONString=null;
                    String walletJSONString = null;
                    try {
                        walletsectionJSONString = mapper.writeValueAsString(walletSection);
                        walletJSONString=mapper.writeValueAsString(coinWallet);

                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    WALLETSECTION =walletSection;

                    Intent intent = new Intent(rootContext, SectionDisplay.class);
                    startActivity(intent);

                }
            });




        }

    }



    private AbsListView.OnScrollListener onScrollListener () {
        return new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // Check if the first item is already reached to top
                if (listView.getFirstVisiblePosition() == 0) {
                    View firstChild = listView.getChildAt(0);
                    int topY = 0;
                    if (firstChild != null) {
                        topY = firstChild.getTop();
                    }

                    int headerTopY = headerSpace.getTop();
                    headerText.setY(Math.max(0, headerTopY + topY));

                    // Set the image to scroll half of the amount that of ListView
                    headerView.setY(topY * 0.5f);
                }
            }
        };

    }
}
