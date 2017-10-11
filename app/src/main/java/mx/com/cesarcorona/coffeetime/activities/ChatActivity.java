package mx.com.cesarcorona.coffeetime.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
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
    public static String KEY_COFFEDATE_USER1 ="user1";
    public static String KEY_COFFEDATE_USER2 ="user2";

    private String MESSAGES_REFERENCE = "messages";
    public static String MESSAGE_CHATROOM_HISTORY ="chatrooms";
    private CoffeDate dateSelected;
    private String me="";
    private String otherUser="";
    private boolean firstMessageSendToOtherUser;
    private DatabaseReference otherUserChatReference;
    private DatabaseReference chatUserReference;



    private ListView listView;
    private FirebaseUser user;
    private String loggedInUserName;
    private MessageAdapter adapter;
    private String userChatWith;
    private boolean chatRoomExists;
    private boolean chatOtherRoomExisst;
    private EditText input;
    private String user1;
    private String user2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        firstMessageSendToOtherUser = true;
        user = FirebaseAuth.getInstance().getCurrentUser();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
         input = (EditText) findViewById(R.id.input);
        listView = (ListView) findViewById(R.id.list);
        View emptyView = LayoutInflater.from(ChatActivity.this).inflate(R.layout.empty_view_list,null);
        listView.setEmptyView(emptyView);
        userChatWith = getIntent().getExtras().getString(KEY_COFFEDATE);
        user1 = getIntent().getExtras().getString(KEY_COFFEDATE_USER1);
        user2 = getIntent().getExtras().getString(KEY_COFFEDATE_USER2);




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().trim().equals("")) {
                    Toast.makeText(ChatActivity.this, R.string.enter_message, Toast.LENGTH_SHORT).show();
                } else {


                    DatabaseReference messageReference = FirebaseDatabase.getInstance()
                            .getReference(MESSAGES_REFERENCE+"/"+ user.getUid() +"/"+userChatWith);
                    messageReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(firstMessageSendToOtherUser){
                                firstMessageSendToOtherUser = false ;
                                sendFirstMessageToUser(input.getText().toString());

                            }else{
                                sendMessageToUser(input.getText().toString());
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    messageReference.push()
                            .setValue(new ChatMessage(input.getText().toString(),
                                    FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                                    FirebaseAuth.getInstance().getCurrentUser().getUid())
                            );

                }
            }
        });



        if(user1.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            me =user1;
            otherUser = user2;
        }else{
            me =user2;
            otherUser =user1;
        }

        final DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference(MESSAGE_CHATROOM_HISTORY + "/" + me);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    ChatHistoryElement chatRoom = snapshot.getValue(ChatHistoryElement.class);
                    if(chatRoom.getUserChatWith().equals(userChatWith)){
                        chatRoomExists = true;
                        chatRoom.setDataBaseReference(dataSnapshot.getKey());
                        showAllOldMessages(userChatWith);
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



    private void sendFirstMessageToUser(final String message){
        final DatabaseReference databaseReference =
                FirebaseDatabase.getInstance().getReference(MESSAGE_CHATROOM_HISTORY + "/" + otherUser);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                    ChatHistoryElement chatRoom = snapshot.getValue(ChatHistoryElement.class);
                    if(chatRoom.getUserChatWith().equals(me)){
                        chatOtherRoomExisst = true;
                        chatRoom.setDataBaseReference(dataSnapshot.getKey());
                        otherUserChatReference = FirebaseDatabase.getInstance().getReference(MESSAGES_REFERENCE+"/"+otherUser+"/"+me);
                        //showAllOldMessages(chatRoom.getDataBaseReference());
                        sendMessageToUser(message);
                        break;
                    }
                }

                if(!chatOtherRoomExisst){

                    DatabaseReference profile = FirebaseDatabase.getInstance().getReference(USER_PROFILES_REFERENCE +"/" +me);
                    profile.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                            ChatHistoryElement chatRoom = new ChatHistoryElement(me,userProfile.getFotoUrl(), new Date().getTime(),userProfile.getName());
                            FirebaseDatabase.getInstance().getReference(MESSAGE_CHATROOM_HISTORY+"/"+otherUser).child(me).setValue(chatRoom);
                            otherUserChatReference = FirebaseDatabase.getInstance().getReference(MESSAGES_REFERENCE+"/"+otherUser+"/"+me);
                            sendMessageToUser(message);

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


    private void sendMessageToUser(String message){
         otherUserChatReference.push()
                 .setValue(new ChatMessage(message,
                         FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                         FirebaseAuth.getInstance().getCurrentUser().getUid())
                 );
        input.setText("");
    }


}
