package troffic.mest.troffic;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abu-Bakr Siddique on 5/31/2016.
 */
public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    TextView chatText;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    LinearLayout single_chat_container;

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public void add(ChatMessage cht_msg) {
        super.add(cht_msg);
        chatMessageList.add(cht_msg);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_msg_chat, parent, false);
        }

        single_chat_container = (LinearLayout) row.findViewById(R.id.single_chat_container);
        ChatMessage chatMessageObj = getItem(position);
        chatText = (TextView) row.findViewById(R.id.chat_msg);
        chatText.setText(chatMessageObj.message);
        chatText.setBackgroundResource(chatMessageObj.left ? R.drawable.bubble_left : R.drawable.bubble_right);
        single_chat_container.setGravity(chatMessageObj.left ? Gravity.START : Gravity.END);
        return row;
    }
}
