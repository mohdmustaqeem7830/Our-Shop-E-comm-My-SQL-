package mustaqeem.zubair.ourshop.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import mustaqeem.zubair.ourshop.R;
import mustaqeem.zubair.ourshop.adapters.CartAdapter;
import mustaqeem.zubair.ourshop.databinding.ActivityCheckoutBinding;
import mustaqeem.zubair.ourshop.models.Product;
import mustaqeem.zubair.ourshop.utils.Constants;

public class CheckoutActivity extends AppCompatActivity {

    CartAdapter cartAdapter;

    ArrayList<Product> productArrayList;

    ActivityCheckoutBinding binding;

    ProgressDialog progressDialog;
    Cart cart;

    double totalPrice = 0;
    int tax = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            cart = TinyCartHelper.getCart();


            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Processing...");
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

            totalPrice = ((cart.getTotalPrice().doubleValue()*tax)/100)+cart.getTotalPrice().doubleValue();
            binding.total.setText("INR "+totalPrice);


            binding.checkoutbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                    processOrder();
                }
            });



            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            return insets;
        });


    }

    private void processOrder() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject productOrder = new JSONObject();
        JSONObject dataobject = new JSONObject();
        try {
            productOrder.put("address",binding.address.getText().toString());
            productOrder.put("buyer",binding.name.getText().toString());
            productOrder.put("comment",binding.comment.getText().toString());
            productOrder.put("created_at", Calendar.getInstance().getTimeInMillis());
            productOrder.put("last_update", Calendar.getInstance().getTimeInMillis());
            productOrder.put("date_ship", Calendar.getInstance().getTimeInMillis());
            productOrder.put("email",binding.email.getText().toString());
            productOrder.put("phone",binding.phonet.getText().toString());
            productOrder.put("serial","cab8c1a4e4421a3b");
            productOrder.put("shipping","");
            productOrder.put("shipping_location","");
            productOrder.put("shipping_rate","0.0");
            productOrder.put("status","WAITING");
            productOrder.put("tax",tax);
            productOrder.put("total_fees",totalPrice);

            JSONArray product_order_detail = new JSONArray();

            for (Map.Entry<Item,Integer> item: cart.getAllItemsWithQty().entrySet()){
                Product product = (Product) item.getKey();
                int quantity = item.getValue();
                product.setQuantity(quantity);
                productArrayList.add(product);

                JSONObject productObj = new JSONObject();
                productObj.put("amount",quantity);
                productObj.put("price_item",product.getPrice());
                productObj.put("product_id",product.getId());
                productObj.put("product_name",product.getName());
                product_order_detail.put(productObj);
            }
            dataobject.put("product_order",productOrder);
            dataobject.put("product_order_detail",product_order_detail);



        }catch (JSONException e){

        }

        //Normal me hum String request bhej rhe the but ab data post karna h wo bhi json me to jsonobject request hogi
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.POST_ORDER_URL, dataobject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("success")){
                        Toast.makeText(CheckoutActivity.this, "Ordered Successfully", Toast.LENGTH_SHORT).show();
                        //Ordered number per hi payment hota h to pahle order number lenge
                        String orderNumber = response.getJSONObject("data").getString("code");
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order Successful")
                                .setMessage("Your order number is : "+ orderNumber)
                                .setCancelable(false)
                                .setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(CheckoutActivity.this,PaymentActivity.class);
                                        intent.putExtra("orderCode",orderNumber);
                                        startActivity(intent);
                                    }
                                }).show();
                    }
                    else{
                        Toast.makeText(CheckoutActivity.this, "Ordered failed", Toast.LENGTH_SHORT).show();
                        new AlertDialog.Builder(CheckoutActivity.this)
                                .setTitle("Order Failer")
                                .setCancelable(false)
                                .setMessage("Something went wrong please try again")
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                progressDialog.dismiss();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                Toast.makeText(CheckoutActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(CheckoutActivity.this)
                        .setTitle("Order Failer")
                        .setCancelable(false)
                        .setMessage("Something went wrong please try again")
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               Map<String,String> header = new HashMap<>();
               header.put("Security","secure_code");
                return header;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                50000,  // timeout duration (in milliseconds)
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,  // number of retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(request);
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return super.onNavigateUp();
    }
}