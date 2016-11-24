package com.mohammadreza.salari.shcalandar.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mohammadreza.salari.shcalandar.Model.MyEvent;
import com.mohammadreza.salari.shcalandar.R;
import com.mohammadreza.salari.shcalandar.Views.MyTextView;
import com.mohammadreza.salari.shcalandar.Views.MyTextViewBold;

import java.util.Collections;
import java.util.List;

/**
 * Created by MohammadReza on 11/23/2016.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private Context mContext;
    private List<MyEvent> MyEventList = Collections.emptyList();

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public MyTextViewBold txtSummary, txtLocation, txtDay;
        MyTextView txtMonthYear;
        LinearLayout lytBase;

        public EventViewHolder(View view) {
            super(view);
            lytBase = (LinearLayout) view.findViewById(R.id.lytBase);
            txtSummary = (MyTextViewBold) view.findViewById(R.id.txtSummary);
            txtLocation = (MyTextViewBold) view.findViewById(R.id.txtLocation);
            txtDay = (MyTextViewBold) view.findViewById(R.id.txtDay);
            txtMonthYear = (MyTextView) view.findViewById(R.id.txtMonthYear);
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
        final MyEvent myEvent = MyEventList.get(position);
        holder.txtSummary.setText(myEvent.getSummary());
        holder.txtLocation.setText(myEvent.getLocation());

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
        try {
            if (myEvent.getStart() == null) {
                holder.txtDay.setText("--");
                holder.txtMonthYear.setText("---");
            } else {

                String tokens[] = myEvent.getStart().split("/");
                int year = Integer.parseInt(tokens[2]);
                int month = Integer.parseInt(tokens[1]);
                int day = Integer.parseInt(tokens[0]);
                holder.txtDay.setText(day + "");
                holder.txtMonthYear.setText(month + " - " + year);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.lytBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, myEvent.getDescription() + "", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return MyEventList.size();
    }


}