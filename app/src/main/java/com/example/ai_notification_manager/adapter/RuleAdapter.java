package com.example.ai_notification_manager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ai_notification_manager.R;
import com.example.ai_notification_manager.model.RuleModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.ViewHolder> {

    private List<RuleModel> ruleList;
    private final OnRuleActionListener listener;

    public interface OnRuleActionListener {
        void onToggleEnabled(RuleModel rule, boolean enabled);
        void onEdit(RuleModel rule);
        void onDelete(RuleModel rule);
    }

    public RuleAdapter(List<RuleModel> ruleList, OnRuleActionListener listener) {
        this.ruleList = ruleList;
        this.listener = listener;
    }

    public void updateList(List<RuleModel> newList) {
        this.ruleList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RuleModel rule = ruleList.get(position);

        holder.tvRuleName.setText(rule.getRuleName());
        holder.tvRuleDetails.setText(String.format("Keyword: '%s' → %s (%s)", rule.getKeyword(), rule.getTargetCategory(), rule.getTargetPriority()));

        holder.switchRuleEnabled.setOnCheckedChangeListener(null);
        holder.switchRuleEnabled.setChecked(rule.isEnabled());
        holder.switchRuleEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
            rule.setEnabled(isChecked);
            if (listener != null) {
                listener.onToggleEnabled(rule, isChecked);
            }
        });

        holder.btnEditRule.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(rule);
            }
        });

        holder.btnDeleteRule.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(rule);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ruleList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRuleName, tvRuleDetails;
        SwitchMaterial switchRuleEnabled;
        MaterialButton btnEditRule, btnDeleteRule;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRuleName = itemView.findViewById(R.id.tvRuleName);
            tvRuleDetails = itemView.findViewById(R.id.tvRuleDetails);
            switchRuleEnabled = itemView.findViewById(R.id.switchRuleEnabled);
            btnEditRule = itemView.findViewById(R.id.btnEditRule);
            btnDeleteRule = itemView.findViewById(R.id.btnDeleteRule);
        }
    }
}
