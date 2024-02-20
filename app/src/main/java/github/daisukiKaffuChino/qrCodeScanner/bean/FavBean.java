package github.daisukiKaffuChino.qrCodeScanner.bean;

import androidx.lifecycle.LiveData;

public class FavBean extends LiveData<FavBean> {
    private String content;
    private String id;
    private String title;
    private String img;
    private long time;

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
        postValue(this);
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String str) {
        this.content = str;
        postValue(this);
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String str) {
        this.img = str;
        postValue(this);
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long i) {
        this.time = i;
        postValue(this);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
        postValue(this);
    }
}
