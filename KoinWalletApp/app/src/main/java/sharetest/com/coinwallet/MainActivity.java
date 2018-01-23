package sharetest.com.coinwallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import Coinclasses.CoinWallet;
import Coinclasses.Currency;
import Coinclasses.User;
import Coinclasses.WalletSection;
import AdapterClasses.CoinAdapter;
import SupportingClasses.BottomNavigationViewBehavior;

public class MainActivity extends AppCompatActivity {

    public String bitcoinURL ="https://api.cryptonator.com/api/full/btc-usd";
            //"http://192.168.0.109:8080/Koinwallet/KoinWallet/getUser?userID=10";
            //"http://192.168.43.122:8080/Koinwallet/KoinWallet/getUser?userID=10";
    //

    private TextView headerText;
    private ListView listView;
    private View headerView;
    private View headerSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        setBottomView();

        listView = (ListView) findViewById(R.id.list_view);
        headerView = findViewById(R.id.header_image_view);
        headerText = (TextView) findViewById(R.id.header_text);

        setListViewHeader();


        // Handle list View scroll events
        listView.setOnScrollListener(onScrollListener());

         /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */


    }

    /*
    Handler h = new Handler();
    int delay = 10000; //10 seconds
    Runnable runnable;
    @Override
    protected void onResume() {
     //start handler as activity become visible

        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                updateLatestCurrency();
                runnable=this;

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

    @Override
    protected void onPause() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }
    */




     @Override
    protected void onStart() {
        super.onStart();
         ObjectMapper mapper = new ObjectMapper();
         User sanchit= new User();
         String user=null;
         try {
             user =new getJSONValue(this).execute(bitcoinURL).get();
             sanchit = mapper.readValue(user, User.class);
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (ExecutionException e) {
             e.printStackTrace();
         } catch (JsonParseException e) {
             e.printStackTrace();
         } catch (JsonMappingException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }

         Currency.CURRENCYSTATE = new HashMap<String, Currency>();
         for(WalletSection section : sanchit.getWallet().getSections().values())
         {
             Currency c = section.getCurrency();
             Currency.makeNewCurrency(c);
         }

         TextView currentValue = (TextView) findViewById(R.id.CurrentValue);
         currentValue.setText("WALLET :" + String.valueOf(sanchit.getLiquidCashInWallet()));
         setListViewData(sanchit.getWallet());

    }


    private void updateLatestCurrency() {

        new getJSONValue(this).execute(bitcoinURL);

        Log.d("called","I AM BEING CALLED EVERY TEN SECONDS");
    }

    private void setBottomView() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView .getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        bottomNavigationView.setSelectedItemId(R.id.action_wallet);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Context context= MainActivity.this;
                        Intent intent=null;
                        switch (item.getItemId()) {
                            case R.id.action_watchlist:
                                  intent= new Intent(context, Watchlist.class);
                                startActivity(intent);break;

                            case R.id.action_wallet:break;

                            case R.id.action_exchange:
                                intent  = new Intent(context, CurrencyExchange.class);
                                startActivity(intent);break;

                            case R.id.action_settings:
                                intent  = new Intent(context, Settings.class);
                                startActivity(intent);break;

                        }
                        return true;
                    }
                });

    }


    private void setBottomViewAHbottom() {

        AHBottomNavigationItem item1 = new AHBottomNavigationItem("TITLE", R.drawable.home);
        final AHBottomNavigation bottomNavigation= (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item1);
        bottomNavigation.setCurrentItem(0);
        bottomNavigation.setDefaultBackgroundColor(Color.BLACK);
        bottomNavigation.setAccentColor(fetchColor(R.color.colorPrimary));
        bottomNavigation.setInactiveColor(fetchColor(R.color.colorPrimaryDark));
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                String[] colors = new String[]{"#@31231","#23123","#12312","#12311"};
                Toast.makeText(getBaseContext(), String.valueOf(colors[position]), Toast.LENGTH_SHORT).show();
                //fragment.updateColor(Color.parseColor(colors[position]));

                return wasSelected;
            }
        });

    }



    private int fetchColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void setListViewHeader() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View listHeader = inflater.inflate(R.layout.listview_header, null, false);
        headerSpace = listHeader.findViewById(R.id.header_space);

        listView.addHeaderView(listHeader);
    }

    private void setListViewData(CoinWallet coinWallet) {

        if(coinWallet.getSections()!=null) {
            HashMap<String, WalletSection> sections = coinWallet.getSections();

            CoinAdapter adapter = new CoinAdapter(this, new ArrayList<WalletSection>(sections.values()));
            //ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_listview, R.id.item, modelList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    WalletSection walletSection = (WalletSection) parent.getItemAtPosition(position);

                    ObjectMapper mapper = new ObjectMapper();
                    String walletJSONString = null;
                    try {
                        walletJSONString = mapper.writeValueAsString(walletSection);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(MainActivity.this, SectionDisplay.class);
                    intent.putExtra("section", walletJSONString);
                    startActivity(intent);

                }
            });




        }

    }


    private AbsListView.OnScrollListener onScrollListener () {
        return new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // Check if the first item is already reached to top
                if (listView.getFirstVisiblePosition() == 0) {
                    View firstChild = listView.getChildAt(0);
                    int topY = 0;
                    if (firstChild != null) {
                        topY = firstChild.getTop();
                    }

                    int headerTopY = headerSpace.getTop();
                    headerText.setY(Math.max(0, headerTopY + topY));

                    // Set the image to scroll half of the amount that of ListView
                    headerView.setY(topY * 0.5f);
                }
            }
        };

    }


}

