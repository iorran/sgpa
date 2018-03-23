package com.lgc.ctps.sgpa.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

import com.lgc.ctps.sgpa.domain.enumeration.Access;

/**
 * A Permission.
 */
@Entity
@Table(name = "permission")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "jhi_value", nullable = false)
    private Access value;

    @ManyToOne
    private UseCase useCase;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Access getValue() {
        return value;
    }

    public Permission value(Access value) {
        this.value = value;
        return this;
    }

    public void setValue(Access value) {
        this.value = value;
    }

    public UseCase getUseCase() {
        return useCase;
    }

    public Permission useCase(UseCase useCase) {
        this.useCase = useCase;
        return this;
    }

    public void setUseCase(UseCase useCase) {
        this.useCase = useCase;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Permission permission = (Permission) o;
        if (permission.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), permission.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Permission{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            "}";
    }
}
