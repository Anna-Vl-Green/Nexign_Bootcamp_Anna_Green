package ru.test.nexigntest.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Сущность абонентского вызова
 */
@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCall {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Long customerId;
    private String callType;
    private Long startTime;
    private Long endTime;
}
