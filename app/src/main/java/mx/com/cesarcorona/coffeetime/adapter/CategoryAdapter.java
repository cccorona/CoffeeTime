package mx.com.cesarcorona.coffeetime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.pojo.Categoria;


/**
 * Created by Corona on 8/7/2017.
 */

public class CategoryAdapter extends BaseAdapter {

    private LinkedList<Categoria> allCategories;
    private Context context;
    private CategorySelectedListener categorySelectedListener;


    public interface CategorySelectedListener{
        void OnCategoryClicked(Categoria categoria);
    }

    public void setCategorySelectedListener(CategorySelectedListener categorySelectedListener) {
        this.categorySelectedListener = categorySelectedListener;
    }



    public CategoryAdapter(LinkedList<Categoria> allCategories, Context context) {
        this.allCategories = allCategories;
        this.context = context;
    }

    @Override
    public int getCount() {
        return allCategories.size();
    }

    @Override
    public Categoria getItem(int i) {
        return allCategories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View rootView = inflater.inflate(R.layout.single_item,viewGroup,false);
        TextView title = (TextView) rootView.findViewById(R.id.title_spinner);
        title.setText(allCategories.get(i).getDisplay_title());
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categorySelectedListener != null){
                   // categorySelectedListener.OnCategoryClicked(allCategories.get(i));
                }
            }
        });
        return  rootView;
    }
}
