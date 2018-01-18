package AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import Coinclasses.WalletSection;
import sharetest.com.coinwallet.R;

/**
 * Created by guptapc on 10/01/18.
 */

public class CoinAdapter extends BaseAdapter {




    List<WalletSection> sections = null;
    private static LayoutInflater inflater=null;
    Context mContext;

    public CoinAdapter(Context context, List<WalletSection> walletsections) {

        
        mContext=context;
        sections=walletsections;
    }

    public List<WalletSection> getSections() {
        return sections;
    }
    public void setSections(List<WalletSection> sections) {
        this.sections = sections;
    }

    public int getCount() {
        return sections.size();
    }

    public Object getItem(int position) {
        return sections.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi=convertView;


        if(convertView==null){

            LayoutInflater inflater = LayoutInflater.from(mContext);
            vi = inflater.inflate(R.layout.item_listview,parent,false);
        }


        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
        TextView cashinvested = (TextView)vi.findViewById(R.id.CashInvested); // artist name
        TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image

        WalletSection section = sections.get(position);
        title.setText(section.getCurrency().getCurrencyCode());
        cashinvested.setText(String.valueOf(section.getCashInvested()));
        try {

            artist.setText(section.getCashRedeemed() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        duration.setText(section.getCurrentBalance() + "" );

        return vi;
    }


    public interface OnItemClickListener {
    }
}
