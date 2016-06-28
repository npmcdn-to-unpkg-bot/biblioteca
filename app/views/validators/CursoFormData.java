package views.validators;

import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CursoFormData {

    public String nome = "";
    public String descricao = "";
    public Date dataInicio = null;
    public String site = "";

    /** Necessario para instanciar o form */
    public CursoFormData() {
    }

    public CursoFormData(String nome, String descricao, Date dataInicio, String site) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.site = site;
    }

    public List<ValidationError> validate() {

        List<ValidationError> errors = new ArrayList<>();

        if (nome == null || nome.length() == 0) {
            errors.add(new ValidationError("nome", "Preencha o nome"));
        }

        if (descricao == null || descricao.length() == 0) {
            errors.add(new ValidationError("descricao", "Preencha a descrição"));
        }

        if (site == null || site.length() == 0) {
            errors.add(new ValidationError("site", "Preencha o site"));
        }

        if (dataInicio == null) {
            errors.add(new ValidationError("dataInicio", "Preencha a data de início"));
        }

        return errors.isEmpty() ? null : errors;
    }
}
