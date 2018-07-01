package AdapterClasses;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import java.util.ArrayList;
import java.util.List;

import Coinclasses.Currency;
import Coinclasses.CurrencyCode;
import Coinclasses.WalletSection;
import sharetest.com.coinwallet.R;

/**
 * Created by guptapc on 27/02/18.
 */

public class WalletListAdapter extends RecyclerView.Adapter<WalletListAdapter.ViewHolder> {

    List<WalletSection> sections = null;
    private static LayoutInflater inflater=null;
    Context mContext;
    public RecyclerViewClickListener mListener;

    public WalletListAdapter(Context context, List<WalletSection> walletsections, RecyclerViewClickListener mListener) {


        mContext=context;
        this.mListener=mListener;
        sections=walletsections;
    }


    // Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
    @Override
    public WalletListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a layout
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_listview, null);

        WalletListAdapter.ViewHolder ViewHolder = new ViewHolder(view,mListener);
        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(final WalletListAdapter.ViewHolder viewHolder, final int position ) {

        WalletSection section = sections.get(position);

        viewHolder.Currency_Code.setText(section.getCurrency().getCurrencyCode());
        viewHolder.Cashinvested.setText(String.valueOf(section.getCashInvested()));
        try {

            viewHolder.CashRedeemed.setText(section.getCashRedeemed() + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewHolder.CurrentBalance.setText(section.getCurrentBalance() + "" );
        setchartValue(viewHolder.mChart);

        String currencyName= section.getCurrency().getCurrencyCode().toLowerCase();
        Integer x=mContext.getResources().getIdentifier(currencyName, "drawable", mContext.getPackageName());
        viewHolder.Currency_image.setImageResource(x);

    }


    // initializes textview in this class
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public TextView Currency_Code; // title
        public TextView CashRedeemed; // artist name
        public TextView Cashinvested; // artist name
        public TextView CurrentBalance;
        public ImageView Currency_image;
        public CandleStickChart mChart;
        public RecyclerViewClickListener mListener;

        public ViewHolder(View itemLayoutView, RecyclerViewClickListener listener) {
            super(itemLayoutView);

            Currency_Code = (TextView)itemLayoutView.findViewById(R.id.title); // title
            CashRedeemed = (TextView)itemLayoutView.findViewById(R.id.artist); // artist name
            Cashinvested = (TextView)itemLayoutView.findViewById(R.id.CashInvested); // artist name
            CurrentBalance = (TextView)itemLayoutView.findViewById(R.id.duration); // duration
            Currency_image=(ImageView)itemLayoutView.findViewById(R.id.list_image); // thumb image
            mChart = (CandleStickChart) itemLayoutView.findViewById(R.id.chart1);
            mListener=listener;
            itemLayoutView.setOnClickListener(this);


        }
        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
    //Returns the total number of items in the data set hold by the adapter.
    @Override
    public int getItemCount() {
        return sections.size();
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


}
