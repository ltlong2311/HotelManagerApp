package com.example.hotelapp.Fragment.manage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotelapp.API.BaseUrl;
import com.example.hotelapp.Activities.CreateStaffAccount;
import com.example.hotelapp.Activities.Home;
import com.example.hotelapp.Activities.InvoiceDetail;
import com.example.hotelapp.Activities.UserEdit;
import com.example.hotelapp.Fragment.listRoom.AddRoomFragment;
import com.example.hotelapp.R;
import com.example.hotelapp.Activities.ServiceEdit;
import com.example.hotelapp.Model.User;
import com.example.hotelapp.Adapters.UserAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


public class ManageFragment extends Fragment {

    BaseUrl baseUrl = new BaseUrl();
    String urlGetDataUser = baseUrl.getBaseURL()+ "/users";

    ListView listViewUser;
    ArrayList<User> userList;
    UserAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_manage, container, false);
        ((Home) getActivity())
                .setActionBarTitle("Quản lý");
        listViewUser = root.findViewById(R.id.listViewUser);
        userList = new ArrayList<>();
        adapter = new UserAdapter(getActivity(), R.layout.user_item, userList);
        listViewUser.setAdapter(adapter);
        getData(urlGetDataUser);

        FloatingActionButton fab = root.findViewById(R.id.add_user);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AddUserFragment addUser = new AddUserFragment();
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.drawer_layout, addUser)
//                        .addToBackStack(null)
//                        .commit();
                Intent intent = new Intent(getActivity(), CreateStaffAccount.class);
                startActivity(intent);
            }
        });


        listViewUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), UserEdit.class);
                intent.putExtra("dataUser", (Serializable) userList.get(position));
                startActivity(intent);
            }
        });
        return root;

    }

    private void getData(String urlGetData) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlGetData, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                userList.add(new User(
                                        data.getInt("ID"),
                                        data.getString("HoTen"),
                                        data.getString("userName"),
                                        data.getString("NgaySinh"),
                                        data.getString("CMT"),
                                        data.getString("DiaChi"),
                                        data.getInt("role")
                                ));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
//                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(request);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.personalInfo) {
            InfoManageFragment frg2 = new InfoManageFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .remove(ManageFragment.this)
                    .add(R.id.drawer_layout, frg2)
                    .addToBackStack(null)
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }
}