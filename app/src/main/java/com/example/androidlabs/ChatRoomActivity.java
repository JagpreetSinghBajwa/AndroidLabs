package com.example.androidlabs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ChatRoomActivity extends AppCompatActivity {

    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private Button buttonReceive;
    private ChatAdapter mChatAdapter;
    private boolean side = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        // initialize controls
        buttonSend = (Button) findViewById(R.id.send);
        buttonReceive = (Button) findViewById(R.id.recieve);
        chatText = findViewById(R.id.msg);
        listView = (ListView) findViewById(R.id.msgview);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mChatAdapter = new ChatAdapter(getApplicationContext());

        // set adapter for listview
        listView.setAdapter(mChatAdapter);

        // click listener to send message
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check that user type something or not
                if(chatText.getText().toString().trim().length()>0) {
                    // send message and update list
                    side = true;
                    sendChatMessage();
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter message",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // click listener to receive message
        buttonReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check that user type something or not
                if(chatText.getText().toString().trim().length()>0) {
                    // receive message and update list
                    side = false;
                    sendChatMessage();
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter message",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // long click listern to delete chat message
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                // TODO Auto-generated method stub
                // show alert before deleting message
                AlertDialog.Builder alert = new AlertDialog.Builder(ChatRoomActivity.this);

                alert.setTitle("Do you want to delete this?");
                String temp = "The selected row is:"+index+"\n"+"The database id is:"+mChatAdapter.getItemId(index);
                alert.setMessage(temp);
                alert.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // delete messsage
                                mChatAdapter.remove(index);
                                dialog.cancel();
                            }
                        });
                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alert.show();

                return true;
            }
        });

    }

    // send and receive message method
    private boolean sendChatMessage() {
        mChatAdapter.add(new Message(side, chatText.getText().toString()));
        chatText.setText("");
        return true;
    }

}