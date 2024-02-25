package github.daisukiKaffuChino.MomoQR;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewParent;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.customview.widget.Openable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;

import github.daisukiKaffuChino.MomoQR.databinding.ActivityStartBinding;
import github.daisukiKaffuChino.MomoQR.logic.utils.MyUtil;

public class StartActivity extends BaseActivity {

    private AppBarConfiguration appBarConfiguration;
    private long lastBackPressTime = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityStartBinding binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.startToolbar);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean("forceDark", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_fav, R.id.nav_settings)
                .setOpenableLayout(binding.drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        setupWithNavController(binding.navView, navController);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                    binding.drawer.closeDrawer(GravityCompat.START);
                    return;
                }
                long currentTIme = System.currentTimeMillis();
                if (lastBackPressTime == -1L || currentTIme - lastBackPressTime >= 2000) {
                    MyUtil.toast(R.string.double_click_exit);
                    lastBackPressTime = currentTIme;
                } else
                    finish();
            }
        });

        if (getIntent().getBooleanExtra("startScan",false)){
            Bundle bundle=new Bundle();
            bundle.putBoolean("startScanIntent",true);
            navController.navigate(R.id.nav_home,bundle);
        }
    }

    private void setupWithNavController(@NonNull final NavigationView navigationView, @NonNull final NavController navController) {
        navigationView.setNavigationItemSelectedListener(item -> {
            MenuItem checkedItem = navigationView.getCheckedItem();
            boolean handled = false;
            if (checkedItem != null && checkedItem.getItemId() == item.getItemId()) {
                handled = true;
            } else {
                if (item.getItemId() == R.id.nav_exit)
                    finish();
                else
                    handled = NavigationUI.onNavDestinationSelected(item, navController);
            }
            ViewParent parent = navigationView.getParent();
            if (parent instanceof Openable) {
                ((Openable) parent).close();
            }
            return handled;
        });
        final WeakReference<NavigationView> weakReference = new WeakReference<>(navigationView);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                NavigationView view = weakReference.get();
                if (view == null) {
                    navController.removeOnDestinationChangedListener(this);
                    return;
                }
                Menu menu = view.getMenu();
                for (int h = 0, size = menu.size(); h < size; h++) {
                    MenuItem item = menu.getItem(h);
                    item.setChecked(matchDestination(destination, item.getItemId()));
                }
            }
        });
    }

    private static boolean matchDestination(@NonNull NavDestination destination,
                                            @IdRes int destId) {
        NavDestination currentDestination = destination;
        while (currentDestination.getId() != destId && currentDestination.getParent() != null) {
            currentDestination = currentDestination.getParent();
        }
        return currentDestination.getId() == destId;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}