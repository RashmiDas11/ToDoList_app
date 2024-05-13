package com.example.todolist;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.TaskStackBuilder;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItem;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.listeners.OnClickListener;
import com.mikepenz.materialize.color.Material;

import java.util.ArrayList;
import java.util.List;

public class ItemInfo extends AbstractItem<ItemInfo, ItemInfo.ViewHolder> implements Parcelable {

    public String title;
    public int id;
    public boolean archived;
    private boolean showUnArchiveIcon = false;
    public String date;
    public String time;
    public String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    protected ItemInfo(Parcel in) {
        title = in.readString();
        id = in.readInt();
        archived = in.readByte() != 0;
        showUnArchiveIcon = in.readByte() != 0;
        date = in.readString();
        time = in.readString();
        imagePath = in.readString();
    }

    public static final Creator<ItemInfo> CREATOR = new Creator<ItemInfo>() {
        @Override
        public ItemInfo createFromParcel(Parcel in) {
            return new ItemInfo(in);
        }

        @Override
        public ItemInfo[] newArray(int size) {
            return new ItemInfo[size];
        }
    };

    public boolean isShowUnArchiveIcon() {
        return showUnArchiveIcon;
    }

    public void setShowUnArchiveIcon(boolean showUnArchiveIcon) {
        this.showUnArchiveIcon = showUnArchiveIcon;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    FastAdapter<ItemInfo> fastAdapter;

    public void setId(int id) {
        this.id = id;
    }

    public ItemInfo(int id, String title, String date, String time, String imagePath) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.imagePath = imagePath;
        this.archived = false;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    // Method to check if the item is archived
    public boolean isArchived() {
        return archived;
    }


    public void archiveTask() {
        setArchived(true);
    }


    public void unarchiveTask() {
        setArchived(false);
    }




    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {

        return new ViewHolder(v);
    }


    @Override
    public int getType() {
        return R.id.item_parent;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.row_item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

        dest.writeString(title);
        dest.writeInt(id);
        dest.writeByte((byte) (archived ? 1 : 0));
        dest.writeByte((byte) (showUnArchiveIcon ? 1 : 0));
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(imagePath);
    }


    public class ViewHolder extends FastAdapter.ViewHolder<ItemInfo> {


        TextView dateTitle, titleText, timeTitle;

        ImageView imageViewedit, unarchieve, addImage;


        FastAdapter<ItemInfo> fastAdapter;

        public ViewHolder(View itemView) {
            super(itemView);
            addImage = itemView.findViewById(R.id.item_image);
            imageViewedit = itemView.findViewById(R.id.editimage);
            dateTitle = itemView.findViewById(R.id.dateTitle);
            titleText = itemView.findViewById(R.id.heading);
            timeTitle = itemView.findViewById(R.id.timeTitle);
            unarchieve = itemView.findViewById(R.id.unarchieve);

        }


        @SuppressLint("ResourceAsColor")
        @Override
        public void bindView(ItemInfo item, List<Object> payloads) {
            if (isShowUnArchiveIcon()) {
                unarchieve.setVisibility(View.VISIBLE);
                imageViewedit.setVisibility(View.GONE);
            } else {
                unarchieve.setVisibility(View.GONE);
                imageViewedit.setVisibility(View.VISIBLE);
            }
            titleText.setText(item.title);
            dateTitle.setText(item.date);
            timeTitle.setText(item.time);
            if ((item.isSelected())) {
                titleText.setTextColor(Color.YELLOW);
                dateTitle.setTextColor(Color.YELLOW);
                timeTitle.setTextColor(Color.YELLOW);
            } else {
                titleText.setTextColor(Color.WHITE);
                dateTitle.setTextColor(Color.WHITE);
                timeTitle.setTextColor(Color.WHITE);

            }
            if (item.imagePath != null && !item.imagePath.isEmpty()) {

                Glide.with(itemView.getContext())
                        .load(item.imagePath)
                        .into(addImage);
            } else {
               addImage.setVisibility(View.GONE);
                addImage.setImageDrawable(null);
            }
        }




        @Override
        public void unbindView(ItemInfo item) {

        }


    }
}




