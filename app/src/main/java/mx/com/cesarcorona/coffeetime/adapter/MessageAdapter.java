package mx.com.cesarcorona.coffeetime.adapter;

import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.activities.ChatActivity;
import mx.com.cesarcorona.coffeetime.pojo.ChatMessage;

/**
 * Created by ccabrera on 06/10/17.
 */


public class MessageAdapter extends FirebaseListAdapter<ChatMessage> {

    private ChatActivity activity;
    private FirebaseUser userLoged;

    public MessageAdapter(ChatActivity activity, Class<ChatMessage> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
        this.activity = activity;
        userLoged = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void populateView(View v, ChatMessage model, int position) {
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        TextView messageUser = (TextView) v.findViewById(R.id.message_user);
        TextView messageTime = (TextView) v.findViewById(R.id.message_time);

        messageText.setText(model.getMessageText());
        messageUser.setText(model.getMessageUser());

        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ChatMessage chatMessage = getItem(position);
        if (chatMessage.getMessageUserId().equals(userLoged.getUid()))
            view = activity.getLayoutInflater().inflate(R.layout.item_out_message, viewGroup, false);
        else
            view = activity.getLayoutInflater().inflate(R.layout.item_in_message, viewGroup, false);

        //generating view
        populateView(view, chatMessage, position);

        return view;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }

}