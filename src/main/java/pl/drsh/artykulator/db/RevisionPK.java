/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.drsh.artykulator.db;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author drsh
 */
@Embeddable
public class RevisionPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "article_id")
    private int articleId;
    @Basic(optional = false)
    @Column(name = "id")
    private int id;

    public RevisionPK() {
    }

    public RevisionPK(int articleId, int id) {
        this.articleId = articleId;
        this.id = id;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) articleId;
        hash += (int) id;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RevisionPK)) {
            return false;
        }
        RevisionPK other = (RevisionPK) object;
        if (this.articleId != other.articleId) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pl.drsh.artykulator.db.RevisionPK[ articleId=" + articleId + ", id=" + id + " ]";
    }
    
}
