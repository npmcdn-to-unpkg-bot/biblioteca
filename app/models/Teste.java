package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Teste extends Model {

    @Id
    private Long id;

    @Constraints.Required
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

    public static Finder<Long, Teste> find = new Finder<>(Teste.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<>();
        for (Teste c : Teste.find.orderBy("nome").findList()) {
            options.put(c.id.toString(),c.nome);
        }
        return options;
    }
}
