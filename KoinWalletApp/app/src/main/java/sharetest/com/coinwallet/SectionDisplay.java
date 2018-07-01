package sharetest.com.coinwallet;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import AdapterClasses.*;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import static SupportingClasses.Helper.NET_COST_VALUE;
import static SupportingClasses.Helper.PORTFOLIO_VALUE;
import static SupportingClasses.Helper.WALLETSECTION;

/**
 * Created by guptapc on 13/01/18.
 */

public class SectionDisplay extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView currency_icon;
    private TextView currency_name;

    private TextView portfolio_value_text;
    private TextView market_value_text;
    private TextView net_cost_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.section_display);




        //Toast.makeText(getBaseContext(), String.valueOf(WALLETSECTION.getCashInvested()), Toast.LENGTH_SHORT).show();



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(WALLETSECTION.getCurrency().getName());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        currency_icon=(ImageView)findViewById(R.id.currency_icon_section);
        currency_name=(TextView)findViewById(R.id.currency_code_section);
        portfolio_value_text=(TextView)findViewById(R.id.portfolio_value);
        market_value_text=(TextView) findViewById(R.id.market_value_body);
        net_cost_text=(TextView) findViewById(R.id.net_value_body);


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        /*adapter.setWalletsectionJSONString(walletsectionJSONString);
        adapter.setUserID(userID);
        adapter.setWalletJSONString(walletJSONString);
        */
        viewPager.setAdapter(adapter);
        currency_name.setText(WALLETSECTION.getCurrency().getCurrencyCode());
        String currencyName= WALLETSECTION.getCurrency().getCurrencyCode().toLowerCase();
        Integer x=getResources().getIdentifier(currencyName, "drawable", getPackageName());
        currency_icon.setImageResource(x);
        portfolio_value_text.setText(Float.toString(PORTFOLIO_VALUE));
        net_cost_text.setText(Float.toString(NET_COST_VALUE));
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

}
