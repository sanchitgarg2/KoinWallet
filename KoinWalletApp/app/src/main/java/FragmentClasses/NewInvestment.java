package FragmentClasses;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import AdapterClasses.TransactionAdapter;
import Coinclasses.CoinWallet;
import Coinclasses.Currency;
import Coinclasses.Transaction;
import Coinclasses.WalletSection;
import sharetest.com.coinwallet.AddTransaction;
import sharetest.com.coinwallet.R;

/**
 * Created by guptapc on 14/01/18.
 */

public class NewInvestment extends Fragment implements View.OnClickListener{

    private static String userwalletsection;
    private static int userID;
    private static String userwallet;
    private Button mClickButton1;
    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.transaction_detail, container, false);
        mClickButton1 = (Button)v.findViewById(R.id.addTransaction);
        mClickButton1.setOnClickListener(this);
        listView = (ListView) v.findViewById(R.id.transaction_list_view);

        ObjectMapper mapper = new ObjectMapper();

        CoinWallet wallet= null;
        WalletSection section=null;
        try {
            wallet = mapper.readValue(userwallet, CoinWallet.class);
            section=mapper.readValue(userwalletsection,WalletSection.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("CLASS NAME", getContext().toString());
        if(section.getCurrency()!=null) {
            setListViewData(wallet, userID, section.getCurrency());
        }
        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addTransaction: {
                Intent intent = new Intent(getContext(), AddTransaction.class);
                intent.putExtra("section", userwalletsection);
                intent.putExtra("userID", userID);
                startActivity(intent);

            }
        }
    }

    private void setListViewData(CoinWallet coinWallet, int userid, Currency currency) {



        List<Transaction> transactions = coinWallet.getTransactionList();

        List <Transaction> currencyspecifictransactions= new ArrayList<Transaction>();

        if(transactions!=null) {
            for (Transaction transaction : transactions) {
                // BUY AND SELL TRANSACTIONS OF THAT PARTICULAR CURRENCY
                if (transaction.getIncomingCurrency().getCurrencyCode().equals(currency.getCurrencyCode())
                        || transaction.getOutgoingCurrency().getCurrencyCode().equals(currency.getCurrencyCode())) {
                    currencyspecifictransactions.add(transaction);
                }
            }
        }

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


    public static NewInvestment newInstance(int uID, String section, String wallet) {

        NewInvestment f = new NewInvestment();
        Bundle b = new Bundle();
        //b.putString("msg", text);
        userwalletsection=section;
        userwallet=wallet;
        userID=uID;
        f.setArguments(b);

        return f;
    }
}