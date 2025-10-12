package it.saimao.tmktaikeyboard.suggestions;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;


@Dao
public interface ShanWordDao {
    @Query("SELECT * FROM shan_words WHERE word LIKE :prefix || '%' ORDER BY word LIMIT 10")
    List<ShanWordEntity> getSuggestions(String prefix);
}