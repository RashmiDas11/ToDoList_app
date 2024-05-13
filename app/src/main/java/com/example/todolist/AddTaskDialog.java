package com.example.todolist;

import static android.app.Activity.RESULT_OK;
import static com.example.todolist.MainActivity.GALLERY_REQ_CODE;
import static java.util.Calendar.MINUTE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddTaskDialog extends DialogFragment {
    MainActivity activity;
    String selectedImagePath;
    ImageView imageViewicon;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        activity = (MainActivity) getActivity();//here we know that dialog created in mainActivity(Parent)


        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);

        final EditText editTitle = dialogView.findViewById(R.id.edit_title);
        final TextView dateText = dialogView.findViewById(R.id.date);
        final TextView timeText = dialogView.findViewById(R.id.time);
        imageViewicon = dialogView.findViewById(R.id.addmyimage);
        LinearLayout addImageLayout = dialogView.findViewById(R.id.imagelayout);

        final long date = System.currentTimeMillis();
        SimpleDateFormat dateSdf = new SimpleDateFormat("d MMMM");
        String dateString = dateSdf.format(date);
        dateText.setText(dateString);

        //Set current time as default time
        SimpleDateFormat timeSdf = new SimpleDateFormat("hh : mm a");
        String timeString = timeSdf.format(date);
        timeText.setText(timeString);

        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());

        //Set custom date
        dateText.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                String newMonth = getMonth(monthOfYear + 1);
                                dateText.setText(dayOfMonth + " " + newMonth);
                                cal.set(Calendar.YEAR, year);
                                cal.set(Calendar.MONTH, monthOfYear);
                                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                Log.d("TAG", "onDateSet: Date has been set successfully");
                            }
                        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                datePickerDialog.getDatePicker().setMinDate(date);
            }
        });


        //Set custom time
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(activity,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String time;
                                String minTime = String.format("%02d", minute); //prefix zero to minute if its single digit
                                //Method to postfix AM/PM
                                if (hourOfDay >= 0 && hourOfDay < 12) {
                                    time = hourOfDay + " : " + minTime + " AM";
                                } else {
                                    if (hourOfDay != 12) {
                                        hourOfDay = hourOfDay - 12;
                                    }
                                    time = hourOfDay + " : " + minTime + " PM";
                                }

                                timeText.setText(time);
                                cal.set(Calendar.HOUR, hourOfDay);
                                cal.set(Calendar.MINUTE, minute);
                                cal.set(Calendar.SECOND, 0);
                                Log.d("TAG", "onTimeSet: Time has been set successfully");
                            }
                        }, cal.get(Calendar.HOUR), cal.get(MINUTE), false);
                timePickerDialog.show();
            }
        });
        addImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iGallery = new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery, GALLERY_REQ_CODE);

            }
        });


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity)
                .setView(dialogView)
                .setCancelable(false)
                .setTitle("Lets add new task!")
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String title = editTitle.getText().toString();
                        String date = dateText.getText().toString();
                        String time = timeText.getText().toString();

                        if (title.length() != 0) {

                            try {

                                insertDataToDb(title, date, time, selectedImagePath);
//
                                activity.populateListView();
//
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            activity.toastMsg(" Cannot set an empty ToDo!!!");
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //pass
                        dialog.cancel();
                    }
                });


        return dialogBuilder.create();
    }


    private void insertDataToDb(String title, String date, String time, String imagePath) {

        DatabaseHelper databaseHelper = new DatabaseHelper(activity);

//= databaseHelper.insertData(title, date, time, imagePath);
        boolean insertData ;
        if (imagePath == null || imagePath.isEmpty()) {
            // If no image is added, insert data with null imagePath
            insertData = databaseHelper.insertData(title, date, time, "imagepath");
        } else {
            // If image is added, insert data with imagePath
            insertData = databaseHelper.insertData(title, date, time, imagePath);
        }

        if (insertData) {
            try {
                activity.populateListView();
                activity.toastMsg("Added successfully!");
                Log.d("TAG", "insertDataToDb: Inserted data into database");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            activity.toastMsg("Something went wrong");
    }

    private String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE && data != null) {
            Uri selectedImageUri = data.getData();
            // Do something with the selected image URI, such as displaying it in an ImageView
//            // For example:
//            selectedImageUri.getPath();
            imageViewicon.setImageURI(selectedImageUri);
            String imagePath = FileHelper.saveImageToCache(activity, selectedImageUri);
            if (imagePath != null) {
                selectedImagePath = imagePath;
            }
            // Now you have the path of the saved image file, you can store it in your database
            // Call your database helper method to store the imagePath in the database

            else {
                Toast.makeText(activity, "Failed to select image", Toast.LENGTH_SHORT).show();
            }
        }
//            editTaskDialogFragment.show(getSupportFragmentManager(), "EditTaskDialogFragment");

    }
}
//
