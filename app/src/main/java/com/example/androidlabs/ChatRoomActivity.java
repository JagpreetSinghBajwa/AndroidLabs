package com.example.androidlabs;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Arrays;


public class ChatRoomActivity extends AppCompatActivity {

    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private Button buttonReceive;
    private ChatAdapter mChatAdapter;
    private FrameLayout frameLayout;
    private boolean fromTablet = true;
    private boolean side = true;
    long x;
    DBManager dbManager;
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    DetailsFragment fragment = new DetailsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        dbManager = new DBManager(this);
        dbManager.open();

        buttonSend = (Button) findViewById(R.id.send);
        buttonReceive = (Button) findViewById(R.id.recieve);
        chatText = findViewById(R.id.msg);
        listView = (ListView) findViewById(R.id.msgview);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mChatAdapter = new ChatAdapter(getApplicationContext());
        frameLayout = findViewById(R.id.message_frame_layout);
        if(frameLayout == null){
            fromTablet = false;
        }
        listView.setAdapter(mChatAdapter);
        ArrayList<Message> mArrayList = dbManager.getlist();
        mChatAdapter.adddatabase(mArrayList);

        printCursor(dbManager.fetch() , dbManager.getVersion());

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chatText.getText().toString().trim().length()>0) {
                    side = true;
                    sendChatMessage();
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter message",Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(chatText.getText().toString().trim().length()>0) {
                    side = false;
                    sendChatMessage();
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter message",Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                // TODO Auto-generated method stub
                AlertDialog.Builder alert = new AlertDialog.Builder(ChatRoomActivity.this);

                alert.setTitle("Do you want to delete this?");
                String temp = "The selected row is:"+index+"\n"+"The database id is:"+mChatAdapter.getItemId(index);
                alert.setMessage(temp);
                alert.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // delete messsage
                                x = mChatAdapter.getItemId(index);
                                dbManager.delete(x);
                                mChatAdapter.remove(index);
                                removeDetailFragment();
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Message message = mChatAdapter.getItem(i);
                Bundle bundle = new Bundle();
                bundle.putLong("messageId",message.msg_id);
                bundle.putString("message",message.message);
                bundle.putBoolean("is_send",message.isSend);

                if(fromTablet){
                    loadDetailFragment(bundle);
                }else{
                    Intent goToDetail = new Intent(ChatRoomActivity.this,EmptyActivity.class);
                    goToDetail.putExtra("message",bundle);
                    startActivity(goToDetail);
                }
            }
        });

    }

    private void loadDetailFragment(Bundle bundle){
        try{
            transaction = getSupportFragmentManager().beginTransaction();
            fragment = new DetailsFragment();
            fragment.setArguments(bundle);
            transaction.replace(R.id.message_frame_layout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }catch (Exception exception){

        }

    }

    private void removeDetailFragment(){
        try{
            transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(fragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();
            fragment = null;
        }catch (Exception exception){

        }
    }


    private void sendChatMessage() {
        dbManager.insert(side, chatText.getText().toString().trim());
        long id = dbManager.getLastAddedRowId();
        mChatAdapter.add(new Message(id , side, chatText.getText().toString()));
        chatText.setText("");
    }

    private void printCursor(Cursor c, int version){
        Log.i("Cursor" , "Database Version = " + version);
        int columnCount = c.getColumnCount();
        Log.i("Cursor" ,"Column Count = " + columnCount);
        String[] columnNames = c.getColumnNames();
        String str = Arrays.toString(columnNames);
        Log.i("Cursor" , "Column Names = " + str);
        int rowCount = c.getCount();
        Log.i("Cursor" , "Row Count = " + rowCount);
        for(int i = 0 ; i < c.getCount(); i++){
            ArrayList<Message> lst = dbManager.getlist();
            Message msg = lst.get(i);
            Log.i("Cursor" , "Row number " + i + " = " + msg.getMsg_id() + " " + msg.getMessage() + " " + msg.isSend);
        }
    }

}