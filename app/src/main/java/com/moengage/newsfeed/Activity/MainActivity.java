package com.moengage.newsfeed.Activity;

import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.moengage.newsfeed.Adapter.DividerItemDecoration;
import com.moengage.newsfeed.Adapter.NewsAdapter;
import com.moengage.newsfeed.Comparator.CustomComparatorAsc;
import com.moengage.newsfeed.Comparator.CustomComparatorDesc;
import com.moengage.newsfeed.NetworkManager.NetworkChangeReceiver;
import com.moengage.newsfeed.Utils.AndyUtils;
import com.moengage.newsfeed.Constants.AndyConstants;
import com.moengage.newsfeed.Model.News;
import com.moengage.newsfeed.Parsing.ParseContent;
import com.moengage.newsfeed.R;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    //Tag
    private static final String TAG = "MainActivity";
    //Layout
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.mconstraintLayout)
    ConstraintLayout mConstraintLayout;

    private ParseContent mparseContent;
    private List<News> mNewsList;
    LinearLayoutManager mLayoutManager;
    private NewsAdapter mNewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GetIntent();
        ButterKnife.bind(this);
        InitUI();
        fetchData();
        InstantiateFirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            //register broadcast receiver for internet connection and firebase payload when app is in foreground
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            intentFilter.addAction(getString(R.string.msg));
            this.registerReceiver(new NetworkChangeReceiver(), intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //unregister the receiver
        try {
            unregisterReceiver(new NetworkChangeReceiver());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get token for firebase
    private void InstantiateFirebase() {
        if (AndyUtils.isGooglePlayServicesAvailable(MainActivity.this)) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();
                    Log.e("newToken", !TextUtils.isEmpty(newToken) ? newToken : "");

                }
            });
        }
    }

    //Get intent for firebase push notification when app is in background
    private void GetIntent() {
        if (getIntent().getExtras() != null) {
            String pushNotificationMessage = getIntent().getExtras().getString("message");
            if (!TextUtils.isEmpty(pushNotificationMessage)) {
                AndyUtils.showAlertdialog(this, pushNotificationMessage);
                Log.e("pushNotificationMessage", pushNotificationMessage);
            }
        }
    }

    //Initialise UI
    private void InitUI() {
        mparseContent = new ParseContent(this);
        mNewsList = new ArrayList<>();
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.divider_drawable);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(dividerDrawable));
    }

    //fetch data from API
    public void fetchData() {
        try {
            if (mNewsList.size() == 0)
                parseJson();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJson() throws IOException, JSONException {

        if (!AndyUtils.isNetworkAvailable(MainActivity.this)) {
            checkforInternetValidation();
            return;
        }
        //AsyncTask to fetch the data from API
        new AsyncTask<Void, Void, String>() {
            protected String doInBackground(Void[] params) {
                String buffer = httpConnectionCall();
                if (buffer != null) return buffer;
                return null;
            }

            @Override
            protected void onPreExecute() {
                AndyUtils.showSimpleProgressDialog(MainActivity.this);
            }

            protected void onPostExecute(String result) {
                //do something with response
                if (result != null) {
                    Log.d("newwwss", result);
                    onTaskCompleted(result);
                } else {
                    AndyUtils.removeSimpleProgressDialog();  //will remove progress dialog

                }
            }
        }.execute();
    }

    //http connection call
    private String httpConnectionCall() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(AndyConstants.ServiceType.URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
                Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

            }

            return buffer.toString();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //internet validation
    public void checkforInternetValidation() {
        AndyUtils.showSnackbar(mConstraintLayout, AndyConstants.Constants.INTERNETVALIDATION);
    }

    public void onTaskCompleted(String response) {
        if (response != null) {
            Log.d("responsejson", response.toString());
            if (mparseContent.isSuccess(response)) {
                AndyUtils.removeSimpleProgressDialog();  //will remove progress dialog
                mNewsList = mparseContent.getInfo(response);
                mNewsAdapter = new NewsAdapter(mNewsList, () -> {
                    fetchData();
                });

                mRecyclerView.setAdapter(mNewsAdapter);

            } else {
                AndyUtils.removeSimpleProgressDialog();  //will remove progress dialog
                Toast.makeText(MainActivity.this, mparseContent.getErrorCode(response), Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu, this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_menu1:
                if (mNewsList.size() > 0) {
                    AndyUtils.showSimpleProgressDialog(MainActivity.this);
                    //  Sorting in descending order
                    Collections.sort(mNewsList, new CustomComparatorDesc());
                    // notify adapter
                    mNewsAdapter.notifyDataSetChanged();
                    AndyUtils.removeSimpleProgressDialog();  //will remove progress dialog
                }
                return true;
            case R.id.action_menu2:
                if (mNewsList.size() > 0) {
                    AndyUtils.showSimpleProgressDialog(MainActivity.this);
                    //  Sorting in ascending order
                    Collections.sort(mNewsList, new CustomComparatorAsc());
                    // notify adapter
                    mNewsAdapter.notifyDataSetChanged();
                    AndyUtils.removeSimpleProgressDialog();  //will remove progress dialog
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
