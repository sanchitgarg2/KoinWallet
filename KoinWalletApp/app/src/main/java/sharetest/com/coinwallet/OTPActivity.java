package sharetest.com.coinwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.provider.Settings.Secure;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import Coinclasses.Currency;
import Coinclasses.Currency.*;
import Coinclasses.User;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.CURRENCYLIST;
import static SupportingClasses.Helper.LOGINURL;
import static SupportingClasses.Helper.REGISTERURL;
import static SupportingClasses.Helper.USER;

/**
 * Created by guptapc on 31/01/18.
 */

public class OTPActivity  extends AppCompatActivity implements View.OnClickListener {

    private EditText OTP;
    private TextView error;
    private Button logintoApp;
    private Button login;

    private String Email;
    private String Mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullscreenView();

        setContentView(R.layout.activity_login_otp);


        Email = getIntent().getStringExtra("email");
        Mobile= getIntent().getStringExtra("phoneNumber");

        OTP = (EditText) findViewById(R.id.otp);
        logintoApp = (Button) findViewById(R.id.btnLoginOTP);
        login=(Button) findViewById(R.id.btnLoginAgainOTP);
        error = (TextView) findViewById(R.id.error_OTP);

        logintoApp.setOnClickListener(this);
        login.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoginOTP: {
                String Otp = OTP.getText().toString();
                String device_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

                if (validate(Otp)) {

                        JSONObject obj = new JSONObject();

                        if(Email!=null&&!Email.equals(""))
                        obj.put("email", Email);
                        if(Mobile!=null&&!Mobile.equals(""))
                        obj.put("phoneNumber",Mobile);

                        obj.put("otp",Otp);
                        obj.put("deviceID",device_id);

                    Intent intent = new Intent(this, MainActivity.class);
                    Intent intentlogin = new Intent(this, LoginActivity.class);

                        String response = null;
                        try {
                            response = new postJSONValue(OTPActivity.this).execute(AppURL+LOGINURL,obj.toJSONString()).get();

                            if(response!=null) {

                                JSONParser parser = new JSONParser();
                                JSONObject json = (JSONObject) parser.parse(response);

                                String reasonCode=json.get("statusCode").toString();
                                if (reasonCode.equals("200")) {

                                    if(json.get("user")!=null) {
                                        USER = getUser(json.get("user").toString());
                                        AppuserID=USER.getUSERID();

                                    }
                                    if(json.get("currencyList")!=null) {
                                        CURRENCYLIST = getCurrency(json.get("currencyList").toString());
                                    }

                                    startActivity(intent);
                                }
                                if (reasonCode.equals("400")) {

                                    openDialog("Wrong OTP \n Redirecting to Login....");
                                    startActivity(intentlogin);
                                }
                                if (reasonCode.equals("401")) {

                                    openDialog("Wrong OTP  \n Redirecting to Login....");
                                    startActivity(intentlogin);
                                }
                                if (reasonCode.equals("402")) {

                                    openDialog("SMS Sent Failed \n Redirecting to Login....");
                                    startActivity(intentlogin);
                                }
                                if (reasonCode.equals("403")) {
                                    openDialog("Invalid Request Data \n Redirecting to Login....");
                                    startActivity(intentlogin);
                                }
                            }


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } else {
                    Animation shake = AnimationUtils.loadAnimation(OTPActivity.this, R.anim.shake);
                    OTP.startAnimation(shake);
                    error.setText("!OTP MUST BE 4 CHARACTERS");

                }
                break;
            }
            case R.id.btnLoginAgainOTP:{
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            }


        }
    }

    public boolean validate(final String hex){
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", hex)) {
            if(hex.length() != 4 ) {
                // if(phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check=false;
        }
        return check;
    }
    private void fullscreenView() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

    }
    public void openDialog(String message){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

        dlgAlert.setMessage(message);
        dlgAlert.setTitle("Validation Failed");

        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();

    }
    public User getUser(String jsonuser) throws IOException {

        User user=new User();
        if(jsonuser!=null) {
            ObjectMapper mapper = new ObjectMapper();
            user = mapper.readValue(jsonuser, User.class);

        }
        return user;

    }
    public HashMap<String,CurrencySnapShot> getCurrency(String jsoncurrency) throws JSONException, IOException {

        HashMap<String, CurrencySnapShot> currencylistmap = new HashMap<String, CurrencySnapShot>();
        if(jsoncurrency!=null) {
            org.json.JSONObject jObject = new org.json.JSONObject(jsoncurrency);
            Iterator<?> keys = jObject.keys();

            ObjectMapper mapper = new ObjectMapper();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = jObject.getString(key);
                org.json.JSONObject newJObject = new org.json.JSONObject(value);
               // Currency.CurrencySnapShot snapshot =mapper.readValue(value, CurrencySnapShot.class);
                Currency.CurrencySnapShot snapshot = new Currency.CurrencySnapShot(Float.parseFloat(newJObject.get("valueInINR").toString()),Float.parseFloat(newJObject.get("valueInUSD").toString())
                        , newJObject.get("refreshTime").toString()
                        ,newJObject.get("currencyCode").toString(),newJObject.get("currencyName").toString());


                currencylistmap.put(key, snapshot);

            }
        }
        return  currencylistmap;
    }
}
