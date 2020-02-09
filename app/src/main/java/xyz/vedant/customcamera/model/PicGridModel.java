package xyz.vedant.customcamera.model;

public class PicGridModel {
    String pic_name, pic_path;
    boolean uploaded;

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public PicGridModel(String pic_name, String pic_path, boolean uploaded) {
        this.pic_name = pic_name;
        this.pic_path = pic_path;
        this.uploaded = uploaded;
    }

    public String getPic_name() {
        return pic_name;
    }

    public void setPic_name(String pic_name) {
        this.pic_name = pic_name;
    }

    public String getPic_path() {
        return pic_path;
    }

    public void setPic_path(String pic_path) {
        this.pic_path = pic_path;
    }
}
