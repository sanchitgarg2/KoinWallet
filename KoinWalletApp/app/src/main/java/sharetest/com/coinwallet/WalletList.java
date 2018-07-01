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
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import AdapterClasses.RecyclerViewClickListener;
import AdapterClasses.WalletListAdapter;
import Coinclasses.CoinWallet;
import Coinclasses.User;
import Coinclasses.WalletSection;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.USER;
import static SupportingClasses.Helper.UserURL;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.WALLETSECTION;
import static SupportingClasses.Helper.SCREEN_PAGE;

public class WalletList extends Fragment {

    public WalletList() {
        // Required empty public constructor
    }

    private TextView headerText;
    private RecyclerView listView;
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
            //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            rootContext = v.getContext();
            rootView = v;
            listView = (RecyclerView) v.findViewById(R.id.list_view);
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

                JSONParser parser = new JSONParser();
                org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(user);

                String reasonCode = json.get("statusCode").toString();

                if (reasonCode.equals("200")) {
                    USER = mapper.readValue(json.get("user").toString(), User.class);
                    TextView currentValue = (TextView)rootView.findViewById(R.id.currency_code_section);
                    currentValue.setText("$" + String.valueOf(USER.getLiquidCashInWallet()));
                    setListViewData(USER.getWallet(), USER.getUSERID());
                }
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
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    private void setListViewData(final CoinWallet coinWallet, final int userid) {

        if(coinWallet.getSections()!=null) {

            final HashMap<String, WalletSection> sections = coinWallet.getSections();

            final List<WalletSection> walletSections=new ArrayList<WalletSection>(sections.values());

            listView.setHasFixedSize(true);
            listView.setLayoutManager(new LinearLayoutManager(rootContext));
            listView.addItemDecoration(new DividerItemDecoration(rootContext,LinearLayoutManager.VERTICAL));
            listView.setNestedScrollingEnabled(false);

            RecyclerViewClickListener listener= new RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {

                    //Toast.makeText(rootContext,"currencycliked  "+ walletSections.get(position),Toast.LENGTH_SHORT).show();

                    WalletSection walletSection = walletSections.get(position);
                    WALLETSECTION =walletSection;
                    SCREEN_PAGE=0;
                    Intent intent = new Intent(rootContext, SectionDisplay.class);
                    startActivity(intent);
                }
            };


            WalletListAdapter adapter = new WalletListAdapter(rootContext, walletSections,listener);
            listView.setAdapter(adapter);
        }

    }

}
