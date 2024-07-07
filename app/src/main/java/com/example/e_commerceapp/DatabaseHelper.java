package com.example.e_commerceapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ecommerce.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "email TEXT UNIQUE," +
                "password TEXT" +
                ")");

        db.execSQL("CREATE TABLE products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "description TEXT," +
                "price REAL," +
                "category TEXT" +
                ")");

        db.execSQL("CREATE TABLE cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_id INTEGER," +
                "quantity INTEGER," +
                "FOREIGN KEY(product_id) REFERENCES products(id)" +
                ")");

        // Insert sample data
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE products ADD COLUMN category TEXT");
            insertSampleData(db);
        }
    }

    private void insertSampleData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO products (name, description, price, category) VALUES" +
                "('Phone', 'Latest smartphone', 699.99, 'Electronics')," +
                "('Laptop', 'High-performance laptop', 999.99, 'Electronics')," +
                "('Headphones', 'Noise-cancelling headphones', 199.99, 'Electronics')," +
                "('T-Shirt', 'Comfortable cotton t-shirt', 19.99, 'Clothing')," +
                "('Jeans', 'Stylish denim jeans', 49.99, 'Clothing')," +
                "('Jacket', 'Warm winter jacket', 89.99, 'Clothing')," +
                "('Blender', 'Powerful kitchen blender', 29.99, 'Home & Kitchen')," +
                "('Toaster', '4-slice toaster', 24.99, 'Home & Kitchen')," +
                "('Microwave', 'Compact microwave oven', 59.99, 'Home & Kitchen')," +
                "('Novel', 'Bestselling novel', 14.99, 'Books')," +
                "('Textbook', 'Educational textbook', 79.99, 'Books')," +
                "('Magazine', 'Monthly magazine subscription', 9.99, 'Books')");
    }
}
