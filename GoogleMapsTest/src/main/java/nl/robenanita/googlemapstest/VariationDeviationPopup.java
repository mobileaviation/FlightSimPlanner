package nl.robenanita.googlemapstest;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by Rob Verhoef on 10-4-14.
 */


public class VariationDeviationPopup extends PopupWindow {
    private Context c;
    private View layout;
    private String nums[];
    private NumberPicker np;

    public Boolean result;

    public VariationDeviationPopup(Context context, View layout, HeadingError errorType)
    {
        super(context);
        c = context;
        this.layout = layout;

        result = false;

        nums = new String[50];
        for (int i = -25; i<25; i++)
        {
            nums[i+25] = Integer.toString(i);
        }

        np = (NumberPicker) layout.findViewById(R.id.vardevNumPick);
        np.setMaxValue(nums.length-1);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setDisplayedValues(nums);

        TextView headingErrorTxt = (TextView) layout.findViewById(R.id.headingErrorTitleTxt);
        Button setHeadingErrorBtn = (Button) layout.findViewById(R.id.setvardevBtn);

        setHeadingErrorBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result = true;
                dismiss();
            }
        });

        if (errorType == HeadingError.deviation)
        {
            headingErrorTxt.setText("Set Compass Deviation");
            setHeadingErrorBtn.setText("Set Deviation");
        }

        if (errorType == HeadingError.variation)
        {
            headingErrorTxt.setText("Set Magnetic Variation");
            setHeadingErrorBtn.setText("Set Variation");
        }


    }

    public Integer GetValue()
    {
        Integer v = np.getValue();
        return v-25;
    }

    public void SetValue(Integer value)
    {
        Integer v = value + 25;
        np.setValue(v);
    }




}
