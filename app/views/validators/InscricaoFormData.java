package views.validators;

import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InscricaoFormData {

    public String nome = "";
    public char genero = '\0';
    public Date dataNascimento = null;
    public String escolaridade = "";
    public String profissao = "";
    public String instituicao = "";
    public String pais = "";
    public String estado = "";
    public String cidade = "";
    public String telefone = "";
    public String cpf = "";
    public String email = "";
    public String modalidade = "";
    public String fonte = "";
    public String descricaoFonte = "";

    /** Necessario para instanciar o form */
    public InscricaoFormData() {
    }

    public InscricaoFormData(String nome, char genero, Date dataNascimento, String escolaridade, String profissao, String instituicao, String pais, String estado, String cidade, String telefone, String cpf, String email, String modalidade, String fonte, String descricaoFonte) {
        this.nome = nome;
        this.genero = genero;
        this.dataNascimento = dataNascimento;
        this.escolaridade = escolaridade;
        this.profissao = profissao;
        this.instituicao = instituicao;
        this.pais = pais;
        this.estado = estado;
        this.cidade = cidade;
        this.telefone = telefone;
        this.cpf = cpf;
        this.email = email;
        this.modalidade = modalidade;
        this.fonte = fonte;
        this.descricaoFonte = descricaoFonte;
    }

    public List<ValidationError> validate() {

        List<ValidationError> errors = new ArrayList<>();

        if (nome == null || nome.length() == 0) {
            errors.add(new ValidationError("nome", "Preencha o nome"));
        }

        if (genero == '\0') {
            errors.add(new ValidationError("genero", "Preencha o Gênero"));
        }

        if (dataNascimento == null) {
            errors.add(new ValidationError("dataNascimento", "Preencha a data de nascimento"));
        }

        if (escolaridade == null || escolaridade.length() == 0) {
            errors.add(new ValidationError("escolaridade", "Preencha a escolaridade"));
        }

        if (profissao == null || profissao.length() == 0) {
            errors.add(new ValidationError("profissao", "Preencha a profissão"));
        }

        if (instituicao == null || instituicao.length() == 0) {
            errors.add(new ValidationError("instituicao", "Preencha a instituição"));
        }

        if (pais == null || pais.length() == 0) {
            errors.add(new ValidationError("pais", "Preencha o país"));
        }

        if (estado == null || estado.length() == 0) {
            errors.add(new ValidationError("estado", "Preencha o estado"));
        }

        if (cidade == null || cidade.length() == 0) {
            errors.add(new ValidationError("cidade", "Preencha o cidade"));
        }

        if (telefone == null || telefone.length() == 0) {
            errors.add(new ValidationError("telefone", "Preencha o telefone"));
        }

        if (cpf == null || cpf.length() == 0) {
            errors.add(new ValidationError("cpf", "Preencha o cpf"));
        }

        if (email == null || email.length() == 0) {
            errors.add(new ValidationError("email", "Preencha o email"));
        }

        if (modalidade == null || modalidade.length() == 0) {
            errors.add(new ValidationError("modalidade", "Preenca este campo"));
        }
        if (fonte == null || fonte.length() == 0) {
            errors.add(new ValidationError("fonte", "Preencha a fonte"));
        }

        return errors.isEmpty() ? null : errors;
    }
}
