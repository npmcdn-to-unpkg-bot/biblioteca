package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.libs.Json;
import views.validators.PublicacaoFormData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Publicacao extends Model {

    @Id
    private Long id;

    @Column(nullable = false, length = 250)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String resumo;

    @Column(nullable = false, length = 350)
    private String nomeCapa;

    @Column(nullable = false, length = 400)
    private String url;

    @Column(nullable = false)
    @Formats.DateTime(pattern="YYYY-MM-DD")
    private Date dataCadastro;


    public Publicacao(){
    }

    public Publicacao(Long id, String titulo, String resumo, String nomeCapa, String url) {
        this.setId(id);
        this.titulo = titulo;
        this.resumo = resumo;
        this.nomeCapa = nomeCapa;
        this.url = url;
    }

    /**
     * @return a objeto publicacao atraves da um formData onde o parametro FormData que tem os campos inputs validados
     */
    public static Publicacao makeInstance(PublicacaoFormData formData) {
        Publicacao publicacao = new Publicacao();
        publicacao.setTitulo(formData.titulo);
        publicacao.setResumo(formData.resumo);
        publicacao.setNomeCapa(formData.nomeCapa);
        publicacao.setUrl(formData.url);
        return publicacao;
    }

    /**
     * Return a PublicacaoFormData instance constructed from a publicacao instance.
     * @param id The ID of a livro instance.
     * @return The PublicacaoFormData instance, or throws a RuntimeException.
     */
    public static PublicacaoFormData makePublicacaoFormData(Long id) {

        Publicacao publicacao = Ebean.find(Publicacao.class, id);

        if (publicacao == null) {
            throw new RuntimeException("Publicação não encontrada");
        }

        return new PublicacaoFormData(publicacao.titulo, publicacao.resumo, publicacao.nomeCapa, publicacao.url);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumo() {
        return resumo;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public String getNomeCapa() {
        return nomeCapa;
    }

    public void setNomeCapa(String nomeCapa) {
        this.nomeCapa = nomeCapa;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static Finder<Long, Publicacao> find = new Finder<>(Publicacao.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<>();
        for (Publicacao p : Publicacao.find.orderBy("titulo").findList()) {
            options.put(p.id.toString(),p.titulo);
        }
        return options;
    }

    @Override
    public String toString() {
        return Json.toJson(this).toString();
    }
}
