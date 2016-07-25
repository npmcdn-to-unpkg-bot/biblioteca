package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.avaje.ebeaninternal.server.lib.util.Str;
import play.data.format.Formats;
import play.data.validation.Constraints;
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

    @Column(nullable = false, length = 400)
    private String resumo;

    @Column(nullable = false, length = 400)
    private String url;

    @Formats.DateTime(pattern="YYYY-MM-DD")
    private Date dataCadastro;

    @Formats.DateTime(pattern="YYYY-MM-DD")
    private Date dataAlteracao;

    @Column(nullable = false, length = 150)
    private String nomeCapa;

    public Publicacao() {
    }

    public Publicacao(Long id, String titulo, String resumo, String url, String nomeCapa) {
        this.setId(id);
        this.setTitulo(titulo);
        this.setResumo(resumo);
        this.setUrl(url);
        this.setNomeCapa(nomeCapa);

    }

    /**
     * @return a objeto video atraves da um formData onde o parametro FormData que validou os campos inputs
     * Cria uma instancia estatica do video passando por parametro o objeto formData com os dados preenchidos
     */
    public static Publicacao makeInstance(PublicacaoFormData formData) {
        Publicacao publicacao = new Publicacao();
        publicacao.setTitulo(formData.titulo);
        publicacao.setResumo(formData.resumo);
        publicacao.setUrl(formData.url);
        publicacao.setNomeCapa(formData.nomeCapa);
        return publicacao;
    }

    /**
     * Return a VideoFormData instance constructed from a video instance.
     * @param id The ID of a video instance.
     * @return The VideoFormData instance, or throws a RuntimeException.
     */
    public static PublicacaoFormData makePublicacaoFormData(Long id) {

        Publicacao publicacao = Ebean.find(Publicacao.class, id);

        if (publicacao == null) {
            throw new RuntimeException("Publicação não encontrado");
        }

        return new PublicacaoFormData(publicacao.titulo, publicacao.resumo, publicacao.url, publicacao.nomeCapa);
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public String getNomeCapa() {
        return nomeCapa;
    }

    public void setNomeCapa(String nomeCapa) {
        this.nomeCapa = nomeCapa;
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
