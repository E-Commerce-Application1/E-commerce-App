package com.example.e_commerceapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private SQLiteDatabase db;
    private RadioGroup paymentMethodRadioGroup;
    private RadioButton codRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Setup toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItemList = new ArrayList<>();

        paymentMethodRadioGroup = findViewById(R.id.paymentMethodRadioGroup);
        codRadioButton = findViewById(R.id.codRadioButton);

        DatabaseHelper helper = new DatabaseHelper(this);
        db = helper.getReadableDatabase();

        loadCartItems();

        cartAdapter = new CartAdapter(cartItemList, this);
        cartRecyclerView.setAdapter(cartAdapter);

        // Setup checkout button
        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(v -> {
            int selectedId = paymentMethodRadioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(CartActivity.this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            } else if (selectedId == R.id.codRadioButton) {
                showConfirmationDialog();
            }
        });
    }

    private void loadCartItems() {
        cartItemList.clear();
        Cursor cursor = db.rawQuery("SELECT products.id AS product_id, products.name, products.price, cart.quantity FROM cart JOIN products ON cart.product_id = products.id", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int productId = cursor.getInt(cursor.getColumnIndex("product_id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                double price = cursor.getDouble(cursor.getColumnIndex("price"));
                int quantity = cursor.getInt(cursor.getColumnIndex("quantity"));
                cartItemList.add(new CartItem(productId, name, price, quantity));
            }
            cursor.close();
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Order")
                .setMessage("Are you sure you want to place the order with Cash on Delivery?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        placeOrder();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void placeOrder() {
        // Implement your order placement logic here
        // For demonstration, let's clear the cart and show a thank you popup
        db.execSQL("DELETE FROM cart");
        cartItemList.clear();
        cartAdapter.notifyDataSetChanged();
        showThankYouPopup();
    }

    private void showThankYouPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thank You!")
                .setMessage("Thank you for shopping with us. Your order has been placed successfully.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CartActivity.this, ProductListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}
