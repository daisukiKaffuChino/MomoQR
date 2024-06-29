package github.daisukiKaffuChino.MomoQR.logic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Map;

import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.logic.bean.AppInfoBean;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.SimpleRecyclerVH> {
    private final OnItemClickListener onItemClickListener;
    Map<Integer, String> map;
    ArrayList<AppInfoBean> beans;
    Context context;

    public AppListAdapter(Map<Integer, String> map, OnItemClickListener listener) {
        this.map = map;
        onItemClickListener = listener;
    }

    public AppListAdapter(Context context, ArrayList<AppInfoBean> beans, OnItemClickListener listener) {
        this.beans = beans;
        this.context = context;
        onItemClickListener = listener;
    }

    @NonNull
    @Override
    public SimpleRecyclerVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_create_more_type_qr, parent, false);
        return new SimpleRecyclerVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleRecyclerVH holder, int position) {
        if (map != null) {
            //创建二维码列表的
            ArrayList<Integer> drawables = new ArrayList<>();
            ArrayList<String> titles = new ArrayList<>();
            for (int key : map.keySet()) {
                drawables.add(key);
                titles.add(map.get(key));
            }
            holder.title.setText(titles.get(position));
            holder.icon.setImageResource(drawables.get(position));
            holder.content.setOnClickListener(v ->
                    onItemClickListener.onClick(titles.get(position)));
        } else {
            //显示已安装app列表的
            AppInfoBean bean = beans.get(position);
            holder.title.setText(bean.getAppName());
            //holder.icon.setImageDrawable(bean.getIcon());
            Glide.with(context).load(bean.getIcon()).into(holder.icon);
            holder.content.setOnClickListener(v ->
                    onItemClickListener.onClick(bean.getAppPackageName()));
        }

    }

    @Override
    public int getItemCount() {
        if (map != null)
            return map.size();
        else
            return beans.size();
    }

    public static class SimpleRecyclerVH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        LinearLayout content;

        public SimpleRecyclerVH(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.item_create_qr_list_icon);
            title = itemView.findViewById(R.id.item_create_qr_list_title);
            content = itemView.findViewById(R.id.item_create_qr_list_content);
        }
    }

    public interface OnItemClickListener {
        void onClick(String ext);
    }
}
