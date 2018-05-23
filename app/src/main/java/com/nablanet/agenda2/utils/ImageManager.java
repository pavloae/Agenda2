package com.nablanet.agenda2.utils;

import android.graphics.Bitmap;

import com.nablanet.agenda2.interfaces.ImageManagerInterface;

public class ImageManager {

    public static void uploadImage(ImageManagerInterface imageManagerInterface, int code, Bitmap bitmap) {

        if (imageManagerInterface != null)
            imageManagerInterface.onCompleteOperation(code, true);

    }



}
