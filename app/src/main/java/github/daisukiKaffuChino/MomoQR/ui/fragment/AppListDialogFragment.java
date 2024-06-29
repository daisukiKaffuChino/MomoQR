package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.logic.adapter.AppListAdapter;
import github.daisukiKaffuChino.MomoQR.logic.bean.AppInfoBean;
import github.daisukiKaffuChino.MomoQR.logic.utils.QueryAppsUtil;

public class AppListDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context = requireActivity();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.fragment_qr_create_list, null);
        RecyclerView recyclerView = view.findViewById(R.id.create_list_recyclerView);
        builder.setView(view);

        ArrayList<AppInfoBean> beans = new QueryAppsUtil().getAppList(context.getPackageManager());
        AppListAdapter adapter = new AppListAdapter(context, beans, ext -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            Bundle args = new Bundle();
            args.putString("content", "https://play.google.com/store/apps/details?id=" + ext);
            navController.navigateUp();
            navController.navigate(R.id.nav_result, args);
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        builder.setTitle(R.string.apps);
        builder.setNegativeButton(R.string.cancel, null);
        return builder.create();
    }
}
