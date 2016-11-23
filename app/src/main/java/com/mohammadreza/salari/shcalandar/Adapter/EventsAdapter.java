package com.mohammadreza.salari.shcalandar.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mohammadreza.salari.shcalandar.Model.MyEvent;
import com.mohammadreza.salari.shcalandar.R;
import com.mohammadreza.salari.shcalandar.Views.MyTextViewBold;

import java.util.List;

/**
 * Created by MohammadReza on 11/23/2016.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private Context mContext;
    private List<MyEvent> MyEventList;

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public MyTextViewBold txtSummary, txtLocation;


        public EventViewHolder(View view) {
            super(view);
            txtSummary = (MyTextViewBold) view.findViewById(R.id.txtSummary);
            txtLocation = (MyTextViewBold) view.findViewById(R.id.txtLocation);

        }
    }


    public EventsAdapter(Context mContext, List<MyEvent> MyEventList) {
        this.mContext = mContext;
        this.MyEventList = MyEventList;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);

        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position) {
        MyEvent MyEvent = MyEventList.get(position);
        holder.txtSummary.setText(MyEvent.getSummary());
        holder.txtLocation.setText(MyEvent.getLocation());

        // loading MyEvent cover using Glide library
        //Glide.with(mContext).load(MyEvent.getThumbnail()).into(holder.thumbnail);
/*
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
        */
    }


    @Override
    public int getItemCount() {
        return MyEventList.size();
    }
}