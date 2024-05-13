package com.example.todolist;


import static com.example.todolist.EditTaskDialogFragment.BUNDLE_KEY;
import static java.util.Calendar.MINUTE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.fastadapter.listeners.OnLongClickListener;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.fastadapter_extensions.ActionModeHelper;

//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static final int GALLERY_REQ_CODE = 1000;
    public static final String default_notification_channel_id = "default";
    public static final String TAG = "MainActivity";
    private DatabaseHelper databaseHelper;
    TextView empty;
    ImageView entry;
    SearchView searchView;
    FrameLayout emptyView;
    RecyclerView recyclerView;
    //    CheckBox checkBox;
    ImageView image;

    ImageView imageViewedit;
    ItemAdapter<ItemInfo> itemAdapter;
    private SelectExtension<ItemInfo> selectExtension;
    FastAdapter<ItemInfo> fastAdapter;
    CheckBox checkBox;
    private FloatingActionButton fab;
    private ToggleButton toggleTheme;
    ActionModeHelper actionModeHelper;
    ActionMode actionmode;
    //    ImageView unarchieve;
    ItemInfo itemInfo;
    private SharedPref sharedPreferences;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        entry = findViewById(R.id.entry);
//checkBox = findViewById(R.id.checkbox);
        imageViewedit = findViewById(R.id.editimage);
//        image = findViewById(R.id.addImage);
        empty = findViewById(R.id.emptyTextView);
        emptyView = findViewById(R.id.emptyView);

        sharedPreferences = new SharedPref(this);
        if (sharedPreferences.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }
//        imageViewDelete = findViewById(R.id.delete);
        recyclerView = findViewById(R.id.recycler_view);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);

        recyclerView.setAdapter(fastAdapter);

        fastAdapter.withSelectable(true);
        fastAdapter.withAllowDeselection(true);
        fastAdapter.withSelectOnLongClick(true);
        fastAdapter.withMultiSelect(true);
        fastAdapter.withSelectWithItemUpdate(true);
        selectExtension = fastAdapter.getExtension(SelectExtension.class);
        populateListView();


        fab = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showAddDialog();


            }

        });


        itemAdapter.getItemFilter().withFilterPredicate(new IItemAdapter.Predicate<ItemInfo>() {
            @Override

            public boolean filter(ItemInfo item, CharSequence constraint) {

                if (itemAdapter.getAdapterItems().size() == 0) {
                    return true;
                } else {
                    return item.getTitle().startsWith(String.valueOf(constraint));
                }
            }

        });
        fastAdapter.withOnClickListener(new OnClickListener<ItemInfo>() {
            @Override
            public boolean onClick(View v, IAdapter<ItemInfo> adapter, ItemInfo item, int position) {

                Intent intent = new Intent(MainActivity.this, TaskInfo.class);
                startActivity(intent);
                return true;
            }
        });

        fastAdapter.withOnClickListener(new OnClickListener<ItemInfo>() {
            @Override
            public boolean onClick(View v, @NonNull IAdapter<ItemInfo> itemAdapter, @NonNull ItemInfo item, int position) {

                if (!selectExtension.getSelectedItems().isEmpty()) {

                    if (item.isSelected()) {
                        if (selectExtension.getSelectedItems().size() == 1) {
                            selectExtension.deselect(position);
//                            actionmode.finish();
                            actionmode = null;
                            invalidateOptionsMenu();
                        }
                        selectExtension.deselect(position);
                    } else {
                        selectExtension.select(position);
                    }

                } else {

                }

                return true;
            }
        });
        fastAdapter.withOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v, @NonNull IAdapter adapter, @NonNull IItem item, int position) {
                if (!selectExtension.getSelectedItems().isEmpty()) {
                    invalidateOptionsMenu();

                    return false;
                } else {

                    selectExtension.select(position);
                    actionmode = actionModeHelper.onLongClick(MainActivity.this, position);

//            invalidateOptionsMenu();

                    return true;
                }
            }
        });
        fastAdapter.withEventHook(new ClickEventHook<ItemInfo>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                // Return the view that should trigger the event (in this case, the ImageView)
                imageViewedit = findViewById(R.id.editimage);
                if (viewHolder instanceof ItemInfo.ViewHolder)
                    return ((ItemInfo.ViewHolder) viewHolder).imageViewedit;
                return null;
            }


            @Override
            public void onClick(@NonNull View v, int position, @NonNull FastAdapter<ItemInfo> fastAdapter, @NonNull ItemInfo item) {
                // Handle the click event for the ImageView
                showEditDialog(item);

            }
        });




    }

    public void addItem(ItemInfo item) {
        itemAdapter.add(item);
    }




    private void restartApp() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
        Log.d(TAG, "restartApp: Changed theme successfully");
    }



    ArrayList<ItemInfo> itemsList = new ArrayList<>();

    {
//
        populateListView();
//
        for (ItemInfo myItemInfo : itemsList) {

            addItem(myItemInfo);
//


        }


    }

    public void populateListView() {
        try {
            if (databaseHelper == null) {
                databaseHelper = new DatabaseHelper(this);
            }

            itemsList = databaseHelper.getAllData();
            if (itemAdapter == null) {
                itemAdapter = new ItemAdapter<>();
            }
            itemAdapter.clear();
            itemAdapter.add(itemsList);
            if (itemAdapter.getAdapterItems().size() == 0) {
//            populateListView();
                recyclerView.setVisibility(View.GONE);
                entry.setVisibility(View.VISIBLE);
                empty.setVisibility(View.VISIBLE);
            } else {
                entry.setVisibility(View.GONE);
                empty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }



            fastAdapter.notifyAdapterDataSetChanged();
            Log.d(TAG, "populateListView: Displaying data in list view");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    // to add items
    private void showAddDialog() {
        AddTaskDialog addTaskDialog = new AddTaskDialog();
        addTaskDialog.show(getSupportFragmentManager(), "MyDialogFragment");
    }


    public void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        if (!selectExtension.getSelections().isEmpty()) {

            getMenuInflater().inflate(R.menu.menu_delete, menu);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } else {

            getMenuInflater().inflate(R.menu.menu_front, menu);

//
            MenuItem searchItem = menu.findItem(R.id.action_search);

            if (searchItem != null) {
                SearchView searchView = (SearchView) searchItem.getActionView();
                searchView.setQueryHint("Type here to search");

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        if (itemAdapter.getAdapterItems().size() > 0) {
                            itemAdapter.getItemFilter().filter(newText);
//                            return true;
                        }
                        return true;
                    }
//                        return true;
//                    }
                });

                //                }
//                else {
//                    Toast.makeText(this, "No Tasks Added Yet", Toast.LENGTH_SHORT).show();
//                }
            }
        }
