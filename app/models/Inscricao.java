package models;


import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import play.data.format.Formats;
import play.libs.Json;
import views.validators.InscricaoFormData;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
public class Inscricao extends Model {

    @Id
    private Long id;

    @Column(nullable = false, length = 60)
    private String nome;

    @ManyToOne
    private Genero genero;

    @Column(nullable = false)
    @Formats.DateTime(pattern = "dd-MM-yyyy")
    private Date dataNascimento;

    @ManyToOne
    private Escolaridade escolaridade;

    @Column(nullable = false, length = 30)
    private String profissao;

    @Column(nullable = false, length = 45)
    private String instituicao;

    @Column(nullable = false, length = 45)
    private String pais;

    @Column(nullable = false, length = 45)
    private String estado;

    @Column(nullable = false, length = 45)
    private String cidade;

    @Column(nullable = false, length = 12)
    private String telefone;

    @Column(nullable = false, length = 15)
    private String cpf;

    @Column(nullable = false, length = 35)
    private String email;

    @Column(nullable = false, length = 3)
    private String modalidade;

    @Column(nullable = false, length = 25)
    private String fonte;

    @Column(length = 60)
    private String descricaoFonte;

    @Formats.DateTime(pattern="dd-MM-yyyy")
    private Date dataInscricao;

    @Formats.DateTime(pattern="dd-MM-yyyy")
    private Date dataAlteracao;

    public Inscricao(){
    }

    public Inscricao(Long id,
                     String nome,
                     Genero genero,
                     Date dataNascimento,
                     Escolaridade escolaridade,
                     String profissao,
                     String instituicao,
                     String pais,
                     String estado,
                     String cidade,
                     String telefone,
                     String cpf,
                     String email,
                     String modalidade,
                     String fonte,
                     String descricaoFonte) {
        this.setId(id);
        this.setNome(nome);
        this.setGenero(genero);
        this.setDataNascimento(dataNascimento);
        this.setEscolaridade(escolaridade);
        this.setProfissao(profissao);
        this.setInstituicao(instituicao);
        this.setPais(pais);
        this.setEstado(estado);
        this.setCidade(cidade);
        this.setTelefone(telefone);
        this.setCpf(cpf);
        this.setEmail(email);
        this.setModalidade(modalidade);
        this.setFonte(fonte);
        this.setDescricaoFonte(descricaoFonte);
    }

    /**
     * @return a objeto livro atraves da um formData onde o parametro FormData que validou os campos inputs
     */
    public static Inscricao makeInstance(InscricaoFormData formData) {
        Inscricao inscricao = new Inscricao();
        inscricao.setNome(formData.nome);
        inscricao.setGenero(Genero.findGenero(formData.genero));
        inscricao.setDataNascimento(formData.dataNascimento);
        inscricao.setEscolaridade(Escolaridade.findEscolaridade(formData.escolaridade));
        inscricao.setProfissao(formData.profissao);
        inscricao.setInstituicao(formData.instituicao);
        inscricao.setPais(formData.pais);
        inscricao.setEstado(formData.estado);
        inscricao.setCidade(formData.cidade);
        inscricao.setTelefone(formData.telefone);
        inscricao.setCpf(formData.cpf);
        inscricao.setEmail(formData.email);
        inscricao.setModalidade(formData.modalidade);
        inscricao.setFonte(formData.fonte);
        inscricao.setDescricaoFonte(formData.descricaoFonte);

        return inscricao;
    }

    /**
     * Return a LivroFormData instance constructed from a livro instance.
     * @param id The ID of a livro instance.
     * @return The LivroFormData instance, or throws a RuntimeException.
     */
    public static InscricaoFormData makeInscricaoFormData(Long id) {

        Inscricao inscricao = Ebean.find(Inscricao.class, id);

        if (inscricao == null) {
            throw new RuntimeException("Inscrição não encontrada");
        }

        return new InscricaoFormData(inscricao.nome,
                inscricao.genero,
                inscricao.dataNascimento,
                inscricao.escolaridade,
                inscricao.profissao,
                inscricao.instituicao,
                inscricao.pais,
                inscricao.estado,
                inscricao.cidade,
                inscricao.telefone,
                inscricao.cpf,
                inscricao.email,
                inscricao.modalidade,
                inscricao.fonte,
                inscricao.descricaoFonte);
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

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Escolaridade getEscolaridade() {
        return escolaridade;
    }

    public void setEscolaridade(Escolaridade escolaridade) {
        this.escolaridade = escolaridade;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public String getInstituicao() {
        return instituicao;
    }

    public void setInstituicao(String instituicao) {
        this.instituicao = instituicao;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getDescricaoFonte() {
        return descricaoFonte;
    }

    public void setDescricaoFonte(String descricaoFonte) {
        this.descricaoFonte = descricaoFonte;
    }

    public Date getDataInscricao() {
        return dataInscricao;
    }

    public void setDataInscricao(Date dataInscricao) {
        this.dataInscricao = dataInscricao;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public static Finder<Long, Inscricao> find = new Finder<>(Inscricao.class);

    public static Map<String,String> options() {
        LinkedHashMap<String,String> options = new LinkedHashMap<>();
        for (Inscricao c : Inscricao.find.orderBy("nome").findList()) {
            options.put(c.id.toString(),c.nome);
        }
        return options;
    }

    @Override
    public String toString() {
        return Json.toJson(this).toString();
    }
}
