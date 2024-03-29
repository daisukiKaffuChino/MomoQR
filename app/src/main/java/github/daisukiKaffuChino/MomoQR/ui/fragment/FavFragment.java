package github.daisukiKaffuChino.MomoQR.ui.fragment;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.databinding.FragmentFavBinding;
import github.daisukiKaffuChino.MomoQR.logic.adapter.FavAdapter;
import github.daisukiKaffuChino.MomoQR.logic.bean.FavBean;
import github.daisukiKaffuChino.MomoQR.logic.utils.ActionUtil;
import github.daisukiKaffuChino.MomoQR.logic.utils.FavSqliteHelper;
import github.daisukiKaffuChino.MomoQR.ui.model.FavViewModel;
import github.daisukiKaffuChino.MomoQR.ui.model.FavViewModelFactory;

public class FavFragment extends BaseBindingFragment<FragmentFavBinding> {

    private FragmentFavBinding binding;
    private FavViewModel viewModel;
    FavAdapter adapter;
    FavSqliteHelper helper;

    @Override
    protected FragmentFavBinding onCreateViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent) {
        return FragmentFavBinding.inflate(inflater, parent, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = getBinding();

        initFltBtnMargin();

        helper = new FavSqliteHelper(requireContext());
        viewModel = new ViewModelProvider(this, new FavViewModelFactory(helper.query())).get(FavViewModel.class);
        if (helper.query().size() > viewModel.ls.size())
            viewModel.ls = helper.query();
        initAdapter(view, viewModel.ls);

        binding.favFltBtn.setOnClickListener(v -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_all)
                .setMessage(R.string.sure_to_delete)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    viewModel.deleteAll();
                    helper.deleteAllData();
                    adapter.notifyDataSetChanged();
                    v.setVisibility(View.GONE);
                    binding.favEmptyView.setVisibility(View.VISIBLE);
                })
                .setNegativeButton(R.string.cancel, null)
                .show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        helper.closeDB();
    }

    private void initAdapter(View navRoot, ArrayList<FavBean> list) {
        adapter = new FavAdapter(requireContext(), list, pos -> {
            String content = viewModel.ls.get(pos).getContent();
            Bundle args = new Bundle();
            args.putString("content", content);
            args.putBoolean("isFromFav", true);
            Navigation.findNavController(navRoot).navigate(R.id.nav_result, args);
        }, this::deleteDialog);
        if (Resources.getSystem().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            binding.favRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        else
            binding.favRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.favRecyclerView.setAdapter(adapter);
        if (list.size() > 0) {
            binding.favEmptyView.setVisibility(View.GONE);
            binding.favFltBtn.setVisibility(View.VISIBLE);
        }
    }

    private void initFltBtnMargin() {
        Window window = requireActivity().getWindow();
        WindowInsetsCompat windowInsetsCompat = ViewCompat.getRootWindowInsets(window.getDecorView());
        if (windowInsetsCompat != null) {
            Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars());
            int barHeight = insets.bottom;
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) binding.favFltBtn.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, barHeight + 16);
            binding.favFltBtn.requestLayout();
        }
    }

    private void deleteDialog(int pos) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.delete_item);
        builder.setMessage(R.string.sure_to_delete);
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            boolean success = helper.deleteData(viewModel.ls.get(pos).getId());
            if (!success) {
                ActionUtil.toast(R.string.delete_fail);
                dialogInterface.dismiss();
                return;
            }
            viewModel.ls.remove(pos);
            adapter.notifyItemRemoved(pos);
            if (pos != viewModel.ls.size())
                adapter.notifyItemRangeChanged(pos, viewModel.ls.size() - pos);
            if (viewModel.ls.size() == 0) {
                binding.favEmptyView.setVisibility(View.VISIBLE);
                binding.favFltBtn.setVisibility(View.GONE);
            }
            dialogInterface.dismiss();
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }
}
