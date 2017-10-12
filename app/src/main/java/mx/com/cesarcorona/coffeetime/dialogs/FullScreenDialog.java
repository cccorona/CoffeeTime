package mx.com.cesarcorona.coffeetime.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.pojo.CoffeDate;
import mx.com.cesarcorona.coffeetime.pojo.ReviewObject;

import static mx.com.cesarcorona.coffeetime.activities.CoffeTimeActiviy.USER_PROFILES_REFERENCE;
import static mx.com.cesarcorona.coffeetime.activities.ReviewActivity.REVIEWS_REFENRECE;


/**
 * Created by Corona on 5/27/2017.
 */

public class FullScreenDialog extends DialogFragment implements CoffeDate.FillInformationInterface{


    public static String TAG = FullScreenDialog.class.getSimpleName();
    public static String KEY_DATE = "date";

    private CoffeDate dateToReview;

    private TextView ratePersonName ;
    private ImageView fotoPerson;
    private RatingBar rate ;
    private  EditText reviewText;
    private Button cancelButton, reviewButton;
    private ProgressDialog pDialog;


    private ReviewObject reviewObject;


    public FullScreenDialog(){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        reviewObject = new ReviewObject();

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog fulldialog = new Dialog(getContext(), R.style.FullScreenDialog);
        fulldialog.setContentView(R.layout.rate_dialog_layout);

         ratePersonName = (TextView) fulldialog.findViewById(R.id.nombreChat);
         fotoPerson = (ImageView) fulldialog.findViewById(R.id.foto_chat);
         rate = (RatingBar)fulldialog.findViewById(R.id.rating);
         reviewText = (EditText)fulldialog.findViewById(R.id.review_text);
        cancelButton = (Button)fulldialog.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        reviewButton = (Button) fulldialog.findViewById(R.id.rate_button);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(reviewText.getText().toString().length()>0 && rate.getNumStars() >0){
                    reviewObject.setCommentary(reviewText.getText().toString());
                    reviewObject.setRateStars((int)rate.getRating());
                    if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null){
                        reviewObject.setReviewwerPhotoUrl(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString());
                    }
                    pDialog = new ProgressDialog(getActivity());
                    pDialog.setMessage(getResources().getString(R.string.please_wait_d));
                    pDialog.setCancelable(false);
                    showpDialog();

                    String reviewUser ="";
                    if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(dateToReview.getUser1())){
                        reviewUser = dateToReview.getUser2();
                    }else{
                        reviewUser = dateToReview.getUser1();
                    }

                   FirebaseDatabase.getInstance()
                            .getReference(REVIEWS_REFENRECE +"/"+ reviewUser).push().setValue(reviewObject).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           hidepDialog();
                           getDialog().dismiss();
                       }
                   });





                }else{
                    Toast.makeText(getActivity(),"Fill all review fields ",Toast.LENGTH_LONG).show();
                }


            }
        });


        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        fulldialog.getWindow().setLayout(width, height);
        dateToReview= (CoffeDate) getArguments().getSerializable(KEY_DATE);
        dateToReview.setFillInformationInterface(FullScreenDialog.this);
        String wichUser ="";
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(dateToReview.getUser1())){
            wichUser = dateToReview.getUser2();
        }else{
            wichUser = dateToReview.getUser1();
        }
        dateToReview.fullFillUserProfileWithReference(USER_PROFILES_REFERENCE +"/" +wichUser );

        ratePersonName.setText(dateToReview.getProfile1().getName());


        reviewObject.setReviewer(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());


        return fulldialog;
    }

    @Override
    public void OnDataChangeSuccess() {
        if(dateToReview.getProfile1().getFotoUrl() != null){
            Picasso.with(getActivity()).load(dateToReview.getProfile1().getFotoUrl()).into(fotoPerson);
        }
    }

    @Override
    public void OnError(String error) {

    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}