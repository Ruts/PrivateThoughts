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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.privatethoughts.utilities.JournalColourUtils;
import com.example.android.privatethoughts.utilities.JournalDateUtils;

/**
 * Implements and loads the recyler view with data
 */
class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalAdapterViewHolder> {

    private final Context mContext;

    private Cursor mCursor;

    private final JournalAdapterOnClickHandler mClickHandler;

    /**
     * Creates an interface to define the onclick parameters
     */
    public interface JournalAdapterOnClickHandler{
        void onClick(long timestamp, String password);
    }

    /**
     * Initializes the journal adapter
     * @param context           of the app
     * @param onClickHandler    defined teh onclock function
     */
    public JournalAdapter(@NonNull Context context, JournalAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mClickHandler = onClickHandler;
    }

    /**
     * Defined the view holder for the recycler
     * @param parent    view parent
     * @param viewType  type of view
     * @return initialized view holder
     */
    @Override
    public JournalAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = R.layout.journal_list_item;

        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        view.setFocusable(true);

        return new JournalAdapterViewHolder(view);
    }

    /**
     * Sets the view with the proper values
     * @param holder    the view holder
     * @param position  the position of the view
     */
    @Override
    public void onBindViewHolder(@NonNull JournalAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        String title = mCursor.getString(MainActivity.INDEX_JOURNAL_TITLE);
        StringBuilder subTitle = new StringBuilder();

        if (title.length() > 30){
            subTitle.append(title.substring(0,30));
            subTitle.append("...");
        } else {
            subTitle.append(title);
        }

        holder.titleView.setText(subTitle.toString());

        long timestampInMillis = mCursor.getLong(MainActivity.INDEX_JOURNAL_TIMESTAMP);
        String day = JournalDateUtils.getDayString(timestampInMillis);
        String date = JournalDateUtils.getDateString(timestampInMillis);
        String time = JournalDateUtils.getTimeString(timestampInMillis);

        holder.dayView.setText(day);
        holder.dateView.setText(date);
        holder.timeView.setText(time);

        String colour = mCursor.getString(MainActivity.INDEX_JOURNAL_COLOUR);
        if (colour != null && !(colour.isEmpty())) {
            holder.constraintLayout.setBackgroundColor(JournalColourUtils.getColourResource(mContext, colour));
        }

        String password = mCursor.getString(MainActivity.INDEX_JOURNAL_PASSWORD);
        if (password != null && !(password.isEmpty())) {
            holder.lockView.setVisibility(View.VISIBLE);
        } else {
            holder.lockView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Gets number of items in the recyler view
     * @return number of items
     */
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    /**
     * Swaps old cursor with new cursor
     * @param newCursor the new cursor
     */
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * Inner class to implement the view holder
     */
    class JournalAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView titleView;
        final TextView dayView;
        final TextView dateView;
        final TextView timeView;
        final ImageView lockView;
        final ConstraintLayout constraintLayout;

        /**
         * Sets the values of the view
         * @param view the view
         */
        JournalAdapterViewHolder(View view) {
            super(view);

            titleView = view.findViewById(R.id.textview_title);
            dayView = view.findViewById(R.id.textview_day);
            dateView = view.findViewById(R.id.textview_date);
            timeView = view.findViewById(R.id.textview_time);
            lockView = view.findViewById(R.id.imgview_lock);
            constraintLayout = view.findViewById(R.id.constraint_layout_view);

            view.setOnClickListener(this);
        }

        /**
         * Defines the action to be performed when entry clicked
         * @param v the view tat was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long timestampInMillis = mCursor.getLong(MainActivity.INDEX_JOURNAL_TIMESTAMP);
            String password = mCursor.getString(MainActivity.INDEX_JOURNAL_PASSWORD);
            mClickHandler.onClick(timestampInMillis, password);
        }
    }
}
