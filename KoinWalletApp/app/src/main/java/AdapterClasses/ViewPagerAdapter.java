package AdapterClasses;

/**
 * Created by guptapc on 13/01/18.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.security.PublicKey;

import Coinclasses.WalletSection;
import FragmentClasses.AlarmActivity;
import FragmentClasses.CandleStickChartActivity;
import FragmentClasses.TabFragment;
import FragmentClasses.NewInvestment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String title[] = {"New Investment", "Detail", "Alarm"};





    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {

            case 0: return NewInvestment.newInstance();
            case 1: return CandleStickChartActivity.newInstance();
            case 2: return AlarmActivity.newInstance();
            default:return NewInvestment.newInstance();
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






}