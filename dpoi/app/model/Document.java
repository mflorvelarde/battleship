package model;

import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created by florenciavelarde on 4/5/16.
 */
@Entity
public class Document extends Model {
    @Id
    public Long id;


    public String domain;
    public String path;

    public static Finder<Long, Document> find = new Finder<Long, Document>(Document.class);
    }


}
