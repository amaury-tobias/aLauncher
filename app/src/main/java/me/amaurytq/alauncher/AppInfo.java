package me.amaurytq.alauncher;

class AppInfo {
    private CharSequence label;
    private CharSequence packageName;

    CharSequence getLabel() {
        return label;
    }

    void setLabel(CharSequence label) {
        this.label = label;
    }

    CharSequence getPackageName() {
        return packageName;
    }

    void setPackageName(CharSequence packageName) {
        this.packageName = packageName;
    }
}
