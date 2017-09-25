package mx.com.cesarcorona.coffeetime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

import mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy;
import mx.com.cesarcorona.coffeetime.activities.JustCoffeActivity;
import mx.com.cesarcorona.coffeetime.activities.ReviewActivity;
import mx.com.cesarcorona.coffeetime.fragment.CoffeOptionsFragment;

public class MainActivity extends AppCompatActivity {

    private CardView cooffee, merienda;
    private LinearLayout backgroundCoffe, backgroundMeal;
    private CoffeOptionsFragment coffeOptionsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        cooffee = (CardView) findViewById(R.id.card_view_cofee);
        merienda = (CardView) findViewById(R.id.card_view_merienda);
        backgroundCoffe = (LinearLayout) findViewById(R.id.linear_background_coffe);
        backgroundMeal = (LinearLayout) findViewById(R.id.linear_background_meal);


        cooffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDatesActivity();
            }
        });

        merienda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meriendaActivity();
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


        backgroundCoffe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coffeOptionsFragment = (CoffeOptionsFragment) getSupportFragmentManager().findFragmentByTag(CoffeOptionsFragment.TAG);
                if(coffeOptionsFragment == null){
                    coffeOptionsFragment = new CoffeOptionsFragment();
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_from_down,0)
                            .add(R.id.fragment_selected_time,coffeOptionsFragment,CoffeOptionsFragment.TAG).commit();
                }else {
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.slide_from_right, 0)
                            .replace(R.id.fragment_selected_time, coffeOptionsFragment).commit();
                }
            }
        });


    }

    private void checkDatesActivity(){
        Intent intent = new Intent(MainActivity.this,JustCoffeActivity.class);
        startActivity(intent);
    }

    private void meriendaActivity(){
        Intent intent= new Intent(MainActivity.this, ReviewActivity.class);
        Bundle extras = new Bundle();
        extras.putString(ReviewActivity.KEY_USER,FirebaseAuth.getInstance().getCurrentUser().getUid());
        intent.putExtras(extras);
        startActivity(intent);
    }


}
