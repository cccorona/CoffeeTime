package mx.com.cesarcorona.coffeetime.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.adapter.MessageAdapter;
import mx.com.cesarcorona.coffeetime.pojo.ChatMessage;

public class ChatActivity extends BaseAnimatedActivity {



    private String MESSAGES_REFERENCE = "messages";
    private String MESSAGE_CHATROOM_HISTORY ="chatrooms";



    private ListView listView;
    private FirebaseUser user;
    private String loggedInUserName;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        user = FirebaseAuth.getInstance().getCurrentUser();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final EditText input = (EditText) findViewById(R.id.input);
        listView = (ListView) findViewById(R.id.list);

        showAllOldMessages();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().trim().equals("")) {
                    Toast.makeText(ChatActivity.this, R.string.enter_message, Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference(MESSAGES_REFERENCE+"/"+ user.getUid())
                            .push()
                            .setValue(new ChatMessage(input.getText().toString(),
                                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid())
                            );
                    input.setText("");
                }
            }
        });




    }

    private void showAllOldMessages() {
        loggedInUserName = user.getDisplayName();

        adapter = new MessageAdapter(this, ChatMessage.class, R.layout.item_in_message,
                FirebaseDatabase.getInstance().getReference(MESSAGES_REFERENCE +"/"+user.getUid()));
        listView.setAdapter(adapter);
    }

}
