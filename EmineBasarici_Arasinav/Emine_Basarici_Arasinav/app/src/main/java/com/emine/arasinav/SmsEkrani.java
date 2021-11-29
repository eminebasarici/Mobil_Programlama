package com.emine.arasinav;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SmsEkrani extends AppCompatActivity {
    ListView rehber;
    RadioButton akraba, is, arkadas;
    Button gonder;
    Sqlite sqlite;
    ArrayList<String> isimler, telefonlar;
    EditText baslik, sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_ekrani);
        rehber = findViewById(R.id.list);
        akraba = findViewById(R.id.akraba);
        is = findViewById(R.id.is);
        arkadas = findViewById(R.id.arkadas);
        gonder = findViewById(R.id.gonder);
        baslik = findViewById(R.id.baslik);
        sms = findViewById(R.id.sms);
        sqlite = new Sqlite(this);
        isimler = new ArrayList<>();
        telefonlar = new ArrayList<>();
        akraba.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {  // eğer bu radio buton seçiliyse diğerleri iptal edilir ve listeye bu gruptaki elemanlar getirirlr.
                    is.setChecked(false);
                    arkadas.setChecked(false);
                    IsimleriGetir("akraba");

                }
            }
        });
        is.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    akraba.setChecked(false);
                    arkadas.setChecked(false);
                    IsimleriGetir("is");
                }
            }
        });
        arkadas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is.setChecked(false);
                    akraba.setChecked(false);
                    IsimleriGetir("arkadas");
                }
            }
        });
        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(isimler.size()>0)
                    {
                        if(sms.getText().toString().trim().isEmpty())
                            Toast.makeText(getApplicationContext(), "Lütfen Mesaj Giriniz", Toast.LENGTH_SHORT).show();
                        else{
                            if(checkAndRequestPermissions())
                            {
                                SmsManager smsManager = SmsManager.getDefault(); // smsler seçilen gruptaki elemanlara döngü ile gönderildi.
                                for(int i=0;i<telefonlar.size();i++)
                                smsManager.sendTextMessage(telefonlar.get(i), null, sms.getText().toString(), null, null);
                                Toast.makeText(getApplicationContext(), "Tüm Smsler Gönderildi", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Grup Seçmediniz", Toast.LENGTH_SHORT).show();
                    }
            }
        });

    }
    private boolean checkAndRequestPermissions() {// sms gönderme izni alndı
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 12);
            return false;
        }
        return true;
    }
    private void IsimleriGetir(String grup) {// seçilen gruptaki kişiler listeye dolduruldu
        isimler.clear();
        telefonlar.clear();
        SQLiteDatabase sqLiteDatabase = sqlite.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT k_isim,k_tel FROM KISILER WHERE k_grup='" + grup + "'", null);
        if (cursor.moveToFirst())
            do {
                isimler.add(cursor.getString(0));
                telefonlar.add(cursor.getString(1));
            } while (cursor.moveToNext());
        cursor.close();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, isimler);
        rehber.setAdapter(arrayAdapter);
    }
}