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
    public PublicacaoFormData() {}

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
        } else if (titulo.length() > 250) {
            errors.add(new ValidationError("titulo", "Título com no máximo 250 caractéres"));
        }

        if (resumo == null || resumo.length() == 0) {
            errors.add(new ValidationError("resumo", "Preencha o resumo"));
        } else if (resumo.length() > 400) {
            errors.add(new ValidationError("resumo", "Resumo com no máximo 150 caractéres"));
        }

        if (url == null || url.length() == 0) {
            errors.add(new ValidationError("url", "Preencha a url"));
        } else if (url.length() > 400) {
            errors.add(new ValidationError("url", "URL com no máximo 400 caractéres"));
        }

        return errors.isEmpty() ? null : errors;
    }
}
