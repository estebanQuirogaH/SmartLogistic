package com.projectStore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectStore.entity.Audit;
import com.projectStore.entity.Store;
import com.projectStore.entity.User;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
    List<Audit> findByUserOrderByTimestampDesc(User user);

    List<Audit> findByStoreOrderByTimestampDesc(Store store);

    List<Audit> findAllByOrderByTimestampDesc();
}
