package views.validators;

import models.*;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static views.validators.ValidaCPF.isCPF;

public class InscricaoFormData {

    public String nome = "";
    public String genero = "";
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

    public InscricaoFormData(String nome, Genero genero, Date dataNascimento, Escolaridade escolaridade, String profissao, String instituicao, Pais pais, String estado, String cidade, String telefone, String cpf, String email, String modalidade, String fonte, String descricaoFonte) {
        this.nome = nome;
        this.genero = genero.getNome();
        this.dataNascimento = dataNascimento;
        this.escolaridade = escolaridade.getNome();
        this.profissao = profissao;
        this.instituicao = instituicao;
        this.pais = pais.getNome();
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

        // Genero is required and must exist in database.
        if (genero == null || genero.length() == 0) {
            errors.add(new ValidationError("genero", "Selecione o genero"));
        } else if (Genero.findGenero(genero) == null) {
            errors.add(new ValidationError("genero", "Gênero enválido: " + genero + "."));
        }

        if (dataNascimento == null) {
            errors.add(new ValidationError("dataNascimento", "Preencha a data de nascimento"));
        }

        // Escolaridade is required and must exist in database.
        if (escolaridade == null || escolaridade.length() == 0) {
            errors.add(new ValidationError("escolaridade", "Selecione a escolaridade"));
        } else if (Escolaridade.findEscolaridade(escolaridade) == null) {
            errors.add(new ValidationError("escolaridade", "Escolaridade enválida: " + escolaridade + "."));
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
        } else if (!isCPF(cpf)) {
        errors.add(new ValidationError("cpf", "CPF enválido: " + cpf + "."));
    }

        if (email == null || email.length() == 0) {
            errors.add(new ValidationError("email", "Preencha o email"));
        }

        // Escolaridade is required and must exist in database.
        if (modalidade == null || modalidade.length() == 0) {
            errors.add(new ValidationError("modalidade", "Selecione a modalidade"));
        } else if (Modalidade.findModalidade(modalidade) == null) {
            errors.add(new ValidationError("modalidade", "Modalidade enválida: " + modalidade + "."));
        }

        // Escolaridade is required and must exist in database.
        if (fonte == null || fonte.length() == 0) {
            errors.add(new ValidationError("fonte", "Selecione a fonte"));
        } else if (Fonte.findFonte(fonte) == null) {
            errors.add(new ValidationError("fonte", "Fonte enválida: " + fonte + "."));
        }

        return errors.isEmpty() ? null : errors;
    }
}
