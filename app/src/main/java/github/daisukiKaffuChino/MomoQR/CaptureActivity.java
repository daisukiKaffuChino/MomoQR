package github.daisukiKaffuChino.MomoQR;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.databinding.ActivityCaptureBinding;

public class CaptureActivity extends BaseActivity {

    private ActivityCaptureBinding binding;
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCaptureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        barcodeScannerView = binding.captureBarcodeView;

        initToolbar();

        Window window = getWindow();
        Objects.requireNonNull(WindowCompat.getInsetsController(window, window.getDecorView())).setAppearanceLightNavigationBars(false);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            Window window = getWindow();
            WindowInsetsCompat windowInsetsCompat = ViewCompat.getRootWindowInsets(window.getDecorView());
            if (windowInsetsCompat != null) {
                Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars());
                int barHeight = insets.bottom;
                ImageView view = binding.captureBulbBtn;
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, barHeight + 16);
                view.requestLayout();
            }
        }
    }

    private void initToolbar() {
        setSupportActionBar(binding.captureToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.scan_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.captureToolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}