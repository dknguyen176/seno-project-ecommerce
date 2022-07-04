package com.example.senomerc.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.senomerc.R;
import com.example.senomerc.model.ProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    SearchView searchView;
    HashMap<String, String> dbTags;
    ListView listView;
    List<String> recommendations;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        createTags();

        createSearchView();
    }

    private void createSearchView() {
        recommendations = new ArrayList<>();
        listView = findViewById(R.id.listView);
        searchView = findViewById(R.id.searchProducts);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.replace(' ', ',');
                while (query.contains(",,")){
                    query.replace(",,",",");
                }
                Intent intent = new Intent(SearchActivity.this, AllProductsActivity.class);
                intent.putExtra("title", "Search Result");
                intent.putExtra("tags",query);

                searchView.clearFocus();

                startActivity(intent);
                finish();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.trim();
                if (newText == null || newText.compareTo("") == 0){
                    listView.setAdapter(new ArrayAdapter<String>(
                            SearchActivity.this, android.R.layout.simple_list_item_1, new ArrayList<>()));
                    return false;
                }
                String prefix = "";
                int idx = newText.lastIndexOf(' ');
                if (idx != -1){
                    idx += 1;
                    prefix = newText.substring(0, idx);
                    newText = newText.substring(idx);
                }
                recommendations.clear();;
                for (String tag : dbTags.keySet()){
                    if (tag.startsWith(newText)){
                        recommendations.add(prefix + tag);
                        if (recommendations.size() > 5) break;
                    }
                }
                listView.setAdapter(new ArrayAdapter<String>(
                        SearchActivity.this, android.R.layout.simple_list_item_1, recommendations));
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    itemSelected(recommendations.get(position));
            }
        });
    }

    private void createTags() {
        db = FirebaseFirestore.getInstance();

        dbTags = new HashMap<>();

        db.collection("Product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ProductsModel productsModel = document.toObject(ProductsModel.class);
                        String[] prodTags = productsModel.getTags().split(",");
                        for (String prodTag : prodTags){
                            dbTags.put(prodTag, null);
                        }
                    }
                } else {
                    Toast.makeText(SearchActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void itemSelected(String s){
        searchView.setQuery(s, true);
    }
}