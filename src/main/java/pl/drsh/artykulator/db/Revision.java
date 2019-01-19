/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.drsh.artykulator.db;

import java.io.Serializable;
import java.util.Date;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author drsh
 */
@Entity
@Table(name = "REVISIONS", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Revision.findAll", query = "SELECT r FROM Revision r")
    , @NamedQuery(name = "Revision.findByArticleId", query = "SELECT r FROM Revision r WHERE r.revisionPK.articleId = :articleId")
    , @NamedQuery(name = "Revision.findById", query = "SELECT r FROM Revision r WHERE r.revisionPK.id = :id")
    , @NamedQuery(name = "Revision.findByDateChanged", query = "SELECT r FROM Revision r WHERE r.dateChanged = :dateChanged")})
public class Revision implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected RevisionPK revisionPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DATE_CHANGED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateChanged;
    @JoinColumn(name = "ARTICLE_ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    @JsonbTransient
    private Article article;
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private User userId;

    public Revision() {
    }

    public Revision(RevisionPK revisionPK) {
        this.revisionPK = revisionPK;
    }

    public Revision(RevisionPK revisionPK, Date dateChanged, User userId) {
        this.revisionPK = revisionPK;
        this.dateChanged = dateChanged;
        this.userId = userId;
    }
    
    public Revision(int articleId, int id) {
        this.revisionPK = new RevisionPK(articleId, id);
    }

    public RevisionPK getRevisionPK() {
        return revisionPK;
    }

    public void setRevisionPK(RevisionPK revisionPK) {
        this.revisionPK = revisionPK;
    }

    public Date getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(Date dateChanged) {
        this.dateChanged = dateChanged;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (revisionPK != null ? revisionPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Revision)) {
            return false;
        }
        Revision other = (Revision) object;
        if ((this.revisionPK == null && other.revisionPK != null) || (this.revisionPK != null && !this.revisionPK.equals(other.revisionPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.drsh.artykulator.db.Revision[ revisionPK=" + revisionPK + " ]";
    }
    
}
