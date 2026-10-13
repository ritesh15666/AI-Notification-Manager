package com.example.ai_notification_manager.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ai_notification_manager.NotificationDetailActivity;
import com.example.ai_notification_manager.R;
import com.example.ai_notification_manager.model.AssistantMessageModel;
import com.example.ai_notification_manager.model.NotificationModel;
import com.example.ai_notification_manager.utils.TimestampUtil;

import java.util.List;

public class AssistantAdapter extends RecyclerView.Adapter<AssistantAdapter.ViewHolder> {

    private List<AssistantMessageModel> messageList;

    public AssistantAdapter(List<AssistantMessageModel> messageList) {
        this.messageList = messageList;
    }

    public void updateList(List<AssistantMessageModel> newList) {
        this.messageList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssistantMessageModel message = messageList.get(position);
        holder.tvMessageText.setText(message.getMessageText());

        // Alignment of bubble: user on right, assistant on left
        // (Simplified alignment for chat layout)
        if (message.isUser()) {
            holder.layoutBubble.setBackgroundResource(R.drawable.bg_chip);
        } else {
            holder.layoutBubble.setBackgroundResource(R.drawable.bg_glass_card);
        }

        // Render matching notification cards if present
        holder.layoutNotificationCards.removeAllViews();
        List<NotificationModel> notifs = message.getNotifications();
        if (notifs != null && !notifs.isEmpty()) {
            holder.layoutNotificationCards.setVisibility(View.VISIBLE);
            LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
            for (NotificationModel n : notifs) {
                View cardView = inflater.inflate(R.layout.item_notification, holder.layoutNotificationCards, false);

                // Populate card fields
                TextView tvSourceApp = cardView.findViewById(R.id.tvSourceApp);
                TextView tvTimestamp = cardView.findViewById(R.id.tvTimestamp);
                TextView tvTitle = cardView.findViewById(R.id.tvTitle);
                TextView tvMessage = cardView.findViewById(R.id.tvMessage);
                TextView chipCategory = cardView.findViewById(R.id.chipCategory);

                if (tvSourceApp != null) tvSourceApp.setText(n.getSourceApp());
                if (tvTimestamp != null) tvTimestamp.setText(TimestampUtil.format(n.getTimestamp()));
                if (tvTitle != null) tvTitle.setText(n.getTitle());
                if (tvMessage != null) tvMessage.setText(n.getMessage());
                if (chipCategory != null) chipCategory.setText(n.getCategory());

                cardView.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), NotificationDetailActivity.class);
                    intent.putExtra("notification", n);
                    v.getContext().startActivity(intent);
                });

                holder.layoutNotificationCards.addView(cardView);
            }
        } else {
            holder.layoutNotificationCards.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutBubble, layoutNotificationCards;
        TextView tvMessageText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutBubble = itemView.findViewById(R.id.layoutBubble);
            layoutNotificationCards = itemView.findViewById(R.id.layoutNotificationCards);
            tvMessageText = itemView.findViewById(R.id.tvMessageText);
        }
    }
}
