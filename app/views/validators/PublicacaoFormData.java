package views.validators;

import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class PublicacaoFormData {

    public String titulo = "";
    public String resumo = "";
    public String url = "";
    public String nomeCapa = "";

    /** Necessario para instanciar o form */
    public PublicacaoFormData() {
    }

    public PublicacaoFormData(String titulo, String resumo, String url, String nomeCapa) {
        this.titulo = titulo;
        this.resumo = resumo;
        this.url = url;
        this.nomeCapa = nomeCapa;
    }

    public List<ValidationError> validate() {

        List<ValidationError> errors = new ArrayList<>();

        if (titulo == null || titulo.length() == 0) {
            errors.add(new ValidationError("titulo", "Preencha o título"));
        }

        if (resumo == null || resumo.length() == 0) {
            errors.add(new ValidationError("resumo", "Preencha o resumo"));
        }

        if (url == null || url.length() == 0) {
            errors.add(new ValidationError("url", "Preencha a url"));
        }

        return errors.isEmpty() ? null : errors;
    }
}
