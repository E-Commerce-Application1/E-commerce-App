package com.example.e_commerceapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private SQLiteDatabase db;
    private String currentCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productList = new ArrayList<>();

        // Initialize database
        DatabaseHelper helper = new DatabaseHelper(this);
        db = helper.getReadableDatabase();

        loadProducts(null, null);

        productAdapter = new ProductAdapter(productList, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra("productId", product.getId());
                startActivity(intent);
            }
        }, this);

        productRecyclerView.setAdapter(productAdapter);

        // Setup cart button
        findViewById(R.id.cartButton).setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Setup filter button
        findViewById(R.id.filterButton).setOnClickListener(v -> {
            showFilterDialog();
        });
    }

    private void loadProducts(String query, String category) {
        productList.clear();
        Cursor cursor = null;
        try {
            if (TextUtils.isEmpty(query) && TextUtils.isEmpty(category)) {
                cursor = db.rawQuery("SELECT * FROM products", null);
            } else if (TextUtils.isEmpty(query)) {
                cursor = db.rawQuery("SELECT * FROM products WHERE category=?", new String[]{category});
            } else if (TextUtils.isEmpty(category)) {
                cursor = db.rawQuery("SELECT * FROM products WHERE name LIKE ?", new String[]{"%" + query + "%"});
            } else {
                cursor = db.rawQuery("SELECT * FROM products WHERE name LIKE ? AND category=?", new String[]{"%" + query + "%", category});
            }

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String description = cursor.getString(cursor.getColumnIndex("description"));
                    double price = cursor.getDouble(cursor.getColumnIndex("price"));
                    String productCategory = cursor.getString(cursor.getColumnIndex("category"));
                    productList.add(new Product(id, name, description, price, productCategory));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (productAdapter != null) {
            productAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search products");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadProducts(query, currentCategory);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadProducts(newText, currentCategory);
                return false;
            }
        });

        return true;
    }

    private void showFilterDialog() {
        String[] categories = {"All", "Electronics", "Clothing", "Home & Kitchen", "Books"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter by Category")
                .setItems(categories, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            currentCategory = null;
                            break;
                        case 1:
                            currentCategory = "Electronics";
                            break;
                        case 2:
                            currentCategory = "Clothing";
                            break;
                        case 3:
                            currentCategory = "Home & Kitchen";
                            break;
                        case 4:
                            currentCategory = "Books";
                            break;
                    }
                    loadProducts(null, currentCategory);
                });
        builder.create().show();
    }
}
