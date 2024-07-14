package mustaqeem.zubair.ourshop.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mustaqeem.zubair.ourshop.R;
import mustaqeem.zubair.ourshop.adapters.ProductAdapter;
import mustaqeem.zubair.ourshop.databinding.ActivityCategoryBinding;
import mustaqeem.zubair.ourshop.models.Category;
import mustaqeem.zubair.ourshop.models.Product;
import mustaqeem.zubair.ourshop.utils.Constants;

public class CategoryActivity extends AppCompatActivity {
    ActivityCategoryBinding binding ;
    ProductAdapter productAdapter;
    ArrayList<Product> products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            products = new ArrayList<>();
            productAdapter = new ProductAdapter(products,this);


            int catID = getIntent().getIntExtra("catID",0);
            String categoryName = getIntent().getStringExtra("categoryName");

            getSupportActionBar().setTitle(categoryName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getProducts(catID);

            GridLayoutManager layoutManager = new GridLayoutManager(this,2);
            binding.productlist.setLayoutManager(layoutManager);

            binding.productlist.setAdapter(productAdapter);

            return insets;
        });
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return super.onNavigateUp();
    }

    private void getProducts(int catID){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.GET_PRODUCTS_URL+"?category_id="+catID;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mainObj = new JSONObject(response);
                    if(mainObj.getString("status").equals("success")){
                        JSONArray jsonProductArray  = mainObj.getJSONArray("products");
                        for (int i = 0 ; i < jsonProductArray.length();i++){
                            JSONObject object = jsonProductArray.getJSONObject(i);
                            Product product = new Product(
                                    object.getString("name"),
                                    Constants.PRODUCTS_IMAGE_URL+object.getString("image"),
                                    object.getString("status"),
                                    object.getDouble("price"),
                                    object.getDouble("price_discount"),
                                    object.getInt("stock"),
                                    object.getInt("id")
                            );

                            products.add(product);


                        }
                        productAdapter.notifyDataSetChanged();
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
}