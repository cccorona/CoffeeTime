package mx.com.cesarcorona.coffeetime.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.adapter.ChatHistoryAdapter;
import mx.com.cesarcorona.coffeetime.adapter.MessageAdapter;
import mx.com.cesarcorona.coffeetime.pojo.ChatHistoryElement;
import mx.com.cesarcorona.coffeetime.pojo.ChatMessage;

import static mx.com.cesarcorona.coffeetime.activities.ChatActivity.KEY_COFFEDATE;
import static mx.com.cesarcorona.coffeetime.activities.ChatActivity.MESSAGE_CHATROOM_HISTORY;

public class HistoryChatActivity extends BaseAnimatedActivity implements ChatHistoryAdapter.ChatSelectedInterface{


    private ListView listView;
    private ChatHistoryAdapter adapter;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_chat);
        user = FirebaseAuth.getInstance().getCurrentUser();


        listView = (ListView) findViewById(R.id.list);
        showAllOldMessages();

    }



    private void showAllOldMessages() {

        adapter = new ChatHistoryAdapter(this, ChatHistoryElement.class, R.layout.item_in_message,
                FirebaseDatabase.getInstance().getReference(MESSAGE_CHATROOM_HISTORY +"/"+user.getUid() ));
        listView.setAdapter(adapter);
        adapter.setChatSelectedInterface(HistoryChatActivity.this);
    }

    @Override
    public void OnChatSelected(ChatHistoryElement chatRoom) {

        Intent chatsIntent = new Intent(HistoryChatActivity.this,ChatActivity.class);
        Bundle extras = new Bundle();
        extras.putString(KEY_COFFEDATE,chatRoom.getUserChatWith());
        chatsIntent.putExtras(extras);
        startActivity(chatsIntent);
    }
}
