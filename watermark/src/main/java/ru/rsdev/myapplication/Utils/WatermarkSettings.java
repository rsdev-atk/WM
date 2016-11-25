package ru.rsdev.myapplication.Utils;

import android.graphics.Bitmap;
import android.net.Uri;

public class WatermarkSettings {

    private static final WatermarkSettings instance = new WatermarkSettings();
    private Uri selectedImageUri;
    private Uri selectedWatermarkUri;
    private float waterBoundsX, waterBoundsY;

    private Bitmap watermarkBitmap = null;
    private Bitmap backImageBitmap = null;

    private int chooseColor;


    public String getId_image() {
        return id_image;
    }

    public void setId_image(String id_image) {
        this.id_image = id_image;
    }

    private String id_image;



    private WatermarkSettings(){
    }

    public static WatermarkSettings getInstance(){
        return instance;
    }


    public Uri getSelectedImageUri() {
        return selectedImageUri;
    }

    public void setSelectedImageUri(Uri selectedImageUri) {
        this.selectedImageUri = selectedImageUri;
    }

    public Uri getSelectedWatermarkUri() {
        return selectedWatermarkUri;
    }

    public void setSelectedWatermarkUri(Uri selectedWatermarkUri) {
        this.selectedWatermarkUri = selectedWatermarkUri;
    }

    public float getWaterBoundsX() {
        return waterBoundsX;
    }

    public void setWaterBoundsX(float waterBoundsX) {
        this.waterBoundsX = waterBoundsX;
    }

    public float getWaterBoundsY() {
        return waterBoundsY;
    }

    public void setWaterBoundsY(float waterBoundsY) {
        this.waterBoundsY = waterBoundsY;
    }


    public Bitmap getWatermarkBitmap() {
        return watermarkBitmap;
    }

    public void setWatermarkBitmap(Bitmap watermarkBitmap) {
        this.watermarkBitmap = watermarkBitmap;
    }

    public Bitmap getBackImageBitmap() {
        return backImageBitmap;
    }

    public void setBackImageBitmap(Bitmap backImageBitmap) {
        this.backImageBitmap = backImageBitmap;
    }

    public int getChooseColor() {
        return chooseColor;
    }

    public void setChooseColor(int chooseColor) {
        this.chooseColor = chooseColor;
    }
}
