package com.emine.arasinav;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView rehber;
    RadioButton akraba, is, arkadas;
    Button smsEkrani;
    Sqlite sqlite;
    ArrayList<String> isimler, telefonlar;
    ArrayList<Uri> resimler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rehber = findViewById(R.id.rehberlist);
        akraba = findViewById(R.id.akraba);
        is = findViewById(R.id.is);
        arkadas = findViewById(R.id.arkadas);
        smsEkrani = findViewById(R.id.smsEkrani);
        sqlite = new Sqlite(this);
        isimler = new ArrayList<>();
        telefonlar = new ArrayList<>();
        resimler = new ArrayList<>();
        if (checkAndRequestPermissions()) { //
            isimler.clear();
            telefonlar.clear();
            Cursor telefonun_rehberi = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (telefonun_rehberi.moveToNext()) {
                @SuppressLint("Range") String isim = telefonun_rehberi.getString(telefonun_rehberi.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String numara = telefonun_rehberi.getString(telefonun_rehberi.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                @SuppressLint("Range") String photoUri = telefonun_rehberi.getString(telefonun_rehberi.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                isimler.add(isim);
                telefonlar.add(numara);
                if(photoUri!=null )
                resimler.add(Uri.parse(photoUri));
                else
                    resimler.add(Uri.parse("android.resource://"+getApplicationContext().getPackageName()+"/drawable/galeri.png"));
            } // rehberdeki b??t??n isimler ve varsa foto??raflar?? ??ekildi
            telefonun_rehberi.close();
            RehberListAdapter rehberListAdapter = new RehberListAdapter(getApplicationContext(), isimler, telefonlar, resimler);
            rehber.setAdapter(rehberListAdapter);
        }
        rehber.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {// listedeki herhangi bir elemena bas??l?? tutularak gruba ekleme yap??ld??
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SQLiteDatabase sqLiteDatabase= sqlite.getWritableDatabase();
                ContentValues cv= new ContentValues();
                cv.put("k_isim",isimler.get(position));
                cv.put("k_tel",telefonlar.get(position));

                if (akraba.isChecked()) { // akraba se??iliyse ki??iyi akraba grubuna ekler
                    if(KisiGrupKontrol(isimler.get(position),"akraba")) // ki??i grupta varsa mesaj verir
                        Toast.makeText(getApplicationContext(), "Bu isim bu gruba kay??tl??", Toast.LENGTH_SHORT).show();
                    else { // yoksa veritaban??na kay??t ger??ekle??ir
                        cv.put("k_grup", "akraba");
                        sqLiteDatabase.insert("KISILER",null,cv);// veritaban??na insert
                        Toast.makeText(getApplicationContext(), isimler.get(position)+", Akraba grubuna kaydedildi.", Toast.LENGTH_SHORT).show();
                    }
                } else if (is.isChecked()) { // i?? se??iliyse ki??iyi i?? grubuna ekler
                    if(KisiGrupKontrol(isimler.get(position),"is"))
                        Toast.makeText(getApplicationContext(), "Bu isim bu gruba kay??tl??", Toast.LENGTH_SHORT).show();
                    else {
                        cv.put("k_grup", "is");
                        sqLiteDatabase.insert("KISILER",null,cv);
                        Toast.makeText(getApplicationContext(), isimler.get(position)+", ???? grubuna kaydedildi.", Toast.LENGTH_SHORT).show();
                    }
                } else if (arkadas.isChecked()) { // arkada?? se??iliyse ki??iyi arkada?? grubuna ekler
                    if(KisiGrupKontrol(isimler.get(position),"arkadas"))
                        Toast.makeText(getApplicationContext(), "Bu isim bu gruba kay??tl??", Toast.LENGTH_SHORT).show();
                    else {
                        cv.put("k_grup", "arkadas");
                        sqLiteDatabase.insert("KISILER",null,cv); //
                        Toast.makeText(getApplicationContext(), isimler.get(position)+", Arkada?? grubuna kaydedildi.", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getApplicationContext(), "Grup Se??mediniz", Toast.LENGTH_SHORT).show();



                return false;
            }
        });
        akraba.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is.setChecked(false);
                    arkadas.setChecked(false);
                }
            }
        });
        is.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    akraba.setChecked(false);
                    arkadas.setChecked(false);
                }
            }
        });
        arkadas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is.setChecked(false);
                    akraba.setChecked(false);
                }
            }
        });
        smsEkrani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SmsEkrani.class);// sms ekran??na gider
                startActivity(intent);
            }
        });
    }
    private boolean KisiGrupKontrol(String isim,String grup){ // se??ilen ki??i grupta var m?? yok mu kontrol edilir
        SQLiteDatabase sql=sqlite.getReadableDatabase();
        int kontrol=0;
        Cursor cursor= sql.rawQuery("SELECT COUNT(*) FROM KISILER WHERE k_isim='"+isim+"' AND k_grup='"+grup+"'",null);
        if(cursor.moveToFirst())
            do {
                kontrol=cursor.getInt(0);
            }while (cursor.moveToNext());
            cursor.close();
            if(kontrol!=0)
                return true;
            else
                return  false;



    }

    private boolean checkAndRequestPermissions() { //rehbere ula??mak i??in izin al??n??r
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 12);
            return false;
        }
        return true;
    }
}