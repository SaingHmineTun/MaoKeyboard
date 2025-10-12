package it.saimao.tmktaikeyboard.suggestions;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "shan_words", primaryKeys = {"word"})
public class ShanWordEntity {
    @ColumnInfo(name = "word")
    @NonNull
    public String word;

    public ShanWordEntity(String word) {
        this.word = word;
    }
}