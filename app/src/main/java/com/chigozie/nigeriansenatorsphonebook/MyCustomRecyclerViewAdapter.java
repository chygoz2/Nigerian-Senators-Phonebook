package com.chigozie.nigeriansenatorsphonebook;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCustomRecyclerViewAdapter extends RecyclerView.Adapter<MyCustomRecyclerViewAdapter.MyViewHolder> {
    private Cursor cursor;
    private DataSetObserver dataSetObserver;
    private Context context;
    private ActionInterface actionInterface;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public MyViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public MyCustomRecyclerViewAdapter(Cursor cursor, Context context, ActionInterface actionInterface) {
        this.cursor = cursor;
        dataSetObserver = new NotifyingDataSetObserver();
        this.context = context;
        this.actionInterface = actionInterface;
        if (cursor != null) {
            cursor.registerDataSetObserver(dataSetObserver);
        }

    }

    @Override
    public MyCustomRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.senator_row, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        View view = holder.view;
        cursor.moveToPosition(position);
        TextView name = view.findViewById(R.id.name);
        TextView email = view.findViewById(R.id.email);
        TextView phone = view.findViewById(R.id.phone);
        TextView state = view.findViewById(R.id.state);

        final String nameText = cursor.getString(1);
        final String phoneNumber = cursor.getString(3);
        final String emailAddress = cursor.getString(2);
        name.setText(nameText);
        email.setText(emailAddress);
        phone.setText(phoneNumber);
        state.setText(cursor.getString(4));

        ImageView callIcon = view.findViewById(R.id.callIcon);
        ImageView emailIcon = view.findViewById(R.id.emailIcon);
        ImageView textIcon = view.findViewById(R.id.textIcon);

        callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phoneNumber.equals("N/A")) {
                    actionInterface.onPhoneCallClickListener(phoneNumber);
                }
            }
        });

        emailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionInterface.onEmailClickListener(emailAddress, nameText);
            }
        });

        textIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionInterface.onSmsClickListener(phoneNumber, nameText);
            }
        });
//        phone.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icons8_android_24, 0, 0, 0);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return null;
        }
        final Cursor oldCursor = cursor;
        if (oldCursor != null && dataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(dataSetObserver);
        }
        cursor = newCursor;
        if (cursor != null) {
            if (dataSetObserver != null) {
                cursor.registerDataSetObserver(dataSetObserver);
            }
            notifyDataSetChanged();
        } else {
            notifyDataSetChanged();
        }
        return oldCursor;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            notifyDataSetChanged();
        }
    }
}