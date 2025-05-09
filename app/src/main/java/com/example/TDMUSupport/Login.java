package com.example.TDMUSupport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private RequestQueue requestQueue;
    private Button Submit,backbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        backbtn = findViewById(R.id.backbtn);
        Submit = findViewById(R.id.login_button);
        requestQueue = Volley.newRequestQueue(this);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // Hàm đăng nhập khi người dùng nhấn nút Đăng nhập
    public void login() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Kiểm tra nếu username hoặc password rỗng
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username và Password không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.172.1/login.php"; // Địa chỉ URL của file PHP bạn đã tạo

        // Gửi yêu cầu POST với Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) {
                                // Lấy id_group người dùng từ phản hồi
                                int idGroup = jsonResponse.getInt("id_group");

                                // Đăng nhập thành công
                                Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                                // Chuyển sang màn hình chính và truyền id_group người dùng
                                Intent intent = new Intent(Login.this, task_list.class);
                                intent.putExtra("id_group", idGroup);  // Truyền id_group người dùng
                                startActivity(intent);
                            } else {
                                // Đăng nhập thất bại
                                Toast.makeText(Login.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Login.this, "Lỗi phân tích JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };

        // Thực hiện yêu cầu
        requestQueue.add(stringRequest);
    }
}
