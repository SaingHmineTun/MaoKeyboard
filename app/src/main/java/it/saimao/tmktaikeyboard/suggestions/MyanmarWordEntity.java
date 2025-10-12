package it.saimao.tmktaikeyboard.suggestions;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "myanmar_words", primaryKeys = {"word"})
public class MyanmarWordEntity {
    @ColumnInfo(name = "word")
    @NonNull
    public String word;

    public MyanmarWordEntity(String word) {
        this.word = word;
    }
}