//
//    }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            deleteSelectedItems();

            return true;

        }



        if (id == R.id.action_archieve) {

            archiveTask(selectExtension.getSelectedItems());


            return true;
        }

        if (id == R.id.action_search) {
// added working in searchview
        }
        if (id == R.id.item_arch) {
            Intent intent = new Intent(MainActivity.this, ArchievedActivity.class);
            startActivity(intent);
            populateListView();




        }


        if (id == R.id.item_share) {


            if (!selectExtension.getSelectedItems().isEmpty()) {

                if (selectExtension.getSelectedItems().size() > 1) {

                    Toast.makeText(this, " cant select more than one task to share", Toast.LENGTH_SHORT).show();
                } else {

                    for (ItemInfo itemInfo1 : (selectExtension.getSelectedItems())) {
//                   itemInfo1 = itemAdapter.getItemList().get(i);
//                    boolean currentItem = selectExtension.getSelectedItems().contains(currentItem) ;

                        // Construct the share message
                        String shareText = "Task: " + itemInfo1.getTitle() + "\nDate: " + itemInfo1.getDate() + "\nTime: " + itemInfo1.getTime();
                        // Create an Intent to share the message
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                        // Start activity to show the share dialog
                        startActivity(Intent.createChooser(shareIntent, "Share Task"));
                        selectExtension.deselect();
                    }
                }
                return true;
            } else {
                Toast.makeText(this, "no task added", Toast.LENGTH_SHORT).show();
            }

            return super.onOptionsItemSelected(item);

        }

        return true;
    }

    private void deleteSelectedItems() {
        if (selectExtension.getSelectedItems().isEmpty()) {
            return;
        }

        List<ItemInfo> deleteList = new ArrayList<>(selectExtension.getSelectedItems());

        for (ItemInfo currentItem : deleteList) {
            String title = currentItem.getTitle();
            String date = currentItem.getDate();
            String time = currentItem.getTime();
            String imagePath = currentItem.getImagePath();

            // Delete data from the database
            databaseHelper = new DatabaseHelper(this);
            databaseHelper.deleteData(title, date, time, imagePath);

            // Delete associated image file if imagePath is not null
            if (imagePath != null) {
                File file = new File(imagePath);
                if (file.exists()) {
                    if (file.delete()) {
                        Log.d(TAG, "Deleted image file: " + imagePath);
                    } else {
                        Log.e(TAG, "Failed to delete image file: " + imagePath);
                    }
                }
            }
        }

        populateListView();
        selectExtension.deselect();
        invalidateOptionsMenu();
        Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
    }


    private void archiveTask(Set<ItemInfo> items) {

        if (selectExtension.getSelectedItems().isEmpty()) {
            return;
        }
        Set<ItemInfo> listToArchieve = selectExtension.getSelectedItems();

        for (ItemInfo itemInfo1 : listToArchieve) {

            databaseHelper = new DatabaseHelper(this);
            boolean archeiveSuccess = databaseHelper.setArchiveOrUnArchieveInDb(itemInfo1, 1);
            if (archeiveSuccess) {

//
                Toast.makeText(this, "Added to Archieved List", Toast.LENGTH_SHORT).show();
            }

        }
        selectExtension.deselect();

        populateListView();
        invalidateOptionsMenu();
        fastAdapter.notifyDataSetChanged();
//            Toast.makeText(this, " archieved Succesfully", Toast.LENGTH_SHORT).show();
    }
    // Method to display archived items



    // Method to show edit dialog for existing task
    private void showEditDialog(final ItemInfo itemInfo) {
        Bundle bundle = new Bundle();


        bundle.putParcelable(BUNDLE_KEY,itemInfo);

        EditTaskDialogFragment editTask = new EditTaskDialogFragment();

        // Set the bundle to the dialog fragment
        editTask.setArguments(bundle);


        editTask.show(getSupportFragmentManager(), "MyDialogFragment");

    }

    @Override
    protected void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
        populateListView();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        EventBus.getDefault().unregister(this);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(RefreshHome event) {
//        populateListView();
//    }
//
//    public static class RefreshHome {
//
//    }
}


