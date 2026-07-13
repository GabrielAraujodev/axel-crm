package com.axelcrm.repository;

import com.axelcrm.entity.DealStageHistory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealStageHistoryRepository extends JpaRepository<DealStageHistory, UUID> {
    List<DealStageHistory> findByDeal_IdAndDeletedAtIsNullOrderByEnteredAtAsc(UUID dealId);
    Optional<DealStageHistory> findFirstByDeal_IdAndLeftAtIsNullAndDeletedAtIsNull(UUID dealId);
}
