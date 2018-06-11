package sharetest.com.coinwallet;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sharetest.com.coinwallet.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrencyExchange extends Fragment {


    public CurrencyExchange() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.currencyexchange, container, false);
    }

}
