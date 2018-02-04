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


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.REGISTERURL;
import static SupportingClasses.Helper.updatewatchlistURL;
import static java.security.AccessController.getContext;

/**
 * Created by guptapc on 28/01/18.
 */

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {


    private Pattern pattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private EditText email;
    private TextView error;
    private Button login;
    private Button register;
    private EditText mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        fullscreenView();

        pattern = Pattern.compile(EMAIL_PATTERN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email= (EditText)findViewById(R.id.EmailRegister);
        mobile= (EditText)findViewById(R.id.MobileRegister);
        register=(Button)findViewById(R.id.btnRegistration);
        login=(Button)findViewById(R.id.btnLinkToLoginScreen);
        error=(TextView)findViewById(R.id.error2);

        login.setOnClickListener(this);
        register.setOnClickListener(this);


    }

    private void fullscreenView() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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
            case R.id.btnRegistration: {
                String Email = email.getText().toString();
                String Mobile= mobile.getText().toString();
                String device_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

                if (validateEmail(Email)&&validateMobile(Mobile)) {

                    JSONObject obj = new JSONObject();
                    obj.put("email", Email);
                    obj.put("phoneNumber",Mobile);
                    obj.put("countryCode","91");
                    obj.put("deviceID",device_id);

                    Intent intent = new Intent(this, OTPActivity.class);
                    intent.putExtra("email", Email);
                    intent.putExtra("phoneNumber", Mobile);



                    String response = null;
                    try {
                        response = new postJSONValue(RegistrationActivity.this).execute(AppURL+REGISTERURL,obj.toJSONString()).get();

                        if(response!=null) {

                            JSONParser parser = new JSONParser();
                            JSONObject json = (JSONObject) parser.parse(response);

                            String reasonCode=json.get("statusCode").toString();

                            if (reasonCode.equals("200")) {

                                startActivity(intent);
                            }
                            if (reasonCode.equals("400")) {
                                openDialog("USER ALREADY EXISTS! \n Redirecting to LOGIN Page....");
                                Intent intentlogin = new Intent(this, LoginActivity.class);
                                startActivity(intentlogin);
                            }
                            if (reasonCode.equals("401")) {
                                startActivity(intent);
                            }
                        }


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                } else {
                    if(!validateEmail(Email)) {
                        Animation shake = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.shake);
                        email.startAnimation(shake);
                        error.setText("!PLEASE TYPE VALID EMAIL ");
                    }
                    else{
                        Animation shake = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.shake);
                        mobile.startAnimation(shake);
                        error.setText("!PLEASE TYPE VALID MOBILE ");

                    }

                }
                break;
            }

            case R.id.btnLinkToLoginScreen: {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            }

        }
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
