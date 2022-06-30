package com.example.senomerc.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
*/
import com.example.senomerc.adapters.CategoryAdapter;
import com.example.senomerc.adapters.NewProductsAdapter;
import com.example.senomerc.model.CategoryModel;
import com.example.senomerc.R;
import com.example.senomerc.model.NewProductsModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView catRecyclerView, newProductRecyclerView;

    // Category RecyclerView
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList;

    // New Products RecyclerView
    NewProductsAdapter newProductsAdapter;
    List<NewProductsModel> newProductsModelList;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        createImageSlider(root);

        createCategoryList(root);

        createNewProductsList(root);


        return root;
    }

    private void createNewProductsList(View root) {
        newProductRecyclerView = root.findViewById(R.id.rec_category);
        newProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        newProductsModelList = new ArrayList<>();
        newProductsAdapter = new NewProductsAdapter(getActivity(),newProductsModelList);
        newProductRecyclerView.setAdapter(categoryAdapter);

        String string = "";
        InputStream is = this.getResources().openRawResource(R.raw.newproducts_list_item);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while (true) {
            try {
                if ((string = reader.readLine()) == null) break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String[] arr = string.split("\\|",-1);
            if (arr.length < 5) continue;
            newProductsModelList.add(new NewProductsModel(arr[0], arr[1], arr[2], Integer.valueOf(arr[3]), arr[4]));
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createCategoryList(View root) {
        catRecyclerView = root.findViewById(R.id.rec_category);
        catRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        categoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getActivity(),categoryModelList);
        catRecyclerView.setAdapter(categoryAdapter);

        String string = "";
        InputStream is = this.getResources().openRawResource(R.raw.category_list_items);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while (true) {
            try {
                if ((string = reader.readLine()) == null) break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String[] arr = string.split("\\|",-1);
            if (arr.length < 3) continue;
            categoryModelList.add(new CategoryModel(arr[0], arr[1], arr[2]));
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void createImageSlider(View root) {
        /*
        ImageSlider imageSlider = root.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.banner1, "Banner 1", ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner2, "Banner 2", ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner3, "Banner 3", ScaleTypes.FIT));

        imageSlider.setImageList(slideModels);
        */
    }
}