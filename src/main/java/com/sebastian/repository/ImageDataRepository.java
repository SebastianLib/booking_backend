package com.sebastian.repository;

import com.sebastian.model.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
public interface ImageDataRepository extends JpaRepository<ImageData, Integer> {
    @Query("SELECT i FROM ImageData i WHERE i.filePath LIKE %:fileName%")
    Optional<ImageData> findByFilePathContaining(@Param("fileName") String fileName);
}
