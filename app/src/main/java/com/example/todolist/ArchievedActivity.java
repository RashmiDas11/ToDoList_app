package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ArchievedActivity extends AppCompatActivity {
    MainActivity mainActivity;

    RecyclerView recyclerView;
    FastAdapter<ItemInfo> fastAdapter;
    TextView emptyArchieved;
    ImageView unarchieve;
    ItemAdapter<ItemInfo> itemAdapter;
    DatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archieved_list);

emptyArchieved = findViewById(R.id.emptyarchieved);
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_view_archieved);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);

        recyclerView.setAdapter(fastAdapter);

        fastAdapter.withEventHook(new ClickEventHook<ItemInfo>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {

                unarchieve = findViewById(R.id.unarchieve);
                if (viewHolder instanceof ItemInfo.ViewHolder)
                    return ((ItemInfo.ViewHolder) viewHolder).unarchieve;
                return null;
            }


            @Override
            public void onClick(@NonNull View v, int position, @NonNull FastAdapter<ItemInfo> fastAdapter, @NonNull ItemInfo item) {

                item.setArchived(false);
                databaseHelper.setArchiveOrUnArchieveInDb(item, 0);
                showArchivedItems();
//                EventBus.getDefault().post(new MainActivity.RefreshHome());


            }
        });


        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Show archived items
        showArchivedItems();
    }


    private void showArchivedItems() {
        databaseHelper = new DatabaseHelper(this);
        List<ItemInfo> archivedItems = databaseHelper.getArchivedItems();
        for (ItemInfo itemInfo : archivedItems) {
            itemInfo.setShowUnArchiveIcon(true);
        }

        if (archivedItems != null && !archivedItems.isEmpty()) {

            recyclerView.setVisibility(View.VISIBLE);
            emptyArchieved.setVisibility(View.GONE);
            itemAdapter.clear();
            itemAdapter.add(archivedItems);
            fastAdapter.notifyAdapterDataSetChanged();


        } else {


            recyclerView.setVisibility(View.GONE);
            emptyArchieved.setVisibility(View.VISIBLE);
        }


    }


}

