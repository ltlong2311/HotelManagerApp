package com.example.hotelapp.Adapters;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hotelapp.API.BaseUrl;
import com.example.hotelapp.Activities.Home;
import com.example.hotelapp.Activities.InvoiceDetail;
import com.example.hotelapp.Activities.ServiceEdit;
import com.example.hotelapp.Activities.UserEdit;
import com.example.hotelapp.LoginActivity;
import com.example.hotelapp.Model.Room;
import com.example.hotelapp.R;
import com.example.hotelapp.Model.User;
import com.example.hotelapp.Secure.ISharedPreference;
import com.example.hotelapp.Secure.SecureSharedPref;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<User> userList;
    BaseUrl baseUrl = new BaseUrl();
    String urlDeleteUser = baseUrl.getBaseURL()+ "/deleteUsers";
    String token;
    int idUser;
    ISharedPreference preferences;

    public UserAdapter(Context context, int layout, List<User> userList) {
        this.context = context;
        this.layout = layout;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private class ViewHolder{
        TextView txtTenUser, txtQuyen;
        ImageView imageAvatar, btnEdit;
    }

    @SuppressLint({"SetTextI18n", "RtlHardcoded", "NonConstantResourceId", "ObsoleteSdkInt"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserAdapter.ViewHolder holder;

        if(convertView == null) {
            holder = new UserAdapter.ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder.txtTenUser = convertView.findViewById(R.id.textViewNameUser);
            holder.txtQuyen = convertView.findViewById(R.id.textViewRole);
            holder.imageAvatar = convertView.findViewById(R.id.imageUser);
            holder.btnEdit = convertView.findViewById(R.id.imageViewUserInfo);
            convertView.setTag(holder);
        } else {
            holder = (UserAdapter.ViewHolder) convertView.getTag();
        }

        User user = userList.get(position);

        idUser = user.getID();

        holder.txtTenUser.setText(user.getHoTen());
        if (user.getRole() == 1){
            holder.imageAvatar.setColorFilter(context.getResources().getColor(R.color.color_user));
            holder.txtQuyen.setText("Nhân viên");

        } else if (user.getRole() == 2) {
            holder.imageAvatar.setImageResource(R.drawable.admin);
            holder.txtQuyen.setText("(Quản lý)");
            holder.btnEdit.setVisibility(View.GONE);
        }


        preferences = new SecureSharedPref(context, LoginActivity.SECRET_TOKEN);
        token = preferences.get("token");

        holder.btnEdit.setOnClickListener(v -> {
            PopupMenu popup = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                popup = new PopupMenu(context, v, Gravity.RIGHT);
            }
            assert popup != null;
            popup.inflate(R.menu.menu_manage_user);
            popup.show();
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                switch (id){
                    case R.id.infoUser:
                        Intent intent = new Intent(context, UserEdit.class);
                        intent.putExtra("dataUser", user);
                        context.startActivity(intent);
                        return true;
                    case R.id.deleteUser:
//                        deleteUser(urlDeleteUser, token, user.getID());
                        confirmDeleteUser(user.getID(), user.getHoTen());
                        return true;
                }
                return true;
            });
        });

        return convertView;
    }
    public void confirmDeleteUser(int idUser, String hoten) {
        AlertDialog.Builder dialogDelInvoice = new AlertDialog.Builder(context);
        dialogDelInvoice.setMessage("Xác nhận xóa nhân viên " + hoten + " ?");
        dialogDelInvoice.setNegativeButton("Có", (dialog, which) -> {
            deleteUser(urlDeleteUser, token, idUser);
        });
        dialogDelInvoice.setPositiveButton("Không", (dialog, which) -> {

        });
        dialogDelInvoice.show();
    }

    private void deleteUser(String urlDeleteUser, String token, int idUser) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest= new StringRequest(Request.Method.POST, urlDeleteUser,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String status = obj.getString("status");
                        String msg = obj.getString("msg");
                        if(status.equals("success")){
                            StyleableToast.makeText(context, "Xóa thành công!", Toast.LENGTH_SHORT, R.style.toastBlueLight).show();
                            Intent intent = new Intent(context, Home.class);
                            intent.putExtra("manage", "manage");
                            context.startActivity(intent);
                        } else {
                            StyleableToast.makeText(context, msg, Toast.LENGTH_SHORT, R.style.toastStyle).show();
                        }
                    } catch (Throwable t) {
                        StyleableToast.makeText(context, "Kiểm tra lại quyền chỉnh sửa!", Toast.LENGTH_SHORT, R.style.toastStyle).show();
                    }
                },
                error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token",token);
                params.put("ID", String.valueOf(idUser));
                return params;
            }

        };
        requestQueue.add(stringRequest);

    }

}
