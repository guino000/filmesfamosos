package com.example.android.filmesfamosos.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static android.content.Context.MODE_PRIVATE;

public final class FileSystemUtils {
    public static final String IMAGE_DIR = "pictures";

    public static boolean saveBitmapToInternalStorage(Bitmap bitmap, Context context, String imageName){
        File dir = context.getDir(IMAGE_DIR, MODE_PRIVATE);
        File imageFile = new File(dir,imageName);

        try(FileOutputStream fileOutputStream = new FileOutputStream(imageFile)){
            return bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static Bitmap loadImageFromInternalStorage(String imageName){
        try {
            File file = new File(IMAGE_DIR, imageName);
            return BitmapFactory.decodeStream(new FileInputStream(file));
        }catch (FileNotFoundException e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean deleteFileFromInternalStorage(String fileDir, String fileName){
        File file = new File(fileDir, fileName);
        return file.delete();
    }
}
