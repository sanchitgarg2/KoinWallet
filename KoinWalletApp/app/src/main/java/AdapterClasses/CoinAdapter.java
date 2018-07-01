package AdapterClasses;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import java.util.ArrayList;
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

        CandleStickChart mChart;
        mChart = (CandleStickChart) vi.findViewById(R.id.chart1);
        setchartValue(mChart);


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

    public void setchartValue(CandleStickChart mChart){

        //mChart.setBackgroundColor(Color.WHITE);

        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(0);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setAxisLineWidth(1.2f);
        xAxis.setAxisLineColor(Color.BLACK);
        //xAxis.setGranularity(1f);




        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setLabelCount(7, false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(true);
        leftAxis.setAxisLineWidth(1.2f);
        leftAxis.setAxisLineColor(Color.BLACK);
        // leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        mChart.getAxisRight().setEnabled(false); // no right axis

        // YAxis rightAxis = mChart.getAxisRight();
        //rightAxis.setEnabled(true);
//        rightAxis.setStartAtZero(false);


        ArrayList<CandleEntry> yVals1 = new ArrayList<CandleEntry>();

        for (int i = 0; i < 20; i++) {
            float mult = (0 + 1);
            float val = (float) (Math.random() * 40) + mult;

            float high = (float) (Math.random() * 9) + 8f;
            float low = (float) (Math.random() * 9) + 8f;

            float open = (float) (Math.random() * 6) + 1f;
            float close = (float) (Math.random() * 6) + 1f;
            boolean even = i % 2 == 0;

            yVals1.add(new CandleEntry(
                    i, val + high,
                    val - low,
                    even ? val + open : val - open,
                    even ? val - close : val + close,
                    R.drawable.add
            ));

        }

        CandleDataSet set1 = new CandleDataSet(yVals1, "Data Set");

        set1.setDrawIcons(false);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setShadowColor(Color.DKGRAY);
        set1.setShadowWidth(0.9f);
        // set1.setColor(Color.rgb(80, 80, 80));
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.GREEN);
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setNeutralColor(Color.BLUE);
        //set1.setHighlightLineWidth(1.2f);

        CandleData data = new CandleData(set1);

        mChart.setData(data);
        mChart.invalidate();


    }

    public interface OnItemClickListener {
    }
}
