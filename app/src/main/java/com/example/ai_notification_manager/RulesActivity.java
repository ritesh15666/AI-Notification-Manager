package com.example.ai_notification_manager;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ai_notification_manager.adapter.RuleAdapter;
import com.example.ai_notification_manager.db.RuleDatabaseHelper;
import com.example.ai_notification_manager.model.RuleModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class RulesActivity extends AppCompatActivity {

    private RuleDatabaseHelper dbHelper;
    private RuleAdapter adapter;
    private RecyclerView rvRules;
    private LinearLayout layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        dbHelper = RuleDatabaseHelper.getInstance(this);

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        MaterialButton btnAddRule = findViewById(R.id.btnAddRule);
        btnAddRule.setOnClickListener(v -> showEditRuleDialog(null));

        rvRules = findViewById(R.id.rvRules);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        rvRules.setLayoutManager(new LinearLayoutManager(this));
        List<RuleModel> rules = dbHelper.getAllRules();
        adapter = new RuleAdapter(rules, new RuleAdapter.OnRuleActionListener() {
            @Override
            public void onToggleEnabled(RuleModel rule, boolean enabled) {
                updateRuleEnabledState(rule.getId(), enabled);
                Toast.makeText(RulesActivity.this, "Rule " + (enabled ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEdit(RuleModel rule) {
                showEditRuleDialog(rule);
            }

            @Override
            public void onDelete(RuleModel rule) {
                deleteRule(rule.getId());
            }
        });
        rvRules.setAdapter(adapter);
        updateEmptyState(rules);
    }

    private void updateRuleEnabledState(int ruleId, boolean enabled) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("isEnabled", enabled ? 1 : 0);
            db.update("custom_rules", cv, "id = ?", new String[]{String.valueOf(ruleId)});
        } catch (Exception ignored) {}
    }

    private void deleteRule(int ruleId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Rule")
                .setMessage("Are you sure you want to delete this custom rule?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    try {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.delete("custom_rules", "id = ?", new String[]{String.valueOf(ruleId)});
                        refreshRules();
                        Toast.makeText(this, "Rule deleted", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to delete rule", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditRuleDialog(RuleModel ruleToEdit) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rule_edit, null);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etRuleName = dialogView.findViewById(R.id.etRuleName);
        EditText etRuleKeyword = dialogView.findViewById(R.id.etRuleKeyword);
        Spinner spinnerTargetCategory = dialogView.findViewById(R.id.spinnerTargetCategory);
        Spinner spinnerTargetPriority = dialogView.findViewById(R.id.spinnerTargetPriority);
        MaterialButton btnCancelRule = dialogView.findViewById(R.id.btnCancelRule);
        MaterialButton btnSaveRule = dialogView.findViewById(R.id.btnSaveRule);

        String[] categories = {"Important", "Study", "Career", "Personal", "Shopping", "Other"};
        String[] priorities = {"Critical", "High", "Medium", "Low"};

        spinnerTargetCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));
        spinnerTargetPriority.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorities));

        boolean isEdit = ruleToEdit != null;
        if (isEdit) {
            tvDialogTitle.setText("Edit Custom Rule");
            etRuleName.setText(ruleToEdit.getRuleName());
            etRuleKeyword.setText(ruleToEdit.getKeyword());
            setSpinnerSelection(spinnerTargetCategory, categories, ruleToEdit.getTargetCategory());
            setSpinnerSelection(spinnerTargetPriority, priorities, ruleToEdit.getTargetPriority());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnCancelRule.setOnClickListener(v -> dialog.dismiss());

        btnSaveRule.setOnClickListener(v -> {
            String name = etRuleName.getText().toString().trim();
            String keyword = etRuleKeyword.getText().toString().trim();
            String category = spinnerTargetCategory.getSelectedItem().toString();
            String priority = spinnerTargetPriority.getSelectedItem().toString();

            if (name.isEmpty() || keyword.isEmpty()) {
                Toast.makeText(this, "Please enter rule name and keyword", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("ruleName", name);
                cv.put("keyword", keyword);
                cv.put("targetCategory", category);
                cv.put("targetPriority", priority);

                if (isEdit) {
                    db.update("custom_rules", cv, "id = ?", new String[]{String.valueOf(ruleToEdit.getId())});
                    Toast.makeText(this, "Rule updated", Toast.LENGTH_SHORT).show();
                } else {
                    cv.put("isEnabled", 1);
                    cv.put("ruleOrder", dbHelper.getAllRules().size() + 1);
                    db.insert("custom_rules", null, cv);
                    Toast.makeText(this, "Rule created", Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
                refreshRules();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to save rule", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void setSpinnerSelection(Spinner spinner, String[] items, String value) {
        for (int i = 0; i < items.length; i++) {
            if (items[i].equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void refreshRules() {
        List<RuleModel> rules = dbHelper.getAllRules();
        adapter.updateList(rules);
        updateEmptyState(rules);
    }

    private void updateEmptyState(List<RuleModel> list) {
        if (list.isEmpty()) {
            rvRules.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvRules.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }
}
