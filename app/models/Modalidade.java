package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.List;

@Entity
public class Modalidade extends Model {

    @Id
    private Long id;

    @Column(nullable = false, length = 3)
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
    public static Modalidade findModalidade(String nome) {
        for (Modalidade modalidade : Ebean.find(Modalidade.class).findList()) {
            if (nome.equals(modalidade.getNome())) {
                return modalidade;
            }
        }
        return null;
    }

    /**
     * Provide a list of names for use in form display.
     * @return A list of level names in sorted order.
     */
    public static List<String> getNameList() {
        String[] nameArray = {"Sim", "Não"};
        return Arrays.asList(nameArray);
    }
}
