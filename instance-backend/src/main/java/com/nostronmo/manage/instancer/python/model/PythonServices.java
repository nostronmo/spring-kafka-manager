package com.nostronmo.manage.instancer.python.model;

import java.util.Map;
import java.util.UUID;

import com.nostronmo.manage.instancer.global.Auditable;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Table(name = "python_services")
public class PythonServices extends Auditable {

	@Id
	@ToString.Include
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "service_name", nullable = false)
	private String service;

	@Column(name = "service_state", nullable = false)
	private String serviceState;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "service_checkpoint", joinColumns = @JoinColumn(name = "service_id"))
	@MapKeyColumn(name = "checkpoint_name", nullable = true)
	private Map<String, ServiceState> checkpoints;

	@Column(name = "uptime", nullable = false)
	private String uptime;

	@Column(name = "status", nullable = false)
	private String status;

	@Column(name = "framework", nullable = false)
	private String framework;

}
