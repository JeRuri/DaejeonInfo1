package com.abs2432gmail.daejeoninfo.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.abs2432gmail.daejeoninfo.R;
import com.abs2432gmail.daejeoninfo.WebViewActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment1 extends Fragment {


    public MainFragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_fragment1, container, false);

        ImageView daejeonURL = (ImageView) view.findViewById(R.id.daejeonURL);
        daejeonURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WebViewActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("url","http://www.daejeon.go.kr/");

                intent.putExtra("data",bundle);
                startActivity(intent);
            }
        });
        return view;
    }

}
