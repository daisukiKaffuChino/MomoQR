package github.daisukiKaffuChino.MomoQR.logic.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import github.daisukiKaffuChino.MomoQR.R;

public class CreateQrListAdapter extends RecyclerView.Adapter<CreateQrListAdapter.CreateQrListVH> {
    private final OnItemClickListener onItemClickListener;
    Map<Integer, String> map;

    public CreateQrListAdapter(Map<Integer, String> map, OnItemClickListener listener) {
        this.map = map;
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public CreateQrListVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_create_more_type_qr, parent, false);
        return new CreateQrListVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateQrListVH holder, int position) {
        ArrayList<Integer> drawables = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();
        for (int key : map.keySet()) {
            drawables.add(key);
            String values = map.get(key);
            titles.add(values);
        }
        holder.title.setText(titles.get(position));
        holder.icon.setImageResource(drawables.get(position));
        holder.content.setOnClickListener(v ->
                onItemClickListener.onClick(position));
    }

    @Override
    public int getItemCount() {
        return map.size();
    }

    static class CreateQrListVH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        LinearLayout content;

        public CreateQrListVH(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.item_create_qr_list_icon);
            title = itemView.findViewById(R.id.item_create_qr_list_title);
            content = itemView.findViewById(R.id.item_create_qr_list_content);
        }
    }

    public interface OnItemClickListener {
        void onClick(int pos);
    }
}
