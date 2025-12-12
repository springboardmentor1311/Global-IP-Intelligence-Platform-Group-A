
package com.ipintelligence.repo;

import com.ipintelligence.model.SearchHistory;
import com.ipintelligence.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    Page<SearchHistory> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<SearchHistory> findTop10ByUserOrderByCreatedAtDesc(User user);

    List<SearchHistory> findByUserAndCreatedAtBetween(User user, LocalDateTime startDate, LocalDateTime endDate);

    void deleteByUserAndCreatedAtBefore(User user, LocalDateTime cutoffDate);

    long countByUser(User user);
}