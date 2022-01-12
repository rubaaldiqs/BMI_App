package com.example.bmiProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nmg.bmi_app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class activityHome extends AppCompatActivity {
    Button newRecord, viewFood, addFood;
    TextView logout, status, name, load;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId, stuts, gender,userWeight, userLenght, birthday;
    RecyclerView recyclerView;
    com.example.bmiProject.listRecordAdapter listRecordAdapter;
    List<Records> recordsList;
    double ageparcent = 0,w,l,bmiPercent, bmiChange, bmiLast, bmiTowLast;
    Calendar calendar;
    int age, year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        newRecord = findViewById(R.id.newRecord);
        logout = findViewById(R.id.logout);
        recyclerView = findViewById(R.id.recyclerViews);

        viewFood = findViewById(R.id.viewFood);
        addFood = findViewById(R.id.addFood);
        status = findViewById(R.id.status);
        name = findViewById(R.id.name);
        recordsList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getUid();
        calendar = Calendar.getInstance();

        showData();

        viewFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activityListFood.class);
                startActivity(intent);
            }
        });
        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activityAddFood.class);
                startActivity(intent);
            }
        });
        newRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activityNewRecord.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        firebaseFirestore.collection("User").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String name = task.getResult().get("userName").toString();
                            gender = task.getResult().get("gender").toString();
                            userWeight = task.getResult().get("weight").toString();
                            userLenght = task.getResult().get("lenght").toString();
                            birthday = task.getResult().get("birthday").toString();

                            activityHome.this.name.setText(name);
                        }
                    }
                });

    }


    private void showData() {
        firebaseFirestore.collection("Records").whereEqualTo("uId", userId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String dateRecords = document.getData().get("dateRecords").toString();
                                String lengthRecords = document.getData().get("lengthRecords").toString();
                                String weightRecords = document.getData().get("weightRecords").toString();

                                year = Integer.parseInt(dateRecords.substring(7));
                                age = calendar.get(Calendar.YEAR) - year;
                                w = Double.parseDouble(weightRecords);
                                l = Double.parseDouble(lengthRecords) / 100 * Double.parseDouble(lengthRecords) / 100;

                                if (age >= 20) {
                                    ageparcent = 1;
                                    bmiPercent = (w / l) * ageparcent;
                                } else if ((age >= 10 && age <= 19) && gender.equals("Male")) {
                                    ageparcent = 0.90;
                                    bmiPercent = (w / l) * ageparcent;
                                } else if ((age >= 10 && age <= 19) && gender.equals("Female")) {
                                    ageparcent = 0.80;
                                    bmiPercent = (w / l) * ageparcent;
                                } else if (age >= 2 && age <= 10) {
                                    ageparcent = 0.7;
                                    bmiPercent = (w / l) * ageparcent;
                                }

                                if (bmiPercent > 30) {
                                    stuts = "Obesity";
                                } else if (bmiPercent <= 30 && bmiPercent > 25) {
                                    stuts = "Overweight";
                                } else if (bmiPercent <= 25 && bmiPercent > 18.5) {
                                    stuts = "Healthy Weight";
                                } else if (bmiPercent <= 18.5) {
                                    stuts = "Underweight";
                                }
                                recordsList.add(new Records(dateRecords, "", lengthRecords, weightRecords, userId, stuts, bmiPercent));
                            }
                            Collections.reverse(recordsList);
                            if (recordsList.size() < 1) {
                                load.setVisibility(View.VISIBLE);
                                year = Integer.parseInt(birthday.substring(7));
                                age = calendar.get(Calendar.YEAR) - year;
                                w = Double.parseDouble(userWeight);
                                l = Double.parseDouble(userLenght) / 100 * Double.parseDouble(userLenght) / 100;
                                if (age >= 20) {
                                    ageparcent = 1;
                                    bmiPercent = (w / l) * ageparcent;
                                } else if ((age >= 10 && age <= 19) && gender.equals("Male")) {
                                    ageparcent = 0.90;
                                    bmiPercent = (w / l) * ageparcent;
                                } else if ((age >= 10 && age <= 19) && gender.equals("Female")) {
                                    ageparcent = 0.80;
                                    bmiPercent = (w / l) * ageparcent;
                                } else if (age >= 2 && age <= 10) {
                                    ageparcent = 0.7;
                                    bmiPercent = (w / l) * ageparcent;
                                }

                                if (bmiPercent > 30) {
                                    stuts = "Obesity";
                                } else if (bmiPercent <= 30 && bmiPercent > 25) {
                                    stuts = "Overweight";
                                } else if (bmiPercent <= 25 && bmiPercent > 18.5) {
                                    stuts = "Healthy Weight";

                                } else if (bmiPercent <= 18.5) {
                                    stuts = "Underweight";
                                }

                                status.setText(stuts);

                            } else if (recordsList.size() == 1) {
                                load.setVisibility(View.INVISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);

                                bmiLast = recordsList.get(0).getBmiForRecords();
                                year = Integer.parseInt(birthday.substring(7).toString());
                                age = calendar.get(Calendar.YEAR) - year;
                                w = Double.parseDouble(userWeight);
                                l = Double.parseDouble(userLenght) / 100 * Double.parseDouble(userLenght) / 100;
                                if (age >= 20) {
                                    ageparcent = 1;
                                    bmiPercent = (w / l) * ageparcent;
                                } else if ((age >= 10 && age <= 19) && gender.equals("Male")) {
                                    ageparcent = 0.90;
                                    bmiPercent = (w / l) * ageparcent;
                                } else if ((age >= 10 && age <= 19) && gender.equals("Female")) {
                                    ageparcent = 0.80;
                                    bmiPercent = (w / l) * ageparcent;
                                } else if (age >= 2 && age <= 10) {
                                    ageparcent = 0.7;
                                    bmiPercent = (w / l) * ageparcent;
                                }

                                bmiChange = bmiLast - bmiPercent;
                                String stuts = recordsList.get(0).getStutesRecords();

                                switch (stuts) {
                                    case "Obesity":
                                        if ((bmiChange < -1) || (bmiChange >= -1 && bmiChange < -0.6)) {
                                            status.setText(stuts + " Go Ahead");
                                        } else if ((bmiChange < -0.3 && bmiChange >= -0.6) || (bmiChange >= -0.3 && bmiChange > 0)) {
                                            status.setText(stuts + " Little Changes");
                                        } else if (bmiChange >= 0 && bmiChange < 0.3) {
                                            status.setText(stuts + " Be Careful");
                                        } else if ((bmiChange >= 0.3 && bmiChange < 0.6) || (bmiChange >= 0.6 && bmiChange < 1) || bmiChange >= 1) {
                                            status.setText(stuts + " So Bad");
                                        }
                                        break;

                                    case "Overweight":
                                        if ((bmiChange < -1) || (bmiChange >= 0.3 && bmiChange < 0.6)) {
                                            status.setText(stuts + " Be Careful");
                                        } else if ((bmiChange >= -1 && bmiChange < -0.6)) {
                                            status.setText(stuts + " Go Ahead");
                                        } else if ((bmiChange < -0.3 && bmiChange >= -0.6)) {
                                            status.setText(stuts + " still good");
                                        } else if ((bmiChange >= -0.3 && bmiChange < 0) || (bmiChange >= 0 && bmiChange < 0.3)) {
                                            status.setText(stuts + " Little Changes");
                                        } else if ((bmiChange >= 0.6 && bmiChange < 1) || bmiChange >= 1) {
                                            status.setText(stuts + " So Bad");
                                        }
                                        break;

                                    case "Healthy Weight":
                                        if ((bmiChange < -1)) {
                                            status.setText(stuts + " So Bad");
                                        } else if ((bmiChange >= -1 && bmiChange < -0.6) || (bmiChange < -0.3 && bmiChange >= -0.6) ||
                                                (bmiChange >= 0.3 && bmiChange < 0.6) || (bmiChange >= 0.6 && bmiChange < 1) || bmiChange >= 1) {
                                            status.setText(stuts + " Be Careful");
                                        } else if ((bmiChange >= -0.3 && bmiChange < 0) || (bmiChange >= 0 && bmiChange < 0.3)) {
                                            status.setText(stuts + " Little Changes");
                                        }
                                        break;


                                    case "Underweight":
                                        if ((bmiChange < -1) || (bmiChange >= -1 && bmiChange < -0.6) ||
                                                (bmiChange < -0.3 && bmiChange >= -0.6)) {
                                            status.setText(stuts + " So Bad");
                                        } else if ((bmiChange >= -0.3 && bmiChange < 0) || (bmiChange >= 0 && bmiChange < 0.3)) {
                                            status.setText(stuts + " Little Changes");
                                        } else if ((bmiChange >= 0.3 && bmiChange < 0.6)) {
                                            status.setText(stuts + " Still Good");
                                        } else if ((bmiChange >= 0.6 && bmiChange < 1) || bmiChange >= 1) {
                                            status.setText(stuts + " Go Ahead");
                                        }
                                        break;
                                }

                            } else {
                                recyclerView.setVisibility(View.VISIBLE);
                                bmiLast = recordsList.get(0).getBmiForRecords();
                                bmiTowLast = recordsList.get(1).getBmiForRecords();


                                bmiChange = bmiLast - bmiTowLast;
                                String stuts = recordsList.get(0).getStutesRecords();

                                switch (stuts) {
                                    case "Obesity":
                                        if ((bmiChange < -1) || (bmiChange >= -1 && bmiChange < -0.6)) {
                                            status.setText(stuts + " Go Ahead");
                                        } else if ((bmiChange < -0.3 && bmiChange >= -0.6) || (bmiChange >= -0.3 && bmiChange > 0)) {
                                            status.setText(stuts + " Little Changes");
                                        } else if (bmiChange >= 0 && bmiChange < 0.3) {
                                            status.setText(stuts + " Be Careful");
                                        } else if ((bmiChange >= 0.3 && bmiChange < 0.6) || (bmiChange >= 0.6 && bmiChange < 1) || bmiChange >= 1) {
                                            status.setText(stuts + " So Bad");
                                        }
                                        break;

                                    case "Overweight":
                                        if ((bmiChange < -1) || (bmiChange >= 0.3 && bmiChange < 0.6)) {
                                            status.setText(stuts + " Be Careful");
                                        } else if ((bmiChange >= -1 && bmiChange < -0.6)) {
                                            status.setText(stuts + " Go Ahead");
                                        } else if ((bmiChange < -0.3 && bmiChange >= -0.6)) {
                                            status.setText(stuts + " still good");
                                        } else if ((bmiChange >= -0.3 && bmiChange < 0) || (bmiChange >= 0 && bmiChange < 0.3)) {
                                            status.setText(stuts + " Little Changes");
                                        } else if ((bmiChange >= 0.6 && bmiChange < 1) || bmiChange >= 1) {
                                            status.setText(stuts + " So Bad");
                                        }
                                        break;

                                    case "Healthy Weight":
                                        if ((bmiChange < -1)) {
                                            status.setText(stuts + " So Bad");
                                        } else if ((bmiChange >= -1 && bmiChange < -0.6) || (bmiChange < -0.3 && bmiChange >= -0.6) ||
                                                (bmiChange >= 0.3 && bmiChange < 0.6) || (bmiChange >= 0.6 && bmiChange < 1) || bmiChange >= 1) {
                                            status.setText(stuts + " Be Careful");
                                        } else if ((bmiChange >= -0.3 && bmiChange < 0) || (bmiChange >= 0 && bmiChange < 0.3)) {
                                            status.setText(stuts + " Little Changes");
                                        }
                                        break;


                                    case "Underweight":
                                        if ((bmiChange < -1) || (bmiChange >= -1 && bmiChange < -0.6) ||
                                                (bmiChange < -0.3 && bmiChange >= -0.6)) {
                                            status.setText(stuts + " So Bad");
                                        } else if ((bmiChange >= -0.3 && bmiChange < 0) || (bmiChange >= 0 && bmiChange < 0.3)) {
                                            status.setText(stuts + " Little Changes");
                                        } else if ((bmiChange >= 0.3 && bmiChange < 0.6)) {
                                            status.setText(stuts + " Still Good");
                                        } else if ((bmiChange >= 0.6 && bmiChange < 1) || bmiChange >= 1) {
                                            status.setText(stuts + " Go Ahead");
                                        }
                                        break;
                                }

                            }
                            listRecordAdapter = new listRecordAdapter(getApplicationContext(),recordsList);
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setAdapter(listRecordAdapter);
                            Log.d("TAG", recordsList.size()+"");

                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }



}