package com.example.todolist;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHelper {

    private static final String TAG = "FileHelper";

    // Method to save the image URI to a file in cache directory
    public static String saveImageToCache(Context context, Uri imageUri) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File file = null;

        try {
            inputStream = context.getContentResolver().openInputStream(imageUri);
            File cacheDir = context.getCacheDir();
            file = File.createTempFile("image", ".jpg", cacheDir);
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error saving image to cache directory: " + e.getMessage());
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing streams: " + e.getMessage());
            }
        }

        return file != null ? file.getAbsolutePath() : null;
    }
}
