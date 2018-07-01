package FragmentClasses;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import AdapterClasses.TransactionAdapter;
import Coinclasses.CoinWallet;
import Coinclasses.Currency;
import Coinclasses.Transaction;
import Coinclasses.User;
import Coinclasses.WalletSection;
import sharetest.com.coinwallet.AddTransaction;
import sharetest.com.coinwallet.R;
import sharetest.com.coinwallet.getJSONValue;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.NET_COST_VALUE;
import static SupportingClasses.Helper.PORTFOLIO_VALUE;
import static SupportingClasses.Helper.USER;
import static SupportingClasses.Helper.UserURL;
import static SupportingClasses.Helper.WALLETSECTION;

/**
 * Created by guptapc on 14/01/18.
 */

public class NewInvestment extends Fragment implements View.OnClickListener{


    private Button mClickButton1;
    private ListView listView;
    public Context rootContext;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.transaction_detail, container, false);
        mClickButton1 = (Button)v.findViewById(R.id.addTransaction);
        mClickButton1.setOnClickListener(this);
        listView = (ListView) v.findViewById(R.id.transaction_list_view);
        rootContext=v.getContext();



         getUser();

         setListViewData();

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addTransaction: {
                Intent intent = new Intent(getContext(), AddTransaction.class);
                startActivity(intent);

            }
        }
    }

    private void setListViewData() {


         float portfolio_value=0f;
         float net_cost_value=0f;
         float market_value=0f;

        List<Transaction> transactions = USER.getWallet().getTransactionList();

        List <Transaction> currencyspecifictransactions= new ArrayList<Transaction>();

        if(transactions!=null) {
            for (Transaction transaction : transactions) {
                // BUY AND SELL TRANSACTIONS OF THAT PARTICULAR CURRENCY
                if (transaction.getIncomingCurrency().getCurrencyCode().equals(WALLETSECTION.getCurrency().getCurrencyCode())
                        || transaction.getOutgoingCurrency().getCurrencyCode().equals(WALLETSECTION.getCurrency().getCurrencyCode())) {
                    currencyspecifictransactions.add(transaction);

                    if(transaction.getPurchaseQuantity()>=0) {
                        net_cost_value += Float.valueOf(transaction.getRate()) * Float.valueOf(transaction.getPurchaseQuantity());
                    }
                    else {
                        net_cost_value += (Float.valueOf(transaction.getRate()) * Float.valueOf(transaction.getPurchaseQuantity())*-1);
                    }
                    portfolio_value+=transaction.getPurchaseQuantity();

                }
            }
        }


        NET_COST_VALUE=net_cost_value;
        PORTFOLIO_VALUE=portfolio_value;

        if(currencyspecifictransactions!=null) {

            TransactionAdapter adapter = new TransactionAdapter(getContext(), currencyspecifictransactions);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                }
            });
        }


    }


    public static NewInvestment newInstance() {

        NewInvestment f = new NewInvestment();
        Bundle b = new Bundle();
        f.setArguments(b);

        return f;
    }
    public void getUser(){

        ObjectMapper mapper = new ObjectMapper();
        //User sanchit= new User();
        String user=null;
        try {
            user =new getJSONValue(rootContext).execute(AppURL+UserURL+Integer.toString(AppuserID)).get();

            if(user!=null) {

                JSONParser parser = new JSONParser();
                org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(user);

                String reasonCode = json.get("statusCode").toString();

                if (reasonCode.equals("200")) {
                    USER = mapper.readValue(json.get("user").toString(), User.class);
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

}