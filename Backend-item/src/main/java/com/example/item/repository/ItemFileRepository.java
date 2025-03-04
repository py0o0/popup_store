package com.example.item.repository;

import com.example.item.entity.ItemFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemFileRepository extends JpaRepository<ItemFile, Long> {
    @Query("SELECT itemFile.itemFile FROM ItemFile itemFile WHERE itemFile.itemId = :itemId")
    List<String> findByItemId(long itemId);

    @Query("SELECT itemFile FROM ItemFile itemFile WHERE itemFile.itemId = :itemId")
    List<ItemFile> findItemFileByItemId(long itemId);
}
