package mustaqeem.zubair.ourshop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.Map;

import mustaqeem.zubair.ourshop.R;
import mustaqeem.zubair.ourshop.adapters.CartAdapter;
import mustaqeem.zubair.ourshop.databinding.ActivityCartBinding;
import mustaqeem.zubair.ourshop.models.Product;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding ;
    CartAdapter cartAdapter;

    ArrayList<Product> productArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityCartBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            Cart cart = TinyCartHelper.getCart();
            productArrayList = new ArrayList<>();

            for (Map.Entry<Item,Integer> item: cart.getAllItemsWithQty().entrySet()){
                Product product = (Product) item.getKey();
                int quantity = item.getValue();
                product.setQuantity(quantity);
                productArrayList.add(product);
            }




//            cartAdapter = new CartAdapter(productArrayList,this);
            cartAdapter = new CartAdapter(productArrayList, this, new CartAdapter.CartListener() {
                @Override
                public void onQuantityChanged() {
                    binding.subtotal.setText(String.format("INR %.2f",cart.getTotalPrice()));
                }
            });
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,linearLayoutManager.getOrientation());

            binding.cartlist.setLayoutManager(linearLayoutManager);
            binding.cartlist.addItemDecoration(dividerItemDecoration);
            binding.cartlist.setAdapter(cartAdapter);
            binding.subtotal.setText(String.format("INR %.2f",cart.getTotalPrice()));

           binding.checkoutBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
               }
           });


         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            return insets;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}