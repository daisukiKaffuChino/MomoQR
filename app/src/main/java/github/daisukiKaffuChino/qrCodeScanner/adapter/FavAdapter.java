package github.daisukiKaffuChino.qrCodeScanner.adapter;

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

import java.util.List;

import github.daisukiKaffuChino.qrCodeScanner.R;
import github.daisukiKaffuChino.qrCodeScanner.bean.FavBean;


public class FavAdapter extends RecyclerView.Adapter<FavAdapter.FavViewHolder> {

    private final List<FavBean> listData;
    private final Context context;
    private final OnItemLongClickListener longClickListener;
    private final OnItemClickListener clickListener;

    public FavAdapter(Context context, List<FavBean> list,OnItemClickListener clickListener,OnItemLongClickListener longClickListener) {
        this.context = context;
        this.listData = list;
        this.clickListener=clickListener;
        this.longClickListener = longClickListener;
    }

    static class FavViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, content;
        LinearLayout root;

        public FavViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.fav_item_imageView);
            title = itemView.findViewById(R.id.fav_item_title_textView);
            content = itemView.findViewById(R.id.fav_item_content_textView);
            root=itemView.findViewById(R.id.fav_item_root);
        }
    }

    @NonNull
    @Override
    public FavViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fav, parent, false);
        return new FavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavViewHolder holder, int position) {
        final FavBean s = listData.get(position);
        holder.title.setText(s.getTitle());
        holder.content.setText(s.getContent());
        Glide.with(context).load(s.getImg()).into(holder.image);

        int bindingPosition=holder.getBindingAdapterPosition();
        holder.root.setOnClickListener(view -> clickListener.onClick(bindingPosition));
        holder.root.setOnLongClickListener(view -> {
            longClickListener.onLongClick(bindingPosition);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public interface OnItemLongClickListener {
        void onLongClick(int pos);
    }

    public interface OnItemClickListener{
        void onClick(int pos);
    }

}


