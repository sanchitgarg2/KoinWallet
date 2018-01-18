package sharetest.com.coinwallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Created by guptapc on 15/01/18.
 */

public class Settings extends  AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setBottomView();
    }

    private void setBottomView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.action_settings);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Context context= Settings.this;
                        Intent intent=null;
                        switch (item.getItemId()) {
                            case R.id.action_watchlist:
                                intent= new Intent(context, Watchlist.class);
                                startActivity(intent);break;

                            case R.id.action_wallet:
                                intent= new Intent(context, MainActivity.class);
                                startActivity(intent);break;

                            case R.id.action_exchange:
                                intent  = new Intent(context, CurrencyExchange.class);
                                startActivity(intent);break;

                            case R.id.action_settings:break;


                        }
                        return true;
                    }
                });

    }
}
