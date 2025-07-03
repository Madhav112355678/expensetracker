package com.example.trackmymoney;

public class CategoryItem {
    private String categoryName;
    private int iconResId;

    public CategoryItem(String categoryName, int iconResId) {
        this.categoryName = categoryName;
        this.iconResId = iconResId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getIconResId() {
        return iconResId;
    }
}
