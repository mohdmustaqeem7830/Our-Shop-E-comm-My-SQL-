package mustaqeem.zubair.ourshop.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import mustaqeem.zubair.ourshop.R;
import mustaqeem.zubair.ourshop.databinding.ActivityPaymentBinding;
import mustaqeem.zubair.ourshop.utils.Constants;

public class PaymentActivity extends AppCompatActivity {
    ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            String orderCode = getIntent().getStringExtra("orderCode");

            binding.webview.setMixedContentAllowed(true);
            binding.webview.loadUrl(Constants.PAYMENT_URL+orderCode);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            return insets;
        });
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return super.onNavigateUp();
    }
}