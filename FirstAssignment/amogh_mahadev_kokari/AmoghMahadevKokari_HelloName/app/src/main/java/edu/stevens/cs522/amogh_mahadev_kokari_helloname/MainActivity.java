package edu.stevens.cs522.amogh_mahadev_kokari_helloname;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements View.OnClickListener {

    String box_input = "@string/user_input";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button send_button = findViewById(R.id.button);
        send_button.setOnClickListener(this);
    }

    public void onClick(View v){
        Intent intent = new Intent(this, ShowActivity.class);
        EditText box_text = findViewById(R.id.text_box_id);
        String message= box_text.getText().toString();
        intent.putExtra(box_input,message);
        startActivity(intent);
    }
}