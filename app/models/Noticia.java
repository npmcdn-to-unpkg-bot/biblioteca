package models;

import com.avaje.ebean.Model;
import play.data.format.Formats;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Noticia extends Model {

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

    @Column(nullable = false, length = 250)
    private String nomeCapa;

    public Noticia() {}

    public Noticia(Long id, String titulo, String resumo, String url, String nomeCapa) {
        this.setId(id);
        this.setTitulo(titulo);
        this.setResumo(resumo);
        this.setUrl(url);
        this.setNomeCapa(nomeCapa);
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
}
