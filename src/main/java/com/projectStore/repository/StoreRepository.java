package com.projectStore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectStore.entity.Store;
import com.projectStore.entity.User;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    List<Store> findByAdmin(User user);

}
