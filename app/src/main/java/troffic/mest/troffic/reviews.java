package troffic.mest.troffic;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class reviews extends Main {

    ListView listView;
    Button btn;
    TextView header;
    EditText editText;
    String route;
    ChatArrayAdapter chatArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        header = (TextView)findViewById(R.id.review_header);

        Intent intent = getIntent();
        if (intent.hasExtra("source_dest")) {
            route = intent.getStringExtra("source_dest");
            header.setText(route);
        }

        editText = (EditText) findViewById(R.id.edit);
        btn = (Button) findViewById(R.id.btn);

        listView = (ListView) findViewById(R.id.listview);
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.single_msg_chat);
        listView.setAdapter(chatArrayAdapter);

        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
                    return sendChatMessage();
                else
                    return false;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });
    }

    private boolean sendChatMessage(){

        final String chat_message = editText.getText().toString().trim();
        if (!chat_message.equals("")){
            //updating the current device
            chatArrayAdapter.add(new ChatMessage(false, chat_message));
            editText.setText("");
            //sending gcm message to the paired device
            final Bundle dataBundle = new Bundle();
            dataBundle.putString("apiKey", API_KEY);
            dataBundle.putString("sender_uname", "***MY USERNAME***");
            dataBundle.putString("sender_reg_id", "***MY REG ID***");
            dataBundle.putString("reg_id", "***GROUP REG ID***");
            dataBundle.putString("chat_message", chat_message);

            AsyncTask sendTask = new AsyncTask() {
                String msg;
                @Override
                protected Object doInBackground(Object[] objects) {
                    JSONGetter parser = new JSONGetter();
                    try {
                        String result=parser.DoHttpRequest(url_str +"gcm_engine.php","POST",dataBundle);
                        if (result != null)
                            msg = new JSONObject(result).getString("result");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    if (msg != null){
                        try {
                            if (new JSONObject(msg).has("failure")){
                                if (new JSONObject(msg).getString("failure").equals("0"))
                                    Toast.makeText(reviews.this, "Message Sent", Toast.LENGTH_SHORT).show();
                                else if (!new JSONObject(msg).getString("failure").equals("0"))
                                    Toast.makeText(reviews.this, "failed to send", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(reviews.this, msg, Toast.LENGTH_SHORT).show();
                            }
                            Log.d("GCM RESULT: ", msg);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else
                        Toast.makeText(reviews.this, "Connection Turned OFF", Toast.LENGTH_SHORT).show();
                }
            };
            sendTask.execute();
        }
        else
            Toast.makeText(this, "Empty Message", Toast.LENGTH_SHORT).show();
        return true;
    }
}
