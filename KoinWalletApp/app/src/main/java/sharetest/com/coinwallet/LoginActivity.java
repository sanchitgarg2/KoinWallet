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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import android.provider.Settings.Secure;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
 * Created by guptapc on 28/01/18.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private EditText email;
    private TextView error;
    private Button login;
    private Button register;
    private Button loginmobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

         fullscreenView();

        pattern = Pattern.compile(EMAIL_PATTERN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email= (EditText)findViewById(R.id.email);
        login=(Button)findViewById(R.id.btnLogin);
        register=(Button)findViewById(R.id.btnRegister);
        error=(TextView)findViewById(R.id.error);

          login.setOnClickListener(this);
          register.setOnClickListener(this);


    }




    public boolean validateEmail(final String hex) {

        matcher = pattern.matcher(hex);
        return matcher.matches();

    }
    public boolean validateMobile(final String hex){
        boolean check=false;
        if(!Pattern.matches("[a-zA-Z]+", hex)) {
            if(hex.length() < 6 || hex.length() > 13) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin: {
                String Data = email.getText().toString();
                String device_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

                if (validateEmail(Data)||validateMobile(Data)) {

                    JSONObject obj = new JSONObject();
                    Intent intent = new Intent(this, OTPActivity.class);
                    Intent intentlogin = new Intent(this, LoginActivity.class);

                    if(validateMobile(Data)) {
                        obj.put("phoneNumber",Data);
                        intent.putExtra("phoneNumber", Data);
                        intent.putExtra("email", "");

                    }
                    else {
                        intent.putExtra("phoneNumber", "");
                        intent.putExtra("email", Data);
                        obj.put("email", Data);

                    }
                    obj.put("deviceID",device_id);

                    String response = null;
                    try {
                        response = new postJSONValue(LoginActivity.this).execute(AppURL+LOGINURL,obj.toJSONString()).get();

                        if(response!=null) {

                            JSONParser parser = new JSONParser();
                            JSONObject json = (JSONObject) parser.parse(response);

                            String reasonCode=json.get("statusCode").toString();

                            if (reasonCode.equals("200")) {

                                startActivity(intent);
                            }
                            if (reasonCode.equals("400")) {

                                openDialog("User Doesn't Exist \n Redirecting to Login....");

                            }
                            if (reasonCode.equals("401")) {

                                openDialog("OTP Failed \n Redirecting to Login....");

                            }
                            if (reasonCode.equals("402")) {

                                openDialog("SMS Sent Failed \n Redirecting to Login....");

                            }
                            if (reasonCode.equals("403")) {
                                openDialog("Invalid Request Data \n Redirecting to Login....");

                            }
                        }


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
                    email.startAnimation(shake);
                    error.setText("!PLEASE TYPE VALID EMAIL OR MOBILE");

                }
                break;
            }

            case R.id.btnRegister: {
                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                break;
            }

        }
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

}
