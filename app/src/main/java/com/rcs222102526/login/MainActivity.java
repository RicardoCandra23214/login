package com.rcs222102526.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private static final String BASE_API_URL = "https://stmikpontianak.cloud/011100862/login.php";
    private static final String TAG = "MainActivity"; // Tag untuk Logcat

    private Button loginButton;
    private EditText idEditText;
    private EditText passwordEditText;
    private String userId; // Mengganti _id menjadi userId agar lebih jelas
    private String userPassword; // Mengganti _password menjadi userPassword

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupWindowInsets();
        setupLoginButton();
    }

    private void initializeViews() {
        idEditText = findViewById(R.id.idEditText);
        loginButton = findViewById(R.id.loginButton);
        passwordEditText = findViewById(R.id.passwordEditText);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userId = idEditText.getText().toString();
                userPassword = passwordEditText.getText().toString();

                String loginUrl = BASE_API_URL + "?id=" + userId + "&password=" + userPassword;
                performLoginRequest(loginUrl);
            }
        });
    }

    private void performLoginRequest(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d(TAG, "Raw API Response: " + response);

                try {
                    response = response.trim();
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String idCount = jsonObject.getString("idCount");

                    Log.d(TAG, "Parsed idCount: " + idCount);

                    if ("1".equals(idCount)) { // Menggunakan "1".equals(idCount) untuk keamanan null-pointer
                        showToast("Selamat datang, " + userId);
                        navigateToMenuActivity();
                    } else {
                        showToast("ID dan password anda tidak cocok.");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON: " + e.getMessage(), e);
                    showToast("Error saat memproses data: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String errorMessage = (responseBody != null) ? new String(responseBody) : "Tidak ada badan respons";
                Log.e(TAG, "HTTP Failure. Status: " + statusCode + ", Error: " + error.getMessage() + ", Response: " + errorMessage, error);
                showToast("Koneksi Gagal: " + (error.getMessage() != null ? error.getMessage() : "Kesalahan tidak diketahui"));
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToMenuActivity() {
        Intent menuIntent = new Intent(getApplicationContext(), MenuActivity.class);
        startActivity(menuIntent);
    }
}