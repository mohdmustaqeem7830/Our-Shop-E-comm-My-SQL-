package mustaqeem.zubair.ourshop.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;

import mustaqeem.zubair.ourshop.R;
import mustaqeem.zubair.ourshop.databinding.ItemCartBinding;
import mustaqeem.zubair.ourshop.databinding.QuantityDialogueBinding;
import mustaqeem.zubair.ourshop.models.Product;

public class CartAdapter  extends  RecyclerView.Adapter<CartAdapter.cartAdapterViewHolder> {

    Context context;
    ArrayList<Product> productArrayList;
    CartListener cartListener;
    Cart cart;
    public interface CartListener{
        public void onQuantityChanged();
    }

    public CartAdapter(ArrayList<Product> productArrayList,Context context,CartListener cartListener){
        this.productArrayList = productArrayList;
        this.context = context;
        this.cartListener = cartListener;
        cart = TinyCartHelper.getCart();
    }

    @NonNull
    @Override
    public cartAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new cartAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull cartAdapterViewHolder holder, int position) {
        Product product = productArrayList.get(position);
        holder.binding.name.setText(product.getName());
        Glide.with(context).load(product.getImage()).into(holder.binding.cartImage);
        holder.binding.price.setText("INR "+product.getPrice());
        holder.binding.quantity.setText(product.getQuantity()+" item(s)");


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                QuantityDialogueBinding quantityDialogueBinding = QuantityDialogueBinding.inflate(LayoutInflater.from(context));
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setView(quantityDialogueBinding.getRoot())
                        .create();

                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

                quantityDialogueBinding.productName.setText(product.getName());
                quantityDialogueBinding.productStock.setText("Stock : "+product.getStock());
                quantityDialogueBinding.quantityText.setText(String.valueOf(product.getQuantity()));

                int stock = product.getStock();

                quantityDialogueBinding.increaseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    int quantity = product.getQuantity();
                    quantity++;
                    if (quantity>product.getStock()){
                        Toast.makeText(context, "Max Stock available is "+ product.getStock(), Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        product.setQuantity(quantity);
                        quantityDialogueBinding.quantityText.setText(String.valueOf(quantity));
                        notifyDataSetChanged();
                        cart.updateItem(product, product.getQuantity());
                        cartListener.onQuantityChanged();
                    }

                    }
                });

                quantityDialogueBinding.decreaseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int quantity = product.getQuantity();
                        if(quantity>1){
                            quantity--;
                        }
                        product.setQuantity(quantity);
                        quantityDialogueBinding.quantityText.setText(String.valueOf(quantity));
                        notifyDataSetChanged();
                        cart.updateItem(product, product.getQuantity());
                        cartListener.onQuantityChanged();
                    }
                });

                quantityDialogueBinding.save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       alertDialog.dismiss();

                       notifyDataSetChanged();
                       cart.updateItem(product, product.getQuantity());
                       cartListener.onQuantityChanged();


                    }
                });

                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class cartAdapterViewHolder extends RecyclerView.ViewHolder {
        ItemCartBinding binding ;
        public cartAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCartBinding.bind(itemView);

        }
    }
}
