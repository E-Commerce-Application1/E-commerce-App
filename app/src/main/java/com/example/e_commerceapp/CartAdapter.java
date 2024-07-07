package com.example.e_commerceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private Context context;
    private SQLiteDatabase db;

    public CartAdapter(List<CartItem> cartItemList, Context context) {
        this.cartItemList = cartItemList;
        this.context = context;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.productNameCart.setText(cartItem.getProductName());
        holder.productPriceCart.setText("Price: $" + cartItem.getProductPrice());
        holder.productQuantityCart.setText("Quantity: " + cartItem.getQuantity());

        holder.itemView.setOnLongClickListener(v -> {
            showRemoveDialog(cartItem, position);
            return true;
        });
    }

    private void showRemoveDialog(CartItem cartItem, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Remove item")
                .setMessage("Do you want to remove this item from the cart?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    removeItem(cartItem, position);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeItem(CartItem cartItem, int position) {
        db.delete("cart", "product_id = ?", new String[]{String.valueOf(cartItem.getProductId())});
        cartItemList.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView productNameCart;
        TextView productPriceCart;
        TextView productQuantityCart;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameCart = itemView.findViewById(R.id.productNameCart);
            productPriceCart = itemView.findViewById(R.id.productPriceCart);
            productQuantityCart = itemView.findViewById(R.id.productQuantityCart);
        }
    }
}
