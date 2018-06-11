package sharetest.com.coinwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Currency;
import java.util.concurrent.ExecutionException;

import Coinclasses.WalletSection;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.WALLETSECTION;
import static SupportingClasses.Helper.addURL;
import static SupportingClasses.Helper.tradeURL;

/**
 * Created by guptapc on 26/01/18.
 */

public class AddTransaction extends AppCompatActivity implements View.OnClickListener{


    private static WalletSection section=null;
    private static Currency currency;
    private RadioGroup radioGroup;
    private RadioButton buy;
    private EditText price;
    private  EditText volume;
    private EditText total;
    private RadioButton checkbutton;
    private Button mClickButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newinvestment);


        //TextView tv = (TextView) v.findViewById(R.id.tvFragThird);
        //tv.setText(getArguments().getString("msg"));

        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        buy = (RadioButton)findViewById(R.id.radioButton1) ;
        checkbutton=buy;
        radioGroup.clearCheck();
        radioGroup.check(buy.getId());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                   checkbutton = (RadioButton) group.findViewById(checkedId);
                if (null != checkbutton && checkedId > -1) {
                    Toast.makeText(AddTransaction.this, checkbutton.getText() + Integer.toString(checkedId), Toast.LENGTH_SHORT).show();
                }

            }

        });



        mClickButton1 = (Button)findViewById(R.id.addbutton);
        price=(EditText)findViewById(R.id.pricevalue);
        volume=(EditText)findViewById(R.id.volumevalue);
        total=(EditText)findViewById(R.id.Totalvolume);


        mClickButton1.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.addbutton: {
                // do something for button 1 click
                String pricevalue=price.getText().toString();
                String volumevalue=volume.getText().toString();
                String totalvolume=total.getText().toString();
                String response=null;

                if((!pricevalue.matches("")&&!volumevalue.matches(""))||
                        (!pricevalue.matches("")&&!totalvolume.matches(""))||
                        (!totalvolume.matches("")&&!volumevalue.matches(""))){

                    if(checkbutton.getText().equals("SELL")){
                        float quantity=Float.parseFloat(volumevalue);
                        quantity=quantity*-1;
                         volumevalue=String.valueOf(quantity);
                    }

                    try {
                        JSONObject obj = new JSONObject();
                        obj.put("userID", Integer.toString(AppuserID));
                        obj.put("currencyCode",WALLETSECTION.getCurrency().getCurrencyCode());
                        obj.put( "price", pricevalue);
                        obj.put("quantity",volumevalue);

                        response =new postJSONValue(AddTransaction.this).execute(AppURL+tradeURL,obj.toJSONString()).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    JSONParser parser = new JSONParser();
                    JSONObject json = null;
                    try {
                        json = (JSONObject) parser.parse(response);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String reasonCode=json.get("statusCode").toString();
                    if(reasonCode.equals("200")) {
                        Toast.makeText(AddTransaction.this, "Great going" + WALLETSECTION.getCurrency().getCurrencyCode() + "  " + AppuserID + "    " + response, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, SectionDisplay.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(AddTransaction.this, "OOPS! SOMETHING WENT MISSING :(", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(AddTransaction.this, "Please fill atleast 2 VALUES", Toast.LENGTH_SHORT).show();
                }
                break;
            }


            //.... etc
        }
    }
}
