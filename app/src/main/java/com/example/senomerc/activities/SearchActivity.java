package com.example.senomerc.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    Toolbar toolbar;
    SearchView searchView;

    HashMap<String, String> dbTags;
    ListView listView;
    List<String> recommendations;

    FirebaseFirestore db;
    ImageView meme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        createToolbar();

        createTags();

        // createSearchView();
    }

    private void createToolbar() {
        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void createSearchView() {
        recommendations = new ArrayList<>();
        listView = findViewById(R.id.listView);
        meme = findViewById(R.id.meme);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query = query.replace(',',' ');
                query = query.trim();
                query = query.replace(' ',',');
                while (query.contains(",,")){
                    query = query.replace(",,",",");
                }
                query = query.toLowerCase();
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
                if (newText != null) {
                    newText = newText.trim();
                    newText.toLowerCase(Locale.ROOT);
                }
                if (newText == null || newText.compareTo("") == 0){
                    meme.setImageResource(R.drawable.anyameme);
                    listView.setAdapter(new ArrayAdapter<String>(
                            SearchActivity.this, android.R.layout.simple_list_item_1, new ArrayList<>()));
                    return false;
                }
                else meme.setImageResource(android.R.color.transparent);
                String prefix = "";
                String suffix = newText;
                int idx = newText.lastIndexOf(' ');
                if (idx != -1){
                    idx += 1;
                    prefix = newText.substring(0, idx);
                    suffix = newText.substring(idx);
                    suffix = suffix.toLowerCase();
                }
                String sus = newText.toLowerCase();
                recommendations.clear();;
                for (String tag : dbTags.keySet()){
                    if (tag.startsWith(suffix)){
                        recommendations.add(prefix + tag);
                    }
                    else if (tag.startsWith(sus)){
                        recommendations.add(newText + tag.substring(newText.length()));
                    }
                    if (recommendations.size() > 6) break;
                }
                listView.setAdapter(new ArrayAdapter<String>(
                        SearchActivity.this, android.R.layout.simple_list_item_1, recommendations));
                return true;
            }
        });

        searchView.setIconified(false);
        searchView.setQueryHint(getResources().getString(R.string.searchHint));

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
                            if (prodTag.compareTo("NEW") == 0 || prodTag.compareTo("POPULAR") == 0) continue;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.searchProducts);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setSubmitButtonEnabled(true);

        createSearchView();

        return true;
    }
}