package com.example.centralchat.messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.centralchat.R;
import com.example.centralchat.chat.Chat;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {
    private List<MessagesList> messagesLists;
    private final Context context;

    public MessagesAdapter(List<MessagesList> messagesLists, Context context) {
        this.messagesLists = messagesLists;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_adapter_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MessagesList list2 = messagesLists.get(position);

        if(list2.getProfilePic().equals("default")) {
            Glide.with(context).load(R.drawable.user_icon).into(holder.profilePic);
        }else {
            Glide.with(context).load(list2.getProfilePic()).into(holder.profilePic);
        }

        holder.name.setText(list2.getUserName());
        holder.lastMessage.setText(list2.getLastMessage());

        if(list2.getUnseenMessages() == 0) {
            holder.unseenMessages.setVisibility(View.GONE);
            holder.lastMessage.setTextColor(Color.parseColor("#454545"));
        }else {
            holder.unseenMessages.setVisibility(View.VISIBLE);
            holder.unseenMessages.setText(context
                    .getString(R.string.unseenmessages, list2.getUnseenMessages()));
            holder.lastMessage.setTextColor(Color.parseColor("#3590F3"));
        }

        Log.d("Message tag", String.valueOf(list2.getUnseenMessages()));
        holder.rootLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat.class);
            intent.putExtra("userName", list2.getUserName());
            intent.putExtra("profile_pic", list2.getProfilePic());
            intent.putExtra("chat_key", list2.getChatKey());
            intent.putExtra("indexNum", list2.getGetOtherIndexNum());
            context.startActivity(intent);
        });
    }

    public void updateData(List<MessagesList> messagesLists) {
        this.messagesLists = messagesLists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messagesLists.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView profilePic;
        private final TextView name;
        private final TextView lastMessage;
        private final TextView unseenMessages;
        private final LinearLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_picture);
            name = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.last_message);
            unseenMessages = itemView.findViewById(R.id.unseen_messages);
            rootLayout = itemView.findViewById(R.id.root_layout);
        }
    }
}
