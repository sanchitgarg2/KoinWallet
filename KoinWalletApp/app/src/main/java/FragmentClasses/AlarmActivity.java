package FragmentClasses;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import sharetest.com.coinwallet.R;

/**
 * Created by guptapc on 09/03/18.
 */

public class AlarmActivity extends Fragment {


    private RadioGroup radioGroup;
    private RadioButton one_day;
    private RadioButton two_day;
    private RadioButton checkbutton;


    private ConstraintLayout alarmsetlayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_alarm, container, false);
        alarmsetlayout=(ConstraintLayout)v.findViewById(R.id.alarmsetbox);
        alarmsetlayout.setVisibility(ConstraintLayout.GONE);


        radioGroup = (RadioGroup)v.findViewById(R.id.radioGroup);
        one_day = (RadioButton)v.findViewById(R.id.radioButton1) ;
        two_day= (RadioButton)v.findViewById(R.id.radioButton2) ;


        checkbutton=one_day;
        radioGroup.clearCheck();
        radioGroup.check(one_day.getId());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkbutton = (RadioButton) group.findViewById(checkedId);
                if (null != checkbutton && checkedId > -1) {
                    if (checkbutton.getText().equals("TWO DAY")) {
                        two_day.setBackgroundColor(getResources().getColor(R.color.accent_color));
                        one_day.setBackgroundColor(0x00000000);
                        //Toast.makeText(AddTransaction.this, checkbutton.getText() + Integer.toString(checkedId), Toast.LENGTH_SHORT).show();
                    } else {
                        one_day.setBackgroundColor(getResources().getColor(R.color.primary_color));
                        two_day.setBackgroundColor(0x00000000);
                    }
                }


            }

        });
        return v;
    }

    public static AlarmActivity newInstance() {

        AlarmActivity f = new AlarmActivity();
        Bundle b = new Bundle();
        f.setArguments(b);

        return f;
    }
}
