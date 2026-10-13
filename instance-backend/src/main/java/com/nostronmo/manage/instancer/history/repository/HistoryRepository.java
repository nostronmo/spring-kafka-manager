package com.nostronmo.manage.instancer.history.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.nostronmo.manage.instancer.history.model.History;

@Repository
public interface HistoryRepository extends JpaRepository<History, UUID>, JpaSpecificationExecutor<History> {

}
