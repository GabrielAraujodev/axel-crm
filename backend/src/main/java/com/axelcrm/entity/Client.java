package com.axelcrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.axelcrm.commons.entity.BaseEntity;
import com.axelcrm.auth.entity.User;

/**
 * A converted lead or manually created customer.
 */
@Entity
@Table(name = "clients")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Client extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String email;

    private String phone;

    @Column(name = "document")
    private String document;

    @Column(name = "company_name")
    private String companyName;

    private String website;

    private String address;

    private String city;

    private String state;

    @Column(name = "zip_code")
    private String zipCode;

    private String country;

    private String industry;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private com.axelcrm.entity.enums.ClientStatus status = com.axelcrm.entity.enums.ClientStatus.ACTIVE;

    @Column(name = "service_type", length = 50)
    private String serviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @OneToMany(mappedBy = "client")
    private List<Contact> contacts = new ArrayList<>();
}
