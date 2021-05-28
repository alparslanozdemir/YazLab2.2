package com.yazlab.mobilsorgu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference dataRef = db.collection("taksidata");
    Query query = dataRef.orderBy("trip_distance", Query.Direction.DESCENDING).limit(5);
    public static TextView textView;
    public static TextView textView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button enuzunmesafe = findViewById(R.id.enuzunmesafe);
        Button enkisamesafe = findViewById(R.id.aracsayisi);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        //textView.setText(getTimestamp("12/12/2020"));
        textView.setMovementMethod(new ScrollingMovementMethod());
        enuzunmesafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorgu1(v);
            }
        });
        enkisamesafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorgu2(v);
            }
        });
    }
    public void sorgu1(View v){
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String sonuc = "";
                            int x =1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                sonuc +="En uzun "+x+". yolculuk tarih: " + getDate(Long.parseLong(document.getData().get("tpep_pickup_datetime").toString())) + "\n";
                                sonuc +="En uzun "+x+". yolculuk Mesafe: " + document.getData().get("trip_distance").toString() + "\n\n";
                                x++;
                            }
                            textView.setText(sonuc);
                        } else {
                            Log.d("Document", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void sorgu2(View v){
        EditText lokasyonid = findViewById(R.id.editTextLokasyon);
        String lokasyon = lokasyonid.getText().toString();
        EditText baslangictarihi = findViewById(R.id.editTextDate);
        String baslangic = baslangictarihi.getText().toString();
        EditText bitistarihi = findViewById(R.id.editTextDate2);
        String bitis = bitistarihi.getText().toString();
        Query query2 = dataRef.orderBy("tpep_pickup_datetime", Query.Direction.ASCENDING).whereGreaterThan("tpep_pickup_datetime", Long.parseLong(getTimestamp(baslangic))).whereLessThan("tpep_pickup_datetime", Long.parseLong(getTimestamp(bitis)));
        query2.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String sonuc = "";
                            int x =0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(lokasyon.equals(document.getData().get("PULocationID").toString())){
                                    x++;
                                }
                                //sonuc +="En kısa "+x+". yolculuk tarih: " + getDate(Long.parseLong(document.getData().get("tpep_pickup_datetime").toString())) + "\n";
                                //sonuc +="En kısa "+x+". yolculuk Mesafe: " + document.getData().get("trip_distance").toString() + "\n\n";
                            }
                            sonuc +="Belirlenen tarihler arasında "+lokasyon+" ID li lokasyondan hareket eden araç sayısı:" + x + "\n";
                            textView2.setText(sonuc);
                        } else {
                            Log.d("Document", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    private String getDate(long time) {
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+3"));

        return sdf.format(date);
    }
    private String getTimestamp(String tarih){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = dateFormat.parse(tarih);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp zamandamgasi = new Timestamp(date);

        return String.valueOf(zamandamgasi.getSeconds());
    }
    public void harita_fonk(View v){
        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
    }
}