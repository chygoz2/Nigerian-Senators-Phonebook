package com.chigozie.nigeriansenatorsphonebook;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.chigozie.nigeriansenatorsphonebook.database.DBHelper;

public class MainActivity extends AppCompatActivity implements ActionInterface {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText search;
    private final int CALL_REQUEST = 100;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.senatorsRecyclerView);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        (new FetchSenatorsTask()).execute();

        search = findViewById(R.id.search);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                (new SearchSenatorsTask(s.toString())).execute();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    public void onPhoneCallClickListener(String phone) {
        this.phoneNumber = phone;
        callPhoneNumber(phone);
    }

    public void onEmailClickListener(String emailAddress, String name) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{emailAddress});
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Dear " + name + ",\r\n\r\n");
        startActivity(emailIntent);
    }

    public void onSmsClickListener(String phone, String text) {
        sendSms(phone, text);
    }

    public void callPhoneNumber(String phoneNumber)
    {
        try
        {
            if (phoneNumber == null) {
                phoneNumber = this.phoneNumber;
            } else {
                this.phoneNumber = phoneNumber;
            }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);
                    return;
                }
            }
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void sendSms(String phoneNumber, String text) {
        try
        {
            Uri uri = Uri.parse("smsto:" + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", text);
            startActivity(intent);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        if(requestCode == CALL_REQUEST)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhoneNumber(this.phoneNumber);
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You need to grant this application Permission to make phone calls");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        }
    }

    private class FetchSenatorsTask extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... voids) {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            Cursor cursor = dbHelper.getAllSenators(dbHelper.getReadableDatabase());
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mAdapter = new MyCustomRecyclerViewAdapter(cursor, MainActivity.this);
            recyclerView.setAdapter(mAdapter);
        }
    }

    private class SearchSenatorsTask extends AsyncTask<Void, Void, Cursor> {
        private String searchText;

        public SearchSenatorsTask (String searchText) {
            this.searchText = searchText;
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            Cursor cursor = dbHelper.search(dbHelper.getReadableDatabase(), searchText);
            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            mAdapter = new MyCustomRecyclerViewAdapter(cursor, MainActivity.this);
            recyclerView.setAdapter(mAdapter);
        }
    }
}
