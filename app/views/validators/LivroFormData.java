package views.validators;

import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class LivroFormData {

    public String titulo = "";
    public String subTitulo = "";
    public String isbn = "";
    public String editora = "";
    public String autores = "";
    public Integer edicao = 0;
    public Integer paginas = 0;
    public Integer ano = 0;

    /** Necessario para instanciar o form */
    public LivroFormData() {
    }

    public LivroFormData(String titulo, String subTitulo, String isbn, String editora, String autores, Integer edicao, Integer paginas, Integer ano) {
        this.titulo = titulo;
        this.subTitulo = subTitulo;
        this.isbn = isbn;
        this.editora = editora;
        this.autores = autores;
        this.edicao = edicao;
        this.paginas = paginas;
        this.ano = ano;
    }

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

        if (isbn.length() > 17) {
            errors.add(new ValidationError("isbn", "Preencha o ISBN corretamente"));
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

