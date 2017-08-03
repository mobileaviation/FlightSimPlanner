package nl.robenanita.googlemapstest.InfoWindows;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.robenanita.googlemapstest.Navaid;
import nl.robenanita.googlemapstest.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavaidInfoWindowFragment extends Fragment {


    public NavaidInfoWindowFragment() {
        // Required empty public constructor
    }

    private Context context;
    private TextView textView;

    private Navaid navaid;
    public void SetNavaid(Navaid navaid, Context context)
    {
        this.context = context;
        this.navaid = navaid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navaid_info_window, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = (TextView)view.findViewById(R.id.navaidInfoWindowInfoTxt);
        if (navaid != null) {
            String info = navaid.getNavaidInfo();
            textView.setText(info);
        }
    }
}
