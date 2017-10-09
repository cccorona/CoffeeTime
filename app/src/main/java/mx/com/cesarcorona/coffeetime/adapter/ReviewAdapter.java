package mx.com.cesarcorona.coffeetime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;

import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.pojo.ReviewObject;

/**
 * Created by ccabrera on 03/09/17.
 */

public class ReviewAdapter extends BaseAdapter {


    private Context context;
    private LinkedList<ReviewObject> reviews;

    public ReviewAdapter(Context context, LinkedList<ReviewObject> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public ReviewObject getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.review_item,parent,false);
        TextView coomentary = (TextView) rootView.findViewById(R.id.review_text);
        RatingBar ratingBar  = (RatingBar)rootView.findViewById(R.id.rating);
        ImageView foto = (ImageView)rootView.findViewById(R.id.foto_chat);
        TextView reviewer = (TextView)rootView.findViewById(R.id.nombreChat);
        TextView date = (TextView)rootView.findViewById(R.id.fecha_chat);
        coomentary.setText(reviews.get(position).getCommentary());
        ratingBar.setNumStars(5);
        ratingBar.setMax(5);
        ratingBar.setRating(reviews.get(position).getRateStars());
        if(reviews.get(position).getReviewwerPhotoUrl() != null){
            Picasso.with(context).load(reviews.get(position).getReviewwerPhotoUrl()).into(foto);
        }

        reviewer.setText(reviews.get(position).getReviewer());
        //date.setText(reviews.get(position).get);





        return rootView;

    }
}
