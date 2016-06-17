package views.validators;

import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventoFormData {

    public String nome = "";
    public Calendar dataInicio = null;
    public Calendar dataFim = null;
    public String site = "";
    public String local = "";

    /** Necessario para instanciar o form */
    public EventoFormData() {
    }

    public EventoFormData(String nome, Calendar dataInicio, Calendar dataFim, String site, String local) {
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.site = site;
        this.local = local;
    }

    public List<ValidationError> validate() {

        List<ValidationError> errors = new ArrayList<>();

        if (nome == null || nome.length() == 0) {
            errors.add(new ValidationError("nome", "Preencha o nome"));
        }

        if (dataInicio == null) {
            errors.add(new ValidationError("dataInicio", "Preencha a Data de Início"));
        }

        if (dataFim == null) {
            errors.add(new ValidationError("dataFim", "Preencha a Data Final"));
        }

        if (site == null || site.length() == 0) {
            errors.add(new ValidationError("site", "Preencha o site"));
        }

        if (local == null || local.length() == 0) {
            errors.add(new ValidationError("local", "Preencha o Local"));
        }

        return errors.isEmpty() ? null : errors;
    }


}
