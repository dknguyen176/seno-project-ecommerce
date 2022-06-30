package com.example.senomerc.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.senomerc.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        createImageSlider(root);

        return root;
    }

    private void createImageSlider(View root) {
        ImageSlider imageSlider = root.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.banner1, "Banner 1", ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner2, "Banner 2", ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner3, "Banner 3", ScaleTypes.FIT));

        imageSlider.setImageList(slideModels);
    }
}