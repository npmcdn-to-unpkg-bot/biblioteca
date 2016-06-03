package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Model;
import com.google.common.util.concurrent.ExecutionError;
import play.Logger;
import play.libs.F;

public class Usuarios extends Model {

    public static F.Option<Usuario> existe(String email, String senha) {

        try {
            Usuario usuario = Ebean.find(Usuario.class).where().eq("email", email).eq("senha", senha).findUnique();

            if (usuario == null) {
                return F.Option.<Usuario>None();
            }

            return F.Option.Some(usuario);
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return F.Option.<Usuario>None();
        }

    }
}
