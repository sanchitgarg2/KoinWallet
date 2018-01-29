package AdapterClasses;

/**
 * Created by guptapc on 13/01/18.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.security.PublicKey;

import Coinclasses.WalletSection;
import FragmentClasses.TabFragment;
import FragmentClasses.NewInvestment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String title[] = {"New Investment", "Detail", "News"};



    public String WalletsectionJSONString;

    public int userID;


    public  String WalletJSONString;

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {

            case 0: return NewInvestment.newInstance(userID,WalletsectionJSONString,WalletJSONString);
            case 1: return TabFragment.getInstance(position);
            case 2: return TabFragment.getInstance(position);
            default:return TabFragment.getInstance(position);
        }

    }

    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }


    public String getWalletsectionJSONString() {
        return WalletsectionJSONString;
    }

    public void setWalletsectionJSONString(String walletsectionJSONString) {
        WalletsectionJSONString = walletsectionJSONString;
    }


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getWalletJSONString() {
        return WalletJSONString;
    }

    public void setWalletJSONString(String walletJSONString) {
        WalletJSONString = walletJSONString;
    }




}