package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.avaje.ebeaninternal.server.lib.util.Str;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.libs.Json;
import views.validators.LivroFormData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.*;

@Entity
public class Livro extends Model {

    @Id
    private Long id;

    @Constraints.Required
    @Column(nullable = false, length = 150)
    private String titulo;

    @Constraints.Required
    @Column(nullable = false, length = 254)
    private String subTitulo;

    @Constraints.Required
    @Column(unique = true, length = 17)
    private String isbn;

    @Constraints.Required
    @Column(nullable = false, length = 100)
    private String editora;

    @Constraints.Required
    @Column(nullable = false, length = 254)
    private String autores;

    @Constraints.Required
    private Integer edicao;

    @Constraints.Required
    private Integer paginas;

    @Constraints.Required
    private Integer ano;

    @Formats.DateTime(pattern="dd-MM-yyyy")
    private Date dataCadastro;

    @Formats.DateTime(pattern="dd-MM-yyyy")
    private Date dataAlteracao;

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

    public String getSubTitulo() {
        return subTitulo;
    }

    public void setSubTitulo(String subTitulo) {
        this.subTitulo = subTitulo;
    }

    public Integer getPaginas() {
        return paginas;
    }

    public void setPaginas(Integer paginas) {
        this.paginas = paginas;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public String getAutores() {
        return autores;
    }

    public void setAutores(String autores) {
        this.autores = autores;
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

    public Integer getEdicao() {
        return edicao;
    }

    public void setEdicao(Integer edicao) {
        this.edicao = edicao;
    }

    public static Finder<Long, Livro> find = new Finder<>(Livro.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<>();
        for (Livro c : Livro.find.orderBy("titulo").findList()) {
            options.put(c.id.toString(),c.titulo);
        }
        return options;
    }

    /**
     * @return a objeto livro atraves da um formData onde o parametro FormData que validou os campos inputs
     */
    public static Livro makeInstance(LivroFormData formData) {
        Livro livro = new Livro();
        livro.setTitulo(formData.titulo);
        livro.setSubTitulo(formData.subTitulo);
        livro.setEdicao(formData.edicao);
        livro.setPaginas(formData.paginas);
        livro.setAno(formData.ano);
        livro.setAutores(formData.autores);
        livro.setEditora(formData.editora);
        livro.setIsbn(formData.isbn);
        return livro;
    }

    @Override
    public String toString() {
        return Json.toJson(this).toString();
    }

}
