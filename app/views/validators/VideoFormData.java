package views.validators;

import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class VideoFormData {

    public String titulo = "";
    public String descricao = "";
    public String url = "";

    /** Necessario para instanciar o form */
    public VideoFormData() {}

    public VideoFormData(String titulo, String descricao, String url) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.url = url;
    }

    public List<ValidationError> validate() {

        List<ValidationError> errors = new ArrayList<>();

        if (titulo == null || titulo.length() == 0) {
            errors.add(new ValidationError("titulo", "Preencha o título"));
        } else if (titulo.length() > 250) {
            errors.add(new ValidationError("titulo", "Título com no máximo 250 caractéres"));
        }

        if (descricao == null || descricao.length() == 0) {
            errors.add(new ValidationError("descricao", "Preencha a Descrição"));
        } else if (descricao.length() > 400) {
            errors.add(new ValidationError("descricao", "Descrição com no máximo 400 caractéres"));
        }

        if (url == null || url.length() == 0) {
            errors.add(new ValidationError("url", "Preencha a URL"));
        } else if (url.length() > 400) {
            errors.add(new ValidationError("url", "URL com no máximo 400 caractéres"));
        }


        return errors.isEmpty() ? null : errors;
    }

}
