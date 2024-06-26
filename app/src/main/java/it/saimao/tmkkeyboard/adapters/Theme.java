package it.saimao.tmkkeyboard.adapters;

import androidx.annotation.DrawableRes;

import java.util.Objects;

public class Theme {
    String name;
    @DrawableRes
    int resource;
    boolean selected = false;

    public Theme(String name, int resource, boolean selected) {
        this.name = name;
        this.resource = resource;
        this.selected = selected;
    }

    public Theme(Theme theme) {
        this.name = theme.name;
        this.resource = theme.resource;
        this.selected = theme.selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
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
        return resource == theme.resource && selected == theme.selected && Objects.equals(name, theme.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, resource, selected);
    }

    @Override
    public String toString() {
        return "Theme{" +
                "name='" + name + '\'' +
                ", resource=" + resource +
                ", selected=" + selected +
                '}';
    }
}