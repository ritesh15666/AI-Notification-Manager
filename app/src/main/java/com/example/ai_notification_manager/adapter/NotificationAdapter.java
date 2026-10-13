package com.example.ai_notification_manager.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ai_notification_manager.R;
import com.example.ai_notification_manager.classifier.NotificationPriorityEngine;
import com.example.ai_notification_manager.model.NotificationModel;
import com.example.ai_notification_manager.utils.AppIconResolver;
import com.example.ai_notification_manager.utils.TimestampUtil;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> notificationList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NotificationModel notification);
    }

    public NotificationAdapter(List<NotificationModel> notificationList, OnItemClickListener listener) {
        this.notificationList = notificationList;
        this.listener = listener;
    }

    public void updateList(List<NotificationModel> newList) {
        this.notificationList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notification = notificationList.get(position);

        CharSequence appLabel = AppIconResolver.getAppLabel(holder.itemView.getContext(), notification.getPackageName(), notification.getSourceApp());
        Drawable appIcon = AppIconResolver.getAppIcon(holder.itemView.getContext(), notification.getPackageName());

        holder.tvSourceApp.setText(appLabel);
        if (holder.ivAppIcon != null) {
            holder.ivAppIcon.setImageDrawable(appIcon);
        }

        holder.tvTimestamp.setText(TimestampUtil.format(notification.getTimestamp()));
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        holder.chipCategory.setText(notification.getCategory());

        // Category badge text color
        int colorRes;
        switch (notification.getCategory().toLowerCase()) {
            case "important": colorRes = R.color.categoryImportant; break;
            case "study": colorRes = R.color.categoryStudy; break;
            case "career": colorRes = R.color.categoryCareer; break;
            case "personal": colorRes = R.color.categoryPersonal; break;
            case "shopping": colorRes = R.color.categoryShopping; break;
            default: colorRes = R.color.categoryOther; break;
        }
        holder.chipCategory.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), colorRes));

        // Priority evaluation & badge binding
        NotificationPriorityEngine.PriorityResult pResult = NotificationPriorityEngine.evaluate(
                notification.getTitle(),
                notification.getMessage(),
                notification.getPackageName(),
                notification.getCategory()
        );
        holder.chipPriority.setText(pResult.getLevel());
        int priorityColor;
        switch (pResult.getLevel()) {
            case "CRITICAL": priorityColor = R.color.colorCritical; break;
            case "HIGH": priorityColor = R.color.colorWarning; break;
            case "MEDIUM": priorityColor = R.color.colorPrimary; break;
            default: priorityColor = R.color.colorMutedText; break;
        }
        holder.chipPriority.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), priorityColor));

        // Read status indicator
        holder.tvUnreadDot.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAppIcon;
        TextView tvSourceApp, tvTimestamp, tvTitle, tvMessage, chipCategory, chipPriority, tvUnreadDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAppIcon = itemView.findViewById(R.id.ivAppIcon);
            tvSourceApp = itemView.findViewById(R.id.tvSourceApp);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            chipCategory = itemView.findViewById(R.id.chipCategory);
            chipPriority = itemView.findViewById(R.id.chipPriority);
            tvUnreadDot = itemView.findViewById(R.id.tvUnreadDot);
        }
    }
}
