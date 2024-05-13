package com.example.todolist;

import  static android.app.Activity.RESULT_OK;
import static com.example.todolist.MainActivity.GALLERY_REQ_CODE;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class EditTaskDialogFragment extends DialogFragment {


    private MainActivity activity;
    Button cancel;
    Button save;
    ItemInfo itemInfo;

    public static final String BUNDLE_KEY = "bundle_key";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
        if (getArguments() != null) {
            itemInfo = getArguments().getParcelable(BUNDLE_KEY);
            super.onCreate(savedInstanceState);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.edit_dialog, container, false);
        activity = (MainActivity) getActivity();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }


        final EditText editTitle = dialogView.findViewById(R.id.edit_title);

        final TextView dateText = dialogView.findViewById(R.id.date);
        final TextView timeText = dialogView.findViewById(R.id.time);

        cancel = dialogView.findViewById(R.id.button_cancel);
        save = dialogView.findViewById(R.id.button_save);
        ImageView imageView = dialogView.findViewById(R.id.myaddedimage);



    if (itemInfo != null) {
        editTitle.setText(itemInfo.title);
        dateText.setText(itemInfo.date);
        timeText.setText(itemInfo.time);
//
        if(itemInfo.imagePath==null){
            imageView.setImageResource(R.drawable.bgtodo2);
            Toast.makeText(activity, "NO IMAGE ADDED", Toast.LENGTH_SHORT).show();
        }else {
        File file = new File(itemInfo.imagePath);
        if (file.exists()) {
            Glide.with(activity)
                    .load(file)
                    .into(imageView);
        } else {
//            Toast.makeText(activity, "image not found", Toast.LENGTH_SHORT).show();
        }

    }
}

        // Set custom date
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        dateText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM");
                        String dateString = sdf.format(cal.getTime());
                        dateText.setText(dateString);
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Set custom time
        timeText.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                    (view, hourOfDay, minute) -> {
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);

                        SimpleDateFormat sdf = new SimpleDateFormat("hh : mm a");
                        String timeString = sdf.format(cal.getTime());
                        timeText.setText(timeString);
                    }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        });
//          editImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent iGallery = new Intent(Intent.ACTION_PICK);
//                    iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(iGallery, GALLERY_REQ_CODE);
//
//                }
//            });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(); // Dismiss the dialog
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = editTitle.getText().toString();
                final String date = dateText.getText().toString();
                final String time = timeText.getText().toString();

                // Check if title is not empty
                if (title.isEmpty()) {
                    Toast.makeText(activity, "Please enter a title", Toast.LENGTH_SHORT).show();
                    return;
                }


                DatabaseHelper databaseHelper1 = new DatabaseHelper(activity);
                if (itemInfo != null ) {

                    boolean updated ;//= databaseHelper1.updateTask(String.valueOf(itemInfo.id),title,date,time,itemInfo.imagePath);
                    if (itemInfo.imagePath != null && !itemInfo.imagePath.isEmpty()) {
                        // If image is added, update task with image path
                        updated = databaseHelper1.updateTask(String.valueOf(itemInfo.id),title,date,time,itemInfo.imagePath);
                    } else {
                        // If no image is added, update task without image path
                        updated = databaseHelper1.updateTask(String.valueOf(itemInfo.id),title,date,time,"imagepath");
                    }
                    if (updated) {
//                           bundle.getString(title);
//                            bundle.getString(date);
//                            bundle.getString(time);
////
                        // Notify MainActivity about task update
                        activity.populateListView();
                        dismiss(); // Dismiss the dialog
                    } else {
                        Toast.makeText(activity, "Failed to update task", Toast.LENGTH_SHORT).show();
                    }
                } else {
//
                    activity.populateListView();

                    dismiss(); // Dismiss the dialog

                }
                }

//                else {
//                    Toast.makeText(activity, "Database connection error", Toast.LENGTH_SHORT).show();
//                }

        });
//        editImage.setOnClickListener(v -> {
//            Intent iGallery = new Intent(Intent.ACTION_PICK);
//            iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(iGallery, GALLERY_REQ_CODE);
//        });


        return dialogView;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE && data != null) {
//            Uri selectedImageUri = data.getData();
//
//            // Set the selected image URI to the ImageView
//            addImage.setImageURI(selectedImageUri);
//        } else {
//            Toast.makeText(activity, "Failed to select image", Toast.LENGTH_SHORT).show();
//
//        }
//    }
}


// Setter method to set activity reference

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE && data != null) {
//            Uri selectedImageUri = data.getData();
//            // Do something with the selected image URI, such as displaying it in an ImageView
//            // For example:
//
//            addImage.setImageURI(selectedImageUri);
//
//
////            insertDataToDb();
//        } else {
//            Toast.makeText(activity, "Failed to select image", Toast.LENGTH_SHORT).show();
//        }
//    }






