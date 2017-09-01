package mx.com.cesarcorona.coffeetime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy;
import mx.com.cesarcorona.coffeetime.activities.JustCoffeActivity;

public class MainActivity extends AppCompatActivity {

    private CardView cooffee, merienda;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Choose an option:");
        setSupportActionBar(toolbar);

        cooffee = (CardView) findViewById(R.id.card_view_cofee);
        merienda = (CardView) findViewById(R.id.card_view_merienda);

        cooffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDatesActivity();
            }
        });

        merienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDatesActivity();
            }
        });

        Button logout = (Button) findViewById(R.id.log_out_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

    }

    private void checkDatesActivity(){
        Intent intent = new Intent(MainActivity.this,JustCoffeActivity.class);
        startActivity(intent);
    }


}
