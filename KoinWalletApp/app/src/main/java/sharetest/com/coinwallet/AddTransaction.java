package sharetest.com.coinwallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import Coinclasses.Currency;
import Coinclasses.CurrencyCode;
import Coinclasses.WalletSection;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.CURRENCYLIST;
import static SupportingClasses.Helper.WALLETSECTION;
import static SupportingClasses.Helper.addURL;
import static SupportingClasses.Helper.tradeURL;

/**
 * Created by guptapc on 26/01/18.
 */

public class AddTransaction extends AppCompatActivity implements View.OnClickListener,SearchView.OnQueryTextListener{


    private static WalletSection section=null;
    private static Currency currency;
    private RadioGroup radioGroup;
    private RadioButton buy;
    private RadioButton sell;
    private EditText price;
    private  EditText volume;
    private EditText total;
    private RadioButton checkbutton;
    private Button mClickButton1;
    String pricevalue;
    String volumevalue;
    String totalvolume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newinvestment);



        //TextView tv = (TextView) v.findViewById(R.id.tvFragThird);
        //tv.setText(getArguments().getString("msg"));

        getSupportActionBar().setTitle(WALLETSECTION.getCurrency().getName()+"-"+WALLETSECTION.getCurrency().getCurrencyCode());

        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        buy = (RadioButton)findViewById(R.id.radioButton1) ;
        sell = (RadioButton)findViewById(R.id.radioButton2) ;

        checkbutton=buy;
        radioGroup.clearCheck();
        radioGroup.check(buy.getId());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                   checkbutton = (RadioButton) group.findViewById(checkedId);
                if (null != checkbutton && checkedId > -1) {
                    if (checkbutton.getText().equals("SELL")) {
                        sell.setBackgroundColor(getResources().getColor(R.color.accent_color));
                        buy.setBackgroundColor(0x00000000);
                        //Toast.makeText(AddTransaction.this, checkbutton.getText() + Integer.toString(checkedId), Toast.LENGTH_SHORT).show();
                    } else {
                        buy.setBackgroundColor(getResources().getColor(R.color.primary_color));
                        sell.setBackgroundColor(0x00000000);
                    }
                }


            }

        });



        mClickButton1 = (Button)findViewById(R.id.addbutton);
        price=(EditText)findViewById(R.id.pricevalue);
        volume=(EditText)findViewById(R.id.volumevalue);
        total=(EditText)findViewById(R.id.Totalvolume);
        price.setText(Float.toString(CURRENCYLIST.get(WALLETSECTION.getCurrency().getCurrencyCode()).getvalueInINR()));
        mClickButton1.setOnClickListener(this);


        addTextListener();
    }

    private List<CurrencyCode> list = new ArrayList<CurrencyCode>();
    public void getCurrencyList() throws JSONException {



        Set entrySet = CURRENCYLIST.entrySet();
        Iterator it = entrySet.iterator();

        while(it.hasNext()){
            Map.Entry me = (Map.Entry)it.next();
            CurrencyCode currencyCode=new CurrencyCode();

            Currency.CurrencySnapShot snapShot=(Currency.CurrencySnapShot)(me).getValue();

            currencyCode.setCurrencyCode(me.getKey().toString());
            currencyCode.setCurrencyName(snapShot.getCurrencyName());
            list.add(currencyCode);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.addbutton: {
                // do something for button 1 click
                pricevalue=price.getText().toString();
                volumevalue=volume.getText().toString();
                totalvolume=total.getText().toString();
                String response=null;

                if((!pricevalue.matches("")&&!volumevalue.matches(""))||
                        (!pricevalue.matches("")&&!totalvolume.matches(""))||
                        (!totalvolume.matches("")&&!volumevalue.matches(""))) {

                    if (checkbutton.getText().equals("SELL")) {
                        float quantity = Float.parseFloat(volumevalue);
                        quantity = quantity * -1;
                        volumevalue = String.valueOf(quantity);
                    }

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("userID", Integer.toString(AppuserID));
                        obj.put("currencyCode", WALLETSECTION.getCurrency().getCurrencyCode());
                        obj.put("price", pricevalue);
                        obj.put("quantity", volumevalue);

                        response = new postJSONValue(AddTransaction.this).execute(AppURL + tradeURL, obj.toJSONString()).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    if(response!=null) {

                        JSONParser parser = new JSONParser();
                        JSONObject json = null;
                        try {
                            json = (JSONObject) parser.parse(response);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String reasonCode = json.get("statusCode").toString();
                        if (reasonCode.equals("200")) {
                            Toast.makeText(AddTransaction.this, "Great going" + WALLETSECTION.getCurrency().getCurrencyCode() + "  " + AppuserID + "    " + response, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, SectionDisplay.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(AddTransaction.this, "OOPS! SOMETHING WENT MISSING :(", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(AddTransaction.this, "OOPS! SOMETHING WENT MISSING :(", Toast.LENGTH_SHORT).show();
                    }

                    } else {
                        Toast.makeText(AddTransaction.this, "Please fill atleast 2 VALUES", Toast.LENGTH_SHORT).show();
                    }

                break;
            }



            //.... etc
        }
    }
    public void addTextListener(){

        price.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {
                pricevalue=price.getText().toString();
                volumevalue=volume.getText().toString();
                totalvolume=total.getText().toString();

                if(!volumevalue.matches("")&&!pricevalue.matches("")){
                    total.setText(Float.toString(Float.valueOf(pricevalue)*Float.valueOf(volumevalue)));
                }
            }

        });
        volume.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {
                pricevalue=price.getText().toString();
                volumevalue=volume.getText().toString();
                totalvolume=total.getText().toString();

                if(!pricevalue.matches("")&&!volumevalue.matches("")){
                    total.setText(Float.toString(Float.valueOf(pricevalue)*Float.valueOf(volumevalue)));
                }
            }

        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
