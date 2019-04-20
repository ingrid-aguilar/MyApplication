package com.example.myapplication;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ContactsAdapter.ContactsAdapterListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Contact> contactList;
    private ContactsAdapter mAdapter;
    private SearchView searchView;

    private static final String URL ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       getSupportActionBar().setDisplayShowHomeEnabled(true);
       getSupportActionBar().setTitle(R.string.toolbar_name);

       recyclerView = findViewById(R.id.recycler_view);
       contactList = new ArrayList<>();
       mAdapter =  new ContactsAdapter(this, contactList, this);

       whitheNotificationsBar(recyclerView);

       RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
       recyclerView.setItemAnimator(new DefaultItemAnimator());
       recyclerView.addItemDecoration(new MyDividetItemDecoration(this, DividerItemDecoration.VERTICAL, 36));
       recyclerView.setAdapter(mAdapter);

       fetchContacts();
    }

    private void fetchContacts(){
        JsonArrayRequest request = new JsonArrayRequest(URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response == null) {
                            Toast.makeText(getApplicationContext(), "No se pudo recuperar los contactos! Por favor intente de nuevo", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<Contact> items = new Gson().fromJson(response.toString(), new TypeToken<List<Contact>>() {
                        }.getType());

                        contactList.clear();
                        contactList.addAll(items);
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"Error" + error.getMessage());
                Toast.makeText(getApplicationContext(), "Error" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        MyApplication.getInstance().addToRequestQueue(request);
    }

    public boolean onCreateOptionsMenu(Menu menu){
       getMenuInflater().inflate(R.menu.menu_main, menu);

       SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
       searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
       searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
       searchView.setMaxWidth(Integer.MAX_VALUE);

       searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String query) {
               mAdapter.getFilter().filter(query);
               return false;
           }

           @Override
           public boolean onQueryTextChange(String query) {
               mAdapter.getFilter().filter(query);
               return false;
           }
       });
       return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_search){
            return true;
        }
        return  super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        if (!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whitheNotificationsBar (View view){
        if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M){
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    public void onContactSelected(Contact contact){
        Toast.makeText(getApplicationContext(), "Selected: " + contact.getName() + "" + contact.getPhone(), Toast.LENGTH_LONG).show();
    }
}
