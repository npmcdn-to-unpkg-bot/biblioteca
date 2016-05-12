package views.validators;

import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class LivroFormData {

    public String titulo = "";
    public String subTitulo = "";
    public String editora = "";
    public String autores = "";
    public String isbn = "";
    public Integer ano = 0;
    public Integer edicao = 0;
    public Integer paginas = 0;

    public List<ValidationError> validate() {

        List<ValidationError> errors = new ArrayList<>();

        if (titulo == null || titulo.length() == 0) {
            errors.add(new ValidationError("titulo", "Preencha o título"));
        }

        if (subTitulo == null || subTitulo.length() == 0) {
            errors.add(new ValidationError("subTitulo", "Preencha o Subtítulo"));
        }

        if (isbn == null || isbn.length() == 0) {
            errors.add(new ValidationError("isbn", "Preencha o ISBN"));
        }

        if (editora == null || editora.length() == 0) {
            errors.add(new ValidationError("editora", "Preencha a Editora"));
        }

        if (autores == null || autores.length() == 0) {
            errors.add(new ValidationError("autores", "Preencha o Autor"));
        }

        if (ano == null || ano == 0) {
            errors.add(new ValidationError("ano", "Preencha o Ano"));
        }

        if (edicao == null || edicao == 0) {
            errors.add(new ValidationError("edicao", "Preencha o Edição"));
        }

        if (paginas == null || paginas == 0) {
            errors.add(new ValidationError("paginas", "Preencha as Páginas"));
        }

        return errors.isEmpty() ? null : errors;
    }
}

