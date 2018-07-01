package sharetest.com.coinwallet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import static SupportingClasses.Helper.AppuserID;
import static SupportingClasses.Helper.SCREEN_PAGE;

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //setTitle("Fragment Two"); //this will set title of Action Bar


        android.support.v4.app.FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();


        switch(SCREEN_PAGE) {

            case 0:
                WalletList walletList = new WalletList();
                fragmentTransaction1.replace(R.id.content, walletList, "Fragment Two");  //create first framelayout with id fram in the activity where fragments will be displayed
                navigation.setSelectedItemId(R.id.action_wallet);
                break;
            case 1: Watchlist watchlist = new Watchlist();
                fragmentTransaction1.replace(R.id.content, watchlist, "Fragment Two");  //create first framelayout with id fram in the activity where fragments will be displayed
                navigation.setSelectedItemId(R.id.action_watchlist);
                break;
            case 2: CurrencyExchange currencyExchange = new CurrencyExchange();
                fragmentTransaction1.replace(R.id.content, currencyExchange, "Fragment Two");  //create first framelayout with id fram in the activity where fragments will be displayed
                navigation.setSelectedItemId(R.id.action_exchange);
                break;
            case 3:Settings settings = new Settings();
                fragmentTransaction1.replace(R.id.content, settings, "Fragment Two");  //create first framelayout with id fram in the activity where fragments will be displayed
                navigation.setSelectedItemId(R.id.action_settings);
                break;

        }
        fragmentTransaction1.commit();
        scheduleAlarm();



    }




    // Setup a recurring alarm every half hour
    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        //AlarmManager.INTERVAL_HALF_HOUR

        long interval=10000;
        //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, firstMillis
                , interval, pIntent);


    }

}
