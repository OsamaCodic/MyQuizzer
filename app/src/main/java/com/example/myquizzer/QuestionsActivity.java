package com.example.myquizzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.service.controls.actions.FloatAction;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();


    public static final String FILE_NAME="QUIZZER";
    public static final String KEY_NAME="QUESTIONS";
    private TextView questions,noindicator;
    private FloatingActionButton bookmarkbtn;
    private LinearLayout optionsContainer;
    private Button sharebtn,nextbtn;
    private int count=0;
    private List<QuestionModel> list;

    private int position=0;
    private int score=0;
    private String category;
    private int setNo;
    private Dialog loadingDialog;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int matchedQuestionPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Toolbar tb=findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        questions=findViewById(R.id.question);
        noindicator=findViewById(R.id.no_indicator);
        bookmarkbtn=findViewById(R.id.Bookmarks_btn);
        optionsContainer=findViewById(R.id.options_container);
        sharebtn=findViewById(R.id.share_btn);
        nextbtn=findViewById(R.id.next_btn);
        preferences=getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor=preferences.edit();

        category = getIntent().getStringExtra("category");
        setNo = getIntent().getIntExtra("setNo", 1);


        list = new ArrayList<>();

        myRef.child("SETS").child(category).child("questions").orderByChild("setNo").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                for (DataSnapshot snapshot : snapshot2.getChildren()){
                    list.add(snapshot.getValue(QuestionModel.class));
                }
                if(list.size() > 0){

                    for(int i =0 ; i < 4; i++){
                        optionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkAnswer((Button) v);
                            }
                        });
                    }

                    playanim(questions, 0, list.get(position).getQuestion());

                    nextbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nextbtn.setEnabled(false);
                            nextbtn.setAlpha(0.7f);
                            enableOption(true);
                            position++;
                            if (position == list.size()){
                                ////Score Activity
                                return;
                            }
                            count=0;
                            playanim(questions, 0, list.get(position).getQuestion());
                        }
                    });
                }else {
                    finish();
                    Toast.makeText(QuestionsActivity.this, "No questions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuestionsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }


    private void playanim(View view, int value, String data){
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (value==0 && count<4){
                    String option="";
                    if(count==0){option=list.get(position).getOptionA();}
                    else if (count==1){option=list.get(position).getOptionB();}
                    else if (count==2){option=list.get(position).getOptionC();}
                    else if (count==3){option=list.get(position).getOptionD();}
                    playanim(optionsContainer.getChildAt(count),0, option);
                    count++;
                }

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                ((TextView)view).setText(data);
                if (value==0){
                    try {
                        ((TextView)view).setText(data);
                        noindicator.setText(position+1+"/"+list.size());
//                        if(modelMatch()){
//                            bookmarkbtn.setImageDrawable(getDrawable(R.drawable.bookmark));
//                        }
//                        else {
//                            bookmarkbtn.setImageDrawable(getDrawable(R.drawable.bookmark_icon));
//                        }
                    }catch (ClassCastException ex){
                        ((Button)view).setText(data);
                    }
                    view.setTag(data);
                    playanim(view,1, data);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void checkAnswer(Button SelectedOption){
        enableOption(false);
        nextbtn.setEnabled(true);
        nextbtn.setAlpha(1);

        if(SelectedOption.getText().toString().equals(list.get(position).getCorrectANS())){
            ///Correct Answer
            score++;
            SelectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

        }else {
            ///Incorrect Answer
            SelectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
            Button correctoption = (Button) optionsContainer.findViewWithTag(list.get(position).getCorrectANS());
            correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }
    }

    private void enableOption(boolean enable){
        for(int i =0 ; i < 4; i++){
            optionsContainer.getChildAt(i).setEnabled(enable);
            if (enable){
                optionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
            }
        }
    }

}