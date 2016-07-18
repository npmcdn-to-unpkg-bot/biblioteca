package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;

@Entity
public class Fonte extends Model {

    @Id
    private Long id;

    @Column(nullable = false, length = 30)
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
    public static Fonte findFonte(String nome) {
        for (Fonte fonte : Ebean.find(Fonte.class).findList()) {
            if (nome.equals(fonte.getNome())) {
                return fonte;
            }
        }
        return null;
    }

    /**
     * Provide a list of names for use in form display.
     * @return A list of level names in sorted order.
     */
    public static List<String> getNameList() {
        String[] nameArray = {"Site do CIBiogás-ER",
                "Indicação de amigos",
                "Facebook",
                "Email",
                "Outro"};
        return Arrays.asList(nameArray);
    }
}
