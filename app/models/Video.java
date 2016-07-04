package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.libs.Json;
import views.validators.VideoFormData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Video extends Model {

    @Id
    private Long id;

    @Constraints.Required
    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 400)
    private String descricao;

    @Column(nullable = false, length = 400)
    private String url;

    @Formats.DateTime(pattern="dd-MM-yyyy")
    private Date dataCadastro;

    @Formats.DateTime(pattern="dd-MM-yyyy")
    private Date dataAlteracao;

    public Video(){}

    public Video(Long id, String titulo, String descricao, String url) {
        this.setId(id);
        this.setTitulo(titulo);
        this.setDescricao(descricao);
        this.setUrl(url);
    }

    /**
     * @return a objeto video atraves da um formData onde o parametro FormData que validou os campos inputs
     * Cria uma instancia estatica do video passando por parametro o objeto formData com os dados preenchidos
     */
    public static Video makeInstance(VideoFormData formData) {
        Video video = new Video();
        video.setTitulo(formData.titulo);
        video.setDescricao(formData.descricao);
        video.setUrl(formData.url);
        return video;
    }

    /**
     * Return a VideoFormData instance constructed from a video instance.
     * @param id The ID of a video instance.
     * @return The VideoFormData instance, or throws a RuntimeException.
     */
    public static VideoFormData makeVideoFormData(Long id) {

        Video video = Ebean.find(Video.class, id);

        if (video == null) {
            throw new RuntimeException("Video não encontrado");
        }

        return new VideoFormData(video.titulo, video.descricao, video.url);
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public static Finder<Long, Video> find = new Finder<>(Video.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<>();
        for (Video v : Video.find.orderBy("titulo").findList()) {
            options.put(v.id.toString(),v.titulo);
        }
        return options;
    }

    @Override
    public String toString() {
        return Json.toJson(this).toString();
    }

}
