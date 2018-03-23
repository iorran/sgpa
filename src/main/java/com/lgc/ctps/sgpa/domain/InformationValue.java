package com.lgc.ctps.sgpa.domain;


import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A InformationValue.
 */
@Entity
@Table(name = "information_value")
public class InformationValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "jhi_value", nullable = false)
    private String value;

    @ManyToOne
    private Information information;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public InformationValue value(String value) {
        this.value = value;
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Information getInformation() {
        return information;
    }

    public InformationValue information(Information information) {
        this.information = information;
        return this;
    }

    public void setInformation(Information information) {
        this.information = information;
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
        InformationValue informationValue = (InformationValue) o;
        if (informationValue.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), informationValue.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "InformationValue{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            "}";
    }
}
