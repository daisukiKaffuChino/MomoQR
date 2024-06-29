package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.databinding.FragmentQrCreateListBinding;
import github.daisukiKaffuChino.MomoQR.logic.adapter.AppListAdapter;

public class QrCreateListFragment extends BaseBindingFragment<FragmentQrCreateListBinding> {
    @Override
    protected FragmentQrCreateListBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentQrCreateListBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentQrCreateListBinding binding = getBinding();

        Map<Integer, String> map = new HashMap<>();
        map.put(R.drawable.ic_wifi_plus, getString(R.string.wifi));
        map.put(R.drawable.ic_email_edit_outline, getString(R.string.email));
        map.put(R.drawable.ic_google_play, getString(R.string.apps));
        AppListAdapter adapter = new AppListAdapter(map, title -> {
            if (Objects.equals(title, getString(R.string.wifi))) {
                getNavController().navigate(R.id.nav_wifi_dialog);
            } else if (Objects.equals(title, getString(R.string.email))) {
                getNavController().navigate(R.id.nav_email_dialog);
            } else if (Objects.equals(title, getString(R.string.apps))) {
                getNavController().navigate(R.id.nav_app_list_dialog);
            }
        });
        binding.createListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.createListRecyclerView.setAdapter(adapter);
    }

}
