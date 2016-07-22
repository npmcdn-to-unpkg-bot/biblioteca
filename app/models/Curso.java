package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.libs.Json;
import views.validators.CursoFormData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Curso extends Model {

    @Id
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 400)
    private String descricao;

    @Column(nullable = false)
    @Formats.DateTime(pattern="YYYY-MM-DD")
    private Date dataInicio;

    @Column(nullable = false, length = 80)
    private String site;

    @Column(nullable = false, length = 150)
    private String nomeCapa;

    public Curso(){
    }

    public Curso(Long id, String nome, String descricao, Date dataInicio, String site, String nomeCapa) {
        this.setId(id);
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.site = site;
        this.nomeCapa = nomeCapa;
    }

    /**
     * @return a objeto curso atraves da um formData onde o parametro FormData que validou os campos inputs
     */
    public static Curso makeInstance(CursoFormData formData) {
        Curso curso = new Curso();
        curso.setNome(formData.nome);
        curso.setDescricao(formData.descricao);
        curso.setDataInicio(formData.dataInicio);
        curso.setSite(formData.site);
        curso.setNomeCapa(formData.nomeCapa);
        return curso;
    }

    /**
     * Return a CursoFormData instance constructed from a evento instance.
     * @param id The ID of a evento instance.
     * @return The EventoFormData instance, or throws a RuntimeException.
     */
    public static CursoFormData makeCursoFormData(Long id) {

        Curso curso = Ebean.find(Curso.class, id);

        if (curso == null) {
            throw new RuntimeException("Curso não encontrado");
        }

        return new CursoFormData(curso.nome, curso.descricao, curso.dataInicio, curso.site, curso.nomeCapa);
    }

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

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getNomeCapa() {
        return nomeCapa;
    }

    public void setNomeCapa(String nomeCapa) {
        this.nomeCapa = nomeCapa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static Finder<Long, Curso> find = new Finder<>(Curso.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<>();
        for (Curso c : Curso.find.orderBy("nome").findList()) {
            options.put(c.id.toString(),c.nome);
        }
        return options;
    }

    @Override
    public String toString() {
        return Json.toJson(this).toString();
    }

}
