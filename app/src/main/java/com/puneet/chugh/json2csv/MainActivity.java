package com.puneet.chugh.json2csv;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.puneet.chugh.libjson2csv.MyConverter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "My-Json2Csv";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Inside onCreate() of MainActivity..");

        Button button = findViewById(R.id.button);
        button.setOnClickListener((view)->
            requestPermissionWithOperation());
    }

    public void requestPermissionWithOperation(){
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(!report.areAllPermissionsGranted()){
                            StringBuilder stringBuilder = new StringBuilder();
                            for(PermissionDeniedResponse response: report.getDeniedPermissionResponses()){
                                stringBuilder.append(response.getPermissionName());
                                stringBuilder.append(",");
                            }
                            stringBuilder.deleteCharAt(stringBuilder.length()-1);
                            Snackbar.make(findViewById(R.id.id_main_activity),
                                    getResources().getString(R.string.permission_rationale)+stringBuilder.toString(),
                                    Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                        else {
                            //Converting Json to Csv
                            MyConverter myConverter = new MyConverter();
                            try {
                                File inputFile = new File(Environment.getExternalStorageDirectory() + File.separator + "myjson.json");
                                File outputFile = new File(Environment.getExternalStorageDirectory() + File.separator + "myCsv_ouput.txt");
                                myConverter.json2Csv(inputFile, outputFile);
                            } catch (Exception e) {
                                Log.e(TAG, "IOException : "+e.getMessage());
                            }
                        }
                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        showPermissionRationale(token);
                    }
                }).check();
    }

    public void showPermissionRationale(final PermissionToken token) {
        new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.grant_permissions))
                .setMessage(getResources().getString(R.string.permission_rationale))
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                })
                .show();
    }
}
