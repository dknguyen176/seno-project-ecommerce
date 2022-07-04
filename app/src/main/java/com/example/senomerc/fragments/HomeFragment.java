package com.example.senomerc.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import com.example.senomerc.activities.AllCategoryActivity;
import com.example.senomerc.activities.AllProductsActivity;
import com.example.senomerc.adapters.CategoryAdapter;
import com.example.senomerc.adapters.ProductsAdapter;
import com.example.senomerc.model.CategoryModel;
import com.example.senomerc.R;
import com.example.senomerc.model.ProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView catRecyclerView, newProductRecyclerView, popularProductRecyclerView;

    // Category RecyclerView
    CategoryAdapter categoryAdapter;
    ArrayList<CategoryModel> categoryModelList;

    // New Products RecyclerView
    ProductsAdapter newProductsAdapter;
    List<ProductsModel> newProductsModelList;

    // Popular Products RecyclerView
    ProductsAdapter popularProductsAdapter;
    List<ProductsModel> popularProductsModelList;

    // Firestore
    FirebaseFirestore db;

    final int popular_shown = 6;
    final int new_shown = 6;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Intent intent = getActivity().getIntent();
        categoryModelList = (ArrayList<CategoryModel>) intent.getSerializableExtra("CategoryList");
        newProductsModelList = (ArrayList<ProductsModel>) intent.getSerializableExtra("NewList");
        popularProductsModelList = (ArrayList<ProductsModel>) intent.getSerializableExtra("PopularList");

        db = FirebaseFirestore.getInstance();

        createCategoryList(root);

        createNewProductsList(root);

        createPopularProductsList(root);

        createSeeAllOnClick(root);
        return root;
    }

    private void createSeeAllOnClick(View root) {
        TextView newProductSeeAll = root.findViewById(R.id.new_see_all);

        newProductSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllProductsActivity.class);
                intent.putExtra("tags", "new");
                intent.putExtra("title", "New Products");
                startActivity(intent);
            }
        });

        TextView popularProductSeeAll = root.findViewById(R.id.popular_see_all);

        popularProductSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllProductsActivity.class);
                intent.putExtra("tags", "popular");
                intent.putExtra("title", "Popular Products");
                startActivity(intent);
            }
        });

        TextView categorySeeAll = root.findViewById(R.id.category_see_all);

        categorySeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AllCategoryActivity.class));
            }
        });
    }

    private void createPopularProductsList(View root) {
        popularProductRecyclerView = root.findViewById(R.id.popular_rec);
        popularProductRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        // popularProductsModelList = new ArrayList<>();
        popularProductsAdapter = new ProductsAdapter(getActivity(),popularProductsModelList, R.layout.product_large);
        popularProductRecyclerView.setAdapter(popularProductsAdapter);
    }

    private void createNewProductsList(View root) {
        newProductRecyclerView = root.findViewById(R.id.new_product_rec);
        newProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        // newProductsModelList = new ArrayList<>();
        newProductsAdapter = new ProductsAdapter(getActivity(),newProductsModelList, R.layout.products);
        newProductRecyclerView.setAdapter(newProductsAdapter);
    }


    private void createCategoryList(View root) {
        catRecyclerView = root.findViewById(R.id.rec_category);
        catRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        // categoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getActivity(),categoryModelList,R.layout.category_list);
        catRecyclerView.setAdapter(categoryAdapter);
    }

}