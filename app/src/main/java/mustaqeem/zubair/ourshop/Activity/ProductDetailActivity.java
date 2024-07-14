package mustaqeem.zubair.ourshop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.PixelCopy;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONException;
import org.json.JSONObject;

import mustaqeem.zubair.ourshop.R;
import mustaqeem.zubair.ourshop.databinding.ActivityProductDetailBinding;
import mustaqeem.zubair.ourshop.models.Product;
import mustaqeem.zubair.ourshop.utils.Constants;

public class ProductDetailActivity extends AppCompatActivity {

    ActivityProductDetailBinding binding ;

    Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
           String name = getIntent().getStringExtra("name");
           String image = getIntent().getStringExtra("image");
           int id = getIntent().getIntExtra("id",0);
           double price = getIntent().getDoubleExtra("price",0);

           getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           getSupportActionBar().setTitle(name);

            Glide.with(this).load(image).into(binding.productImage);
            getProductDetails(id);

            Cart cart = TinyCartHelper.getCart();
           binding.addToCardBtn.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                 cart.addItem(currentProduct,1);
               }
           });
            return insets;
        });
    }
    private void getProductDetails(int id){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCT_DETAILS_URL+id;

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    if(object.getString("status").equals("success")){
                        JSONObject childobject = object.getJSONObject("product");
                        String description = childobject.getString("description");
                        binding.productDescription.setText(Html.fromHtml(description));

                        currentProduct =new Product(
                                childobject.getString("name"),
                                Constants.PRODUCTS_IMAGE_URL+childobject.getString("image"),
                                childobject.getString("status"),
                                childobject.getDouble("price"),
                                childobject.getDouble("price_discount"),
                                childobject.getInt("stock"),
                                childobject.getInt("id")
                        );

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        queue.add(request);


    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart,menu);
            return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.cart){
            startActivity(new Intent(this,CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}