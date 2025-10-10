package it.saimao.tmktaikeyboard.adapters;

import androidx.annotation.DrawableRes;

import java.util.Objects;

public class Theme {
    String name;
    boolean selected;

    public Theme(String name) {
        this.name = name;
        this.selected = false;
    }

    public Theme(Theme theme) {
        this.name = theme.name;
        this.selected = theme.selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Theme theme = (Theme) o;
        return Objects.equals(name, theme.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public interface OnThemeClickListener {
        void onThemeClicked(Theme theme);
    }
}
