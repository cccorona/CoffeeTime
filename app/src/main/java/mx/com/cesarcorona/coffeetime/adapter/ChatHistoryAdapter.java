package mx.com.cesarcorona.coffeetime.adapter;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.activities.ChatActivity;
import mx.com.cesarcorona.coffeetime.activities.HistoryChatActivity;
import mx.com.cesarcorona.coffeetime.pojo.ChatHistoryElement;
import mx.com.cesarcorona.coffeetime.pojo.ChatMessage;

/**
 * Created by ccabrera on 06/10/17.
 */

public class ChatHistoryAdapter extends FirebaseListAdapter<ChatHistoryElement> {


    private HistoryChatActivity activity;
    private ChatSelectedInterface chatSelectedInterface;

    public interface ChatSelectedInterface{
        void OnChatSelected(ChatHistoryElement chatRoom);
    }

    public ChatHistoryAdapter(HistoryChatActivity context, Class<ChatHistoryElement> modelClass, @LayoutRes int modelLayout, DatabaseReference query) {
        super(context, modelClass, modelLayout, query);
        this.activity = context;
    }


    public void setChatSelectedInterface(ChatSelectedInterface chatSelectedInterface) {
        this.chatSelectedInterface = chatSelectedInterface;
    }

    @Override
    protected void populateView(View v, final ChatHistoryElement model, int position) {
        ImageView userPhoto = (ImageView) v.findViewById(R.id.foto_chat);
        TextView nombreChat = (TextView)v.findViewById(R.id.nombreChat);
        TextView fechaChat = (TextView)v.findViewById(R.id.fecha_chat);

        if(model.getUserPhoto() != null){
            Picasso.with(activity).load(model.getUserPhoto()).fit().into(userPhoto);
        }
        nombreChat.setText(model.getUserChatWithDisplayName());
        fechaChat.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getUserChatDate()));

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatSelectedInterface != null){
                      chatSelectedInterface.OnChatSelected(model);
                }
            }
        });




    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ChatHistoryElement chatHistoryElement = getItem(position);
            view = activity.getLayoutInflater().inflate(R.layout.chat_history_item, viewGroup, false);
        //generating view
        populateView(view, chatHistoryElement, position);

        return view;
    }



}
