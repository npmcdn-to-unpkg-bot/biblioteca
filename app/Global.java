import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

import java.io.File;

import static play.mvc.Results.notFound;

public class Global extends GlobalSettings {

    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader request) {
        return F.Promise.<Result> pure(notFound(views.html.mensagens.erro.naoEncontrada.render(request.uri())));
    }

    @Override
    public void beforeStart(Application app) {

        File directoryPdfsArtigos = new File(Play.application().configuration().getString("diretorioDePdfsArtigos"));
        File directoryPdfsLivros = new File(Play.application().configuration().getString("diretorioDePdfsLivros"));
        File directoryPdfsPublicacoes = new File(Play.application().configuration().getString("diretorioDePdfsPublicacoes"));

        File directoryImgCursos = new File(Play.application().configuration().getString("diretorioDeFotosCursos"));
        File directoryImgPublicacoes = new File(Play.application().configuration().getString("diretorioDeFotosPublicacoes"));
        File directoryImgNoticias = new File(Play.application().configuration().getString("diretorioDeFotosNoticias"));

        if (!directoryPdfsArtigos.exists()) {
            if (directoryPdfsArtigos.mkdirs()) {
                Logger.info("Directory artigos is created " + Play.application().configuration().getString("diretorioDePdfsArtigos"));
            } else {
                Logger.error("Failed to create directory " + Play.application().configuration().getString("diretorioDePdfsArtigos"));
            }
        }

        if (!directoryPdfsLivros.exists()) {
            if (directoryPdfsLivros.mkdirs()) {
                Logger.info("Directory livros is created! " + Play.application().configuration().getString("diretorioDePdfsLivros"));
            } else {
                Logger.error("Failed to create directory livros! " +Play.application().configuration().getString("diretorioDePdfsLivros"));
            }
        }

        if (!directoryPdfsPublicacoes.exists()) {
            if (directoryPdfsPublicacoes.mkdirs()) {
                Logger.info("Directory publicações is created! " + Play.application().configuration().getString("diretorioDePdfsPublicacoes"));
            } else {
                Logger.error("Failed to create directory publicações! " + Play.application().configuration().getString("diretorioDePdfsPublicacoes"));
            }
        }

        if (!directoryImgCursos.exists()) {
            if (directoryImgCursos.mkdirs()) {
                Logger.info("Directory cursos is created! " + Play.application().configuration().getString("diretorioDeFotosCursos"));
            } else {
                Logger.error("Failed to create directory cursos! " + Play.application().configuration().getString("diretorioDeFotosCursos"));
            }
        }

        if (!directoryImgPublicacoes.exists()) {
            if (directoryImgPublicacoes.mkdirs()) {
                Logger.info("Directory publicações imagens is created! " + Play.application().configuration().getString("diretorioDeFotosPublicacoes"));
            } else {
                Logger.error("Failed to create directory publicações imagens! " + Play.application().configuration().getString("diretorioDeFotosPublicacoes"));
            }
        }

        if (!directoryImgNoticias.exists()) {
            if (directoryImgNoticias.mkdirs()) {
                Logger.info("Directory notícias is created! " + Play.application().configuration().getString("diretorioDeFotosNoticias"));
            } else {
                Logger.error("Failed to create directory notícias!" + Play.application().configuration().getString("diretorioDeFotosNoticias"));
            }
        }
    }

}
