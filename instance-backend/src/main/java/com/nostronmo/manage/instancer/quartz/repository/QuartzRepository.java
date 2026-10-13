package com.nostronmo.manage.instancer.quartz.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nostronmo.manage.instancer.quartz.model.QuartzScheduler;

@Repository
public interface QuartzRepository
		extends JpaRepository<QuartzScheduler, UUID>, JpaSpecificationExecutor<QuartzScheduler> {

	@Query("SELECT q FROM QuartzScheduler q WHERE q.active = true")
	List<QuartzScheduler> findByActiveTrue();

	@Query("SELECT q FROM QuartzScheduler q WHERE q.active = true AND q.targetService = :targetService")
	List<QuartzScheduler> findByTargetServiceAndActiveTrue(@Param("targetService") String targetService);
	
	@Query("SELECT COUNT(q) > 0 FROM QuartzScheduler q WHERE q.name = :name")
	boolean existsByName(@Param("name") String name);

	@Query("SELECT COUNT(q) > 0 FROM QuartzScheduler q WHERE q.targetService = :targetService")
	boolean existsByTargetService(@Param("targetService") String targetService);

}
