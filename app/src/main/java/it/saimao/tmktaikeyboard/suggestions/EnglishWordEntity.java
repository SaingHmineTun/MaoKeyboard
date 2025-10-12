package it.saimao.tmktaikeyboard.suggestions;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "english_words", primaryKeys = {"word"})
public class EnglishWordEntity {
    @ColumnInfo(name = "word")
    @NonNull
    public String word;

    public EnglishWordEntity(String word) {
        this.word = word;
    }
}