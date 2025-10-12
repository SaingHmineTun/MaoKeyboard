package it.saimao.tmktaikeyboard.suggestions;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EnglishWordDao {
    @Query("SELECT * FROM english_words WHERE word LIKE :prefix || '%' ORDER BY word LIMIT 10")
    List<EnglishWordEntity> getSuggestions(String prefix);
}

