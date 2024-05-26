package com.ydd.zhichat.ui.other.frament;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ydd.zhichat.R;

public class ImageFragment extends Fragment implements View.OnClickListener {
    @Override
    public void onClick(View v) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_viedio, container, false);

        return view;
    }
}
