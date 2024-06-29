package github.daisukiKaffuChino.MomoQR.ui.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import github.daisukiKaffuChino.MomoQR.R;
import github.daisukiKaffuChino.MomoQR.logic.utils.GlideRoundTransform;

public class ShowImageDialog extends Dialog {

    private ImageView imageView;
    private final String imgPath;
    private final Context context;

    public ShowImageDialog(Context context, String path) {
        super(context, R.style.ShowImageDialog);
        this.context=context;
        this.imgPath = path;
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) setViewContent(imageView, imgPath);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_show_image);

        imageView = findViewById(R.id.dialog_imageView);
        handler.sendEmptyMessage(0);
        Window w = getWindow();
        WindowManager.LayoutParams lp = null;
        if (w != null) {
            lp = w.getAttributes();
            lp.x = 0;
            lp.y = 40;
            w.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        onWindowAttributesChanged(lp);
        imageView.setOnClickListener(view -> dismiss());
    }

    private void setViewContent(ImageView view, String content) {
        if (view == null)
            return;

        Glide.with(context)
                .load(content)
                .transform(new GlideRoundTransform(16,0))
                .into(view);

    }

}
