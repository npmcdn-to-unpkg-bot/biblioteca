package models;

import akka.util.Crypt;
import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.libs.Json;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Usuario extends Model {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String confirmacaoToken;

    @Formats.NonEmpty
    private Boolean validado = false;

    @Constraints.Required
    @Column(nullable = false, length = 60)
    private String nome;

    @Constraints.Required
    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false, length = 60)
    @JsonIgnore
    private String senha;

    @Column(nullable = false)
    private Integer privilegio;

    @Column(nullable = false)
    @JsonIgnore
    private Boolean status;

    @Formats.DateTime(pattern="dd-MM-yyyy")
    private Date dataCadastro;

    @Formats.DateTime(pattern="dd-MM-yyyy")
    private Date dataAlteracao;

    @JsonIgnore
    public Boolean getStatus() {
        return status;
    }

    @JsonProperty
    public void setStatus(Boolean status) {
        this.status = status;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getSenha() {
        return senha;
    }

    @JsonProperty
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Integer getPrivilegio() {
        return privilegio;
    }

    public void setPrivilegio(Integer privilegio) {
        this.privilegio = privilegio;
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

    public String getConfirmacaoToken() {
        return confirmacaoToken;
    }

    public void setConfirmacaoToken(String confirmacaoToken) {
        this.confirmacaoToken = confirmacaoToken;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    /**
     * Confirms an account.
     *
     * @return true if confirmed, false otherwise
     */
    public boolean confirmado(Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        usuario.setValidado(true);
        usuario.save();
        return true;
    }

    /**
     * Change password account
     *
     * @param senha
     * save new password
     */
    public void mudarSenha(String senha) {
        this.senha = Crypt.sha1(senha);
        this.dataAlteracao = new Date();
        this.save();
    }

    public static Finder<Long, Usuario> find = new Finder<>(Usuario.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<>();
        for (Usuario c : Usuario.find.orderBy("nome").findList()) {
            options.put(c.id.toString(),c.nome);
        }
        return options;
    }

    @Override
    public String toString() {
        return Json.toJson(this).toString();
    }

}
