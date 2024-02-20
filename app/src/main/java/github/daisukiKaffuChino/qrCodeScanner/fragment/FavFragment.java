package github.daisukiKaffuChino.qrCodeScanner.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import github.daisukiKaffuChino.qrCodeScanner.R;
import github.daisukiKaffuChino.qrCodeScanner.adapter.FavAdapter;
import github.daisukiKaffuChino.qrCodeScanner.bean.FavBean;
import github.daisukiKaffuChino.qrCodeScanner.databinding.FragmentFavBinding;
import github.daisukiKaffuChino.qrCodeScanner.model.FavViewModel;
import github.daisukiKaffuChino.qrCodeScanner.model.FavViewModelFactory;
import github.daisukiKaffuChino.qrCodeScanner.util.FavSqliteHelper;
import github.daisukiKaffuChino.qrCodeScanner.util.MyUtil;

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

        helper = new FavSqliteHelper(requireContext());
        viewModel = new ViewModelProvider(this, new FavViewModelFactory(helper.query())).get(FavViewModel.class);
        if (helper.query().size() > viewModel.ls.size())
            viewModel.ls = helper.query();
        initAdapter(viewModel.ls);

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
                .setNegativeButton(R.string.cancel,null)
                .show());
    }

    private void initAdapter(ArrayList<FavBean> list) {
        adapter = new FavAdapter(requireContext(), list, pos -> {

        }, this::deleteDialog);
        binding.favRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.favRecyclerView.setAdapter(adapter);
        if (list.size() > 0) {
            binding.favEmptyView.setVisibility(View.GONE);
            binding.favFltBtn.setVisibility(View.VISIBLE);
        }
    }

    private void deleteDialog(int pos) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.delete_item);
        builder.setMessage(R.string.sure_to_delete);
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            boolean success = helper.deleteData(viewModel.ls.get(pos).getId());
            if (!success) {
                MyUtil.toast(R.string.delete_fail);
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
