package com.auth.repository;

import com.auth.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Long> {

    Page<Story> findByTitleContainingIgnoreCase(Pageable pageable, String keyword);

}
