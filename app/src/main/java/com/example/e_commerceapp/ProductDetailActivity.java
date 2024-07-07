package com.example.e_commerceapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProductDetailActivity extends AppCompatActivity {

    private int productId;
    private SQLiteDatabase db;
    private Product product;

    private ImageView productImageDetail;
    private TextView productNameDetail;
    private TextView productPriceDetail;
    private TextView productDescriptionDetail;
    private TextView quantityTextView;
    private Button addToCartButtonDetail;
    private Button increaseQuantityButton;
    private Button decreaseQuantityButton;

    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize views
        productImageDetail = findViewById(R.id.productImageDetail);
        productNameDetail = findViewById(R.id.productNameDetail);
        productPriceDetail = findViewById(R.id.productPriceDetail);
        productDescriptionDetail = findViewById(R.id.productDescriptionDetail);
        quantityTextView = findViewById(R.id.quantityTextView);
        addToCartButtonDetail = findViewById(R.id.addToCartButtonDetail);
        increaseQuantityButton = findViewById(R.id.increaseQuantityButton);
        decreaseQuantityButton = findViewById(R.id.decreaseQuantityButton);

        // Setup toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Get product ID from intent
        productId = getIntent().getIntExtra("productId", -1);

        // Initialize database
        DatabaseHelper helper = new DatabaseHelper(this);
        db = helper.getReadableDatabase();

        loadProductDetails(productId);

        // Set listeners for buttons
        addToCartButtonDetail.setOnClickListener(v -> addToCart(product, quantity));
        increaseQuantityButton.setOnClickListener(v -> {
            quantity++;
            quantityTextView.setText(String.valueOf(quantity));
        });
        decreaseQuantityButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityTextView.setText(String.valueOf(quantity));
            }
        });
    }

    private void loadProductDetails(int productId) {
        Cursor cursor = db.rawQuery("SELECT * FROM products WHERE id = ?", new String[]{String.valueOf(productId)});
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            double price = cursor.getDouble(cursor.getColumnIndex("price"));
            String category = cursor.getString(cursor.getColumnIndex("category"));

            product = new Product(productId, name, description, price, category);

            productNameDetail.setText(name);
            productPriceDetail.setText("$" + price);
            productDescriptionDetail.setText(description);
            // For demonstration purposes, the image is left blank
            productImageDetail.setImageResource(R.drawable.ic_launcher_background);

            cursor.close();
        }
    }

    private void addToCart(Product product, int quantity) {
        // Check if the product is already in the cart
        Cursor cursor = db.rawQuery("SELECT * FROM cart WHERE product_id = ?", new String[]{String.valueOf(product.getId())});
        if (cursor != null && cursor.moveToFirst()) {
            // If the product is already in the cart, increase the quantity
            int currentQuantity = cursor.getInt(cursor.getColumnIndex("quantity"));
            ContentValues values = new ContentValues();
            values.put("quantity", currentQuantity + quantity);
            db.update("cart", values, "product_id = ?", new String[]{String.valueOf(product.getId())});
        } else {
            // If the product is not in the cart, add it with the specified quantity
            ContentValues values = new ContentValues();
            values.put("product_id", product.getId());
            values.put("quantity", quantity);
            db.insert("cart", null, values);
        }

        if (cursor != null) {
            cursor.close();
        }

        Toast.makeText(this, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
    }
}
