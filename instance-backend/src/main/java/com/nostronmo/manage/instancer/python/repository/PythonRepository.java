package com.nostronmo.manage.instancer.python.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.nostronmo.manage.instancer.python.model.PythonServices;

@Repository
public interface PythonRepository extends JpaRepository<PythonServices, UUID>, JpaSpecificationExecutor<PythonServices> {

}
