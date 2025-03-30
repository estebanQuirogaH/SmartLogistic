package com.projectStore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectStore.entity.RoleEntity;

@Repository
public interface StoreRepository extends JpaRepository<RoleEntity, Integer> {

}
