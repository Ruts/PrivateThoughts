/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.privatethoughts;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.privatethoughts.utilities.JournalColourUtils;
import com.example.android.privatethoughts.utilities.JournalDateUtils;

/**
 * implements and loads the recyler view with data
 */

class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalAdapterViewHolder> {

    private final Context mContext;

    private Cursor mCursor;

    private final JournalAdapterOnClickHandler mClickHandler;

    public interface JournalAdapterOnClickHandler{
        void onClick(long timestamp);
    }

    public JournalAdapter(@NonNull Context context, JournalAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mClickHandler = onClickHandler;
    }

    @Override
    public JournalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.journal_list_item;

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        view.setFocusable(true);

        return new JournalAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String title = mCursor.getString(MainActivity.INDEX_JOURNAL_TITLE);
        StringBuilder subTitle = new StringBuilder();

        if (title.length() > 20){
            subTitle.append(title.substring(0,20));
            subTitle.append("...");
        } else {
            subTitle.append(title);
        }

        holder.titleView.setText(subTitle.toString());

        String content = mCursor.getString(MainActivity.INDEX_JOURNAL_CONTENT);
        StringBuilder subContent = new StringBuilder();

        if (content.length() > 30) {
            subContent.append(content.substring(0,30));
            subContent.append("....");
        } else {
            subContent.append(content);
        }

        holder.contentView.setText(subContent.toString());

        long timestampInMillis = mCursor.getLong(MainActivity.INDEX_JOURNAL_TIMESTAMP);
        String day = JournalDateUtils.getDayString(mContext, timestampInMillis);
        String date = JournalDateUtils.getDateString(mContext, timestampInMillis);
        String time = JournalDateUtils.getTimeString(mContext, timestampInMillis);

        holder.dayView.setText(day);
        holder.dateView.setText(date);
        holder.timeView.setText(time);

        String colour = mCursor.getString(MainActivity.INDEX_JOURNAL_COLOUR);

//        if (colour != null && !(colour.isEmpty())) {
//            holder.constraintLayout.setBackgroundColor(JournalColourUtils.getColourResource(mContext, colour));
//        }
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    class JournalAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView titleView;
        final TextView contentView;
        final TextView dayView;
        final TextView dateView;
        final TextView timeView;

        final ConstraintLayout constraintLayout;

        JournalAdapterViewHolder(View view) {
            super(view);

            titleView = view.findViewById(R.id.textview_title);
            contentView = view.findViewById(R.id.textview_content);
            dayView = view.findViewById(R.id.textview_day);
            dateView = view.findViewById(R.id.textview_date);
            timeView = view.findViewById(R.id.textview_time);

            constraintLayout = view.findViewById(R.id.constraint_layout_view);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long timestampInMillis = mCursor.getLong(MainActivity.INDEX_JOURNAL_TIMESTAMP);
            mClickHandler.onClick(timestampInMillis);
        }
    }
}
