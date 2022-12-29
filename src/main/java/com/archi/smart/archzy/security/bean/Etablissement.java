package com.archi.smart.archzy.security.bean;

import com.archi.smart.archzy.audit.AuditBusinessObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.*;


/**
 * Etablissement
 *
 * @author JAF
 * @version 1.2
 */

@Entity
@Table(name = "etablissement_")
@GenericGenerator(name = "etablissementSequenceGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "etablissement_seq"), @Parameter(name = "increment_size", value = "1")})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Etablissement extends AuditBusinessObject {

    /**
     * Fields.
     */

    /**
     * Code
     */
    private String code;

    /**
     * Actif
     */
    private boolean actif = false;

    /**
     * Hl7
     */
    private String hl7;

    /**
     * Ordre
     */
    private Long ordre;


    /**
     * Constructeur par défaut.
     */
    public Etablissement() {
        super();
    }

    /**
     * Constructeur.
     *
     * @param id clé primaire
     */
    public Etablissement(Long id) {
        super(id);
    }

    /**
     * Methods.
     */

    @Column(length = 255)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(columnDefinition = "boolean default false")
    @Type(type = "boolean")
    public boolean isActif() {
        return this.actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    @Column(length = 255)
    public String getHl7() {
        return this.hl7;
    }

    public void setHl7(String hl7) {
        this.hl7 = hl7;
    }

    public Long getOrdre() {
        return this.ordre;
    }

    public void setOrdre(Long ordre) {
        this.ordre = ordre;
    }


    @Id
    @Column(name = "id_etablissement")
    @GeneratedValue(generator = "etablissementSequenceGenerator")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public String getLabel() {
        label = super.getLabel();
        return label;
    }


}