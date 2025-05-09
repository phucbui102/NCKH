package com.example.TDMUSupport;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.CaptureActivity;

public class CaptureActivityPortrait extends CaptureActivity {

    private static final int REQUEST_PERMISSION_CODE = 100;
    private static final int BARCODE_SCAN_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra quyền đọc bộ nhớ
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        }
    }

    // Xử lý kết quả quét mã vạch
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BARCODE_SCAN_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String barcodeValue = data.getStringExtra("SCAN_RESULT");

            Toast.makeText(this, "Mã quét được: " + barcodeValue, Toast.LENGTH_LONG).show();

            // Gửi lại kết quả về Notify (hoặc màn hình gọi activity này)
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SCAN_RESULT", barcodeValue);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    // Xử lý cấp quyền truy cập ảnh
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền truy cập ảnh", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
