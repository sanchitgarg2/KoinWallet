package sharetest.com.coinwallet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import sharetest.com.coinwallet.R;
import sharetest.com.coinwallet.Watchlist;

import static SupportingClasses.Helper.AppuserID;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.action_wallet:
                    WalletList fragment1 = new WalletList();
                    android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.content, fragment1, "Walletlist");  //create first framelayout with id fram in the activity where fragments will be displayed
                    fragmentTransaction1.commit();
                    return true;

                case R.id.action_watchlist:
                    Watchlist fragment2 = new Watchlist();
                    android.support.v4.app.FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.content, fragment2, "Watchlist");  //create first framelayout with id fram in the activity where fragments will be displayed
                    fragmentTransaction2.commit();
                    return true;
                case R.id.action_exchange:
                    CurrencyExchange fragment3 = new CurrencyExchange();
                    android.support.v4.app.FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.content, fragment3, "FragmentThree");  //create first framelayout with id fram in the activity where fragments will be displayed
                    fragmentTransaction3.commit();
                    return true;
                case R.id.action_settings:
                    Settings fragment4 = new Settings();
                    android.support.v4.app.FragmentTransaction fragmentTransaction4 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction4.replace(R.id.content, fragment4, "Fragment One");  //create first framelayout with id fram in the activity where fragments will be displayed
                    fragmentTransaction4.commit();
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.action_watchlist);
        //setTitle("Fragment Two"); //this will set title of Action Bar
        Watchlist fragment1 = new Watchlist();
        android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
        fragmentTransaction1.replace(R.id.content, fragment1, "Fragment Two");  //create first framelayout with id fram in the activity where fragments will be displayed
        fragmentTransaction1.commit();
    }

}
