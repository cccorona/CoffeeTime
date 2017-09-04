package mx.com.cesarcorona.coffeetime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        View rootView = LayoutInflater.from(context).inflate(R.layout.review_item_layout,parent,false);
        TextView coomentary = (TextView) rootView.findViewById(R.id.review_text);
        coomentary.setText(reviews.get(position).getCommentary());

        TextView reviewer = (TextView) rootView.findViewById(R.id.review_name);
        reviewer.setText(reviews.get(position).getReviewer());

        ImageView one = (ImageView) rootView.findViewById(R.id.one);
        ImageView two = (ImageView) rootView.findViewById(R.id.two);
        ImageView twee = (ImageView) rootView.findViewById(R.id.tree);
        ImageView four = (ImageView) rootView.findViewById(R.id.four);
        ImageView five = (ImageView) rootView.findViewById(R.id.five);


        switch (reviews.get(position).getRateStars()){
            case 1:
                two.setVisibility(View.GONE);
                twee.setVisibility(View.GONE);
                four.setVisibility(View.GONE);
                five.setVisibility(View.GONE);
                break;
            case 2:
                twee.setVisibility(View.GONE);
                four.setVisibility(View.GONE);
                five.setVisibility(View.GONE);
                break;
            case 3:
                four.setVisibility(View.GONE);
                five.setVisibility(View.GONE);
                break;
            case 4:
                five.setVisibility(View.GONE);
                break;
            case 5:
                break;
        }


        return rootView;

    }
}
