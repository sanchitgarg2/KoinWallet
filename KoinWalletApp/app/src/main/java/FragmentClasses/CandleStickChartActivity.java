
package FragmentClasses;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import Coinclasses.CandleStickDataPoint;
import sharetest.com.coinwallet.LoginActivity;
import sharetest.com.coinwallet.R;
import sharetest.com.coinwallet.postJSONValue;

import static SupportingClasses.Helper.AppURL;
import static SupportingClasses.Helper.CANDLESTICKURL;
import static SupportingClasses.Helper.LOGINURL;
import static SupportingClasses.Helper.WALLETSECTION;

public class CandleStickChartActivity extends Fragment implements OnSeekBarChangeListener {

    private CandleStickChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    private Context mcontext;
    List<CandleStickDataPoint> data = new ArrayList<CandleStickDataPoint>();
    Button buttons[] = new Button[9];


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_candlechart, container, false);


        mcontext = v.getContext();
        tvX = (TextView) v.findViewById(R.id.tvXMax);
        tvY = (TextView) v.findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) v.findViewById(R.id.seekBar1);
        mSeekBarX.setOnSeekBarChangeListener(this);

        mSeekBarY = (SeekBar) v.findViewById(R.id.seekBar2);
        mSeekBarY.setOnSeekBarChangeListener(this);

        mChart = (CandleStickChart) v.findViewById(R.id.chart1);

        getGraphdata("THREE_HOURS");
        InitializeGraph(mChart);

        // setting data

        Resources res = getResources();
        for(int i=0;i<9;i++) {
            int j=i+1;
            String b = "button" + j;
            buttons[i] = (Button)v.findViewById(res.getIdentifier(b, "id", getActivity().getPackageName()));
            buttons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    callgraphforTimelines(getResources().getResourceEntryName(((Button) v).getId()));
                }
            });
        }
        mSeekBarX.setProgress(40);
        mSeekBarY.setProgress(100);

        mChart.getLegend().setEnabled(false);

        return v;
    }


    ArrayList<String > timeLines = new ArrayList<String >(
            Arrays.asList("ALL_TIME", "ONE_YEAR", "SIX_MONTHS","THREE_MONTHS","ONE_MONTH","ONE_WEEK","ONE_DAY","THREE_HOURS","ONE_HOUR"));

    private void callgraphforTimelines(String s) {

            char a_char = s.charAt(6);
            int a=Character.getNumericValue(a_char);
            getGraphdata(timeLines.get(a-1));
            mSeekBarX.setProgress(40);
            mSeekBarY.setProgress(100);
            setGraphValue();


    }

    private void getGraphdata(String TimeLine) {

        String response = null;
        JSONObject obj = new JSONObject();
        //"currencyCode":"XRP",

        obj.put("timeLine",TimeLine);
        obj.put("currencyCode",WALLETSECTION.getCurrency().getCurrencyCode());
        try {
            response = new postJSONValue(mcontext).execute(AppURL + CANDLESTICKURL, obj.toJSONString()).get();

            if (response != null) {

                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(response);

                String reasonCode = json.get("statusCode").toString();

                if (reasonCode.equals("200")) {

                    //mChart.clear();
                    if(json.containsKey("graphData"))
                    processGraphpoints(json.get("graphData").toString());
                    else
                        data= new ArrayList<CandleStickDataPoint>();

                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {

        }
    }

    private void processGraphpoints(String graphData) throws IOException {


        ObjectMapper mapper= new ObjectMapper();
        data = mapper.readValue(graphData, new TypeReference<List<CandleStickDataPoint>>(){});
        Collections.sort(data);


    }


    private void InitializeGraph(CandleStickChart mChart) {

        //mChart.setBackgroundColor(Color.WHITE);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(0);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
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

    }

    public static CandleStickChartActivity newInstance() {

        CandleStickChartActivity f = new CandleStickChartActivity();
        Bundle b = new Bundle();
        f.setArguments(b);

        return f;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        
        int prog = (mSeekBarX.getProgress()+1);

        tvX.setText("" + prog);
        tvY.setText("" + (mSeekBarY.getProgress()));
        
        mChart.resetTracking();

        Log.d("checking the x seekbar", ""+prog);
        Log.d("checking the y seekbar" , "" +seekBar);
        ArrayList<CandleEntry> yVals1 = new ArrayList<CandleEntry>();

        if(data.size()>0) {

            for (int i = 0; i < data.size(); i++) {

                float mult = (mSeekBarY.getProgress() + 1);

                float high = data.get(i).getHigh();
                float low = data.get(i).getLow();

                float open = data.get(i).getOpen();
                float close = data.get(i).getClose();

                long minutes = TimeUnit.MILLISECONDS.toMinutes((data.get(i).getOpenTimeStamp() + data.get(i).getCloseTimeStamp()) / 2);
                yVals1.add(new CandleEntry(i, high, low, open, close, getResources().getDrawable(R.drawable.add)));
                //mChart.getAxis().setValueFormatter();
            }


            CandleDataSet set1 = new CandleDataSet(yVals1, "Data Set");

            set1.setDrawIcons(false);
            set1.setAxisDependency(AxisDependency.LEFT);
            set1.setShadowColor(Color.DKGRAY);
            set1.setShadowWidth(0.9f);
            // set1.setColor(Color.rgb(80, 80, 80));
            set1.setDecreasingColor(Color.RED);
            set1.setDecreasingPaintStyle(Paint.Style.FILL);
            set1.setIncreasingColor(Color.GREEN);
            set1.setIncreasingPaintStyle(Paint.Style.FILL);
            set1.setNeutralColor(Color.BLUE);
            set1.setHighlightLineWidth(1.2f);

            CandleData data = new CandleData(set1);

            mChart.setData(data);
            mChart.invalidate();
        }
    }

    public void setGraphValue(){

        mChart.resetTracking();


        ArrayList<CandleEntry> yVals1 = new ArrayList<CandleEntry>();

        if(data.size()>0) {

            for (int i = 0; i < data.size(); i++) {

                //float mult = (mSeekBarY.getProgress() + 1);

                float high = data.get(i).getHigh();
                float low = data.get(i).getLow();

                float open = data.get(i).getOpen();
                float close = data.get(i).getClose();

                long minutes = TimeUnit.MILLISECONDS.toMinutes((data.get(i).getOpenTimeStamp() + data.get(i).getCloseTimeStamp()) / 2);
                yVals1.add(new CandleEntry(i, high, low, open, close, getResources().getDrawable(R.drawable.add)));
                //mChart.getAxis().setValueFormatter();
            }


            CandleDataSet set1 = new CandleDataSet(yVals1, "Data Set");

            set1.setDrawIcons(false);
            set1.setAxisDependency(AxisDependency.LEFT);
            set1.setShadowColor(Color.DKGRAY);
            set1.setShadowWidth(0.9f);
            // set1.setColor(Color.rgb(80, 80, 80));
            set1.setDecreasingColor(Color.RED);
            set1.setDecreasingPaintStyle(Paint.Style.FILL);
            set1.setIncreasingColor(Color.GREEN);
            set1.setIncreasingPaintStyle(Paint.Style.FILL);
            set1.setNeutralColor(Color.BLUE);
            set1.setHighlightLineWidth(1.2f);

            CandleData data = new CandleData(set1);

            mChart.setData(data);
            mChart.invalidate();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }
}
