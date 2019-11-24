package com.chigozie.nigeriansenatorsphonebook;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyCustomRecyclerViewAdapter extends RecyclerView.Adapter<MyCustomRecyclerViewAdapter.MyViewHolder> {
    private Cursor cursor;
    private DataSetObserver dataSetObserver;
    private ActionInterface actionInterface;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public MyViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    public MyCustomRecyclerViewAdapter(Cursor cursor, ActionInterface actionInterface) {
        this.cursor = cursor;
        dataSetObserver = new NotifyingDataSetObserver();
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
        final View view = holder.view;
        cursor.moveToPosition(position);
        TextView name = view.findViewById(R.id.name);
        TextView email = view.findViewById(R.id.email);
        TextView phone = view.findViewById(R.id.phone);
        TextView state = view.findViewById(R.id.state);
        TextView district = view.findViewById(R.id.district);

        final String nameText = cursor.getString(1);
        final String emailAddress = cursor.getString(3);
        final String phoneNumber = cursor.getString(4);
        final String stateText = cursor.getString(5);
        final String districtText = cursor.getString(6) + " Senatorial District";
        name.setText(nameText);
        email.setText(emailAddress);
        phone.setText(phoneNumber);
        state.setText(stateText);
        district.setText(districtText);

        ImageView emailIcon = view.findViewById(R.id.emailIcon);
        ImageView callIcon = view.findViewById(R.id.callIcon);
        ImageView textIcon = view.findViewById(R.id.textIcon);

//        if (phoneNumber.equals("N/A")) {
//            callIcon.setVisibility(View.GONE);
//            textIcon.setVisibility(View.GONE);
//        }

        callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phoneNumber.equals("N/A")) {
                    actionInterface.onPhoneCallClickListener(phoneNumber);
                } else {
                    Toast.makeText(view.getContext(), "Phone number not available", Toast.LENGTH_SHORT).show();
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
                if (phoneNumber.equals("N/A")) {
                    Toast.makeText(view.getContext(), "Phone number not available", Toast.LENGTH_SHORT).show();
                } else {
                    actionInterface.onSmsClickListener(phoneNumber, nameText);
                }
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