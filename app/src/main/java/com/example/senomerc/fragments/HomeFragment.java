package com.example.senomerc.fragments;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import com.example.senomerc.activities.AllCategoryActivity;
import com.example.senomerc.activities.AllProductsActivity;
import com.example.senomerc.activities.CartActivity;
import com.example.senomerc.activities.MainActivity;
import com.example.senomerc.activities.MapsActivity;
import com.example.senomerc.adapters.CategoryAdapter;
import com.example.senomerc.adapters.ProductsAdapter;
import com.example.senomerc.model.CategoryModel;
import com.example.senomerc.R;
import com.example.senomerc.model.ProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView catRecyclerView, newProductRecyclerView, popularProductRecyclerView;

    // Category RecyclerView
    public static CategoryAdapter categoryAdapter;
    public static ArrayList<CategoryModel> categoryModelList;

    // New Products RecyclerView
    public static ProductsAdapter newProductsAdapter;
    public static List<ProductsModel> newProductsModelList;
    public static HashMap<String, Integer> newProductsModelPosition;

    // Popular Products RecyclerView
    public static ProductsAdapter popularProductsAdapter;
    public static List<ProductsModel> popularProductsModelList;
    public static HashMap<String, Integer> popularProductModelPosition;

    // Firestore
    FirebaseFirestore db;
    FirebaseAuth auth;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        db.collection("Favorites").document(auth.getCurrentUser().getUid()).collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String docId = document.get("id", String.class);

                                if (popularProductModelPosition.containsKey(docId)) {
                                    int position = popularProductModelPosition.get(docId);
                                    popularProductsModelList.get(position).setFavorite(true);
                                }
                                if (newProductsModelPosition.containsKey(docId)) {
                                    int position = newProductsModelPosition.get(docId);
                                    newProductsModelList.get(position).setFavorite(true);
                                }
                            }
                            createNewProductsList(root);
                            createPopularProductsList(root);
                        }
                    }
                });

        createCategoryList(root);

        createSeeAllOnClick(root);

        createShortcut(root);

        return root;
    }

    private void createShortcut(View root) {
        root.findViewById(R.id.sc_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CartActivity.class));
            }
        });

        root.findViewById(R.id.sc_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("AddToCart").document(auth.getCurrentUser().getUid()).collection("User")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    db.collection("AddToCart").document(auth.getCurrentUser().getUid()).collection("User")
                                            .document(document.getId())
                                            .delete();

                                }

                                MainActivity container = (MainActivity) getActivity();
                                container.loadCartCount();
                            }
                        });
            }
        });

        root.findViewById(R.id.sc_fav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AllProductsActivity.class);
                intent.putExtra("title", "Favorite Products");

                startActivity(intent);
            }
        });

        root.findViewById(R.id.sc_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MapsActivity.class));
            }
        });
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
        /*for (ProductsModel productsModel : popularProductsModelList){
            productsModel.setFavorite(favMap.containsKey(productsModel.getId()));
        }*/
        popularProductsAdapter = new ProductsAdapter(getActivity(),popularProductsModelList, R.layout.product_large);
        popularProductRecyclerView.setAdapter(popularProductsAdapter);
    }

    private void createNewProductsList(View root) {
        newProductRecyclerView = root.findViewById(R.id.new_product_rec);
        newProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        // newProductsModelList = new ArrayList<>();
        /*for (ProductsModel productsModel : newProductsModelList){
            productsModel.setFavorite(favMap.containsKey(productsModel.getId()));
        }*/
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

    public static void onClickFavorites(ImageView favorite, ProductsModel productsModel) {
        boolean tag = (boolean) favorite.getTag();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (tag) {
            firestore.collection("Favorites").document(auth.getCurrentUser().getUid()).collection("User")
                            .document(productsModel.getId()).delete();
        }
        else{
            String docId = productsModel.getId();
            firestore.collection("Favorites").document(auth.getCurrentUser().getUid()).collection("User")
                    .document(docId)
                    .set(new HashMap<String, String>() {{
                            put("id", docId);
                    }});
        }
        tag = !tag;
        favorite.setTag(tag);
        productsModel.setFavorite(tag);

        favorite.setImageResource(productsModel.isFavorite()
                ? R.drawable.icons8_favorite_64_1
                : R.drawable.icons8_favorite_64);

        String docId = productsModel.getId();
        if (popularProductModelPosition.containsKey(docId)) {
            int position = popularProductModelPosition.get(docId);
            popularProductsModelList.get(position).setFavorite(tag);
            popularProductsAdapter.notifyItemChanged(position);
        }
        if (newProductsModelPosition.containsKey(docId)) {
            int position = newProductsModelPosition.get(docId);
            newProductsModelList.get(position).setFavorite(tag);
            newProductsAdapter.notifyItemChanged(position);
        }

        if (AllProductsActivity.active) {
            if (AllProductsActivity.productsListPosition.containsKey(docId)) {
                int position = AllProductsActivity.productsListPosition.get(docId);
                AllProductsActivity.productsList.get(position).setFavorite(tag);
                AllProductsActivity.productsAdapter.notifyItemChanged(position);
            }
        }
    }

}