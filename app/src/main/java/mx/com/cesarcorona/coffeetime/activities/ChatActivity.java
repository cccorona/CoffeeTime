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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import mx.com.cesarcorona.coffeetime.R;
import mx.com.cesarcorona.coffeetime.adapter.MessageAdapter;
import mx.com.cesarcorona.coffeetime.pojo.ChatHistoryElement;
import mx.com.cesarcorona.coffeetime.pojo.ChatMessage;
import mx.com.cesarcorona.coffeetime.pojo.CoffeDate;
import mx.com.cesarcorona.coffeetime.pojo.UserProfile;

import static mx.com.cesarcorona.coffeetime.activities.MainSettingsActivity.USER_PROFILES_REFERENCE;

public class ChatActivity extends BaseAnimatedActivity {


    public static String KEY_COFFEDATE ="coffeDate";
    private String MESSAGES_REFERENCE = "messages";
    public static String MESSAGE_CHATROOM_HISTORY ="chatrooms";



    private ListView listView;
    private FirebaseUser user;
    private String loggedInUserName;
    private MessageAdapter adapter;
    private String userChatWith;
    private boolean chatRoomExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        user = FirebaseAuth.getInstance().getCurrentUser();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final EditText input = (EditText) findViewById(R.id.input);
        listView = (ListView) findViewById(R.id.list);
        userChatWith = getIntent().getExtras().getString(KEY_COFFEDATE);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().trim().equals("")) {
                    Toast.makeText(ChatActivity.this, R.string.enter_message, Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseDatabase.getInstance()
                            .getReference(MESSAGES_REFERENCE+"/"+ user.getUid() +"/"+userChatWith)
                            .push()
                            .setValue(new ChatMessage(input.getText().toString(),
                                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid())
                            );
                    input.setText("");
                }
            }
        });

        final DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference(MESSAGE_CHATROOM_HISTORY + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    ChatHistoryElement chatRoom = snapshot.getValue(ChatHistoryElement.class);
                    if(chatRoom.getUserChatWith().equals(userChatWith)){
                        chatRoomExists = true;
                        chatRoom.setDataBaseReference(dataSnapshot.getKey());
                        showAllOldMessages(chatRoom.getDataBaseReference());
                        break;
                    }
                }

                if(!chatRoomExists){

                    DatabaseReference profile = FirebaseDatabase.getInstance().getReference(USER_PROFILES_REFERENCE +"/" +userChatWith);
                    profile.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                            ChatHistoryElement chatRoom = new ChatHistoryElement(userChatWith,userProfile.getFotoUrl(), new Date().getTime(),userProfile.getName());
                            FirebaseDatabase.getInstance().getReference(MESSAGE_CHATROOM_HISTORY+"/"+user.getUid()).child(userChatWith).setValue(chatRoom);
                            showAllOldMessages(userChatWith);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private void showAllOldMessages(String chatRoomReference) {
        loggedInUserName = user.getDisplayName();

        adapter = new MessageAdapter(this, ChatMessage.class, R.layout.item_in_message,
                FirebaseDatabase.getInstance().getReference(MESSAGES_REFERENCE +"/"+user.getUid() +"/"+ chatRoomReference) );
        listView.setAdapter(adapter);
    }

}
