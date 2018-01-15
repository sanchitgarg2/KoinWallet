package AdapterClasses;

/**
 * Created by guptapc on 13/01/18.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import FragmentClasses.TabFragment;
import FragmentClasses.NewInvestment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private String title[] = {"New Investment", "Detail", "News"};

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {

        switch(position) {

            case 0: return NewInvestment.newInstance("FirstFragment, Instance 1");
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
}