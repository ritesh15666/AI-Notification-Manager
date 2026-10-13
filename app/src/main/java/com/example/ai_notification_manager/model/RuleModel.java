package com.example.ai_notification_manager.model;

import java.io.Serializable;

public class RuleModel implements Serializable {
    private int id;
    private String ruleName;
    private String keyword;
    private String targetCategory;
    private String targetPriority;
    private boolean isEnabled;
    private int ruleOrder;

    public RuleModel(int id, String ruleName, String keyword, String targetCategory, String targetPriority, boolean isEnabled, int ruleOrder) {
        this.id = id;
        this.ruleName = ruleName;
        this.keyword = keyword;
        this.targetCategory = targetCategory;
        this.targetPriority = targetPriority;
        this.isEnabled = isEnabled;
        this.ruleOrder = ruleOrder;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getTargetCategory() { return targetCategory; }
    public void setTargetCategory(String targetCategory) { this.targetCategory = targetCategory; }

    public String getTargetPriority() { return targetPriority; }
    public void setTargetPriority(String targetPriority) { this.targetPriority = targetPriority; }

    public boolean isEnabled() { return isEnabled; }
    public void setEnabled(boolean enabled) { isEnabled = enabled; }

    public int getRuleOrder() { return ruleOrder; }
    public void setRuleOrder(int ruleOrder) { this.ruleOrder = ruleOrder; }
}
