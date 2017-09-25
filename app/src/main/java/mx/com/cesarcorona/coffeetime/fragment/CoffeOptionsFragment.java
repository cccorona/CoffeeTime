package mx.com.cesarcorona.coffeetime.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import mx.com.cesarcorona.coffeetime.R;

/**
 * Created by ccabrera on 24/09/17.
 */

public class CoffeOptionsFragment extends Fragment {


    public static String TAG = CoffeOptionsFragment.class.getSimpleName();



    private LinearLayout linearTimeAndDate , linearLocation, linearTopic, topicContent;
    private ImageView timeFinish, locationFinish , topiFinish;
    private RelativeLayout timeAnDateContent ,locationContent;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.coffe_options_fragment,container,false);
        return  rootView;
    }
}
