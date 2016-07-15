package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Escolaridade extends Model {

    @Id
    private Long id;

    @Column(nullable = false, length = 60)
    private String nome;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Return the GradeLevel instance in the database with name 'levelName' or null if not found.
     * @param nome The Level name.
     * @return The GradeLevel instance, or null if not found.
     */
    public static Escolaridade findEscolaridade(String nome) {
        for (Escolaridade escolaridade : Ebean.find(Escolaridade.class).findList()) {
            if (nome.equals(escolaridade.getNome())) {
                return escolaridade;
            }
        }
        return null;
    }
}
