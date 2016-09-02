import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static play.mvc.Results.notFound;

public class Global extends GlobalSettings {

    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader request) {
        return F.Promise.<Result> pure(notFound(views.html.mensagens.erro.naoEncontrada.render(request.uri())));
    }

    @Override
    public void beforeStart(Application app) {

        //Cria pasta de Artigos
        Path directoryPdfsArtigos = Paths.get(Play.application().configuration().getString("diretorioDePdfsArtigos"));

        try {
            Files.createDirectories(directoryPdfsArtigos);
            Logger.info("Directory Artigos is created " + directoryPdfsArtigos);
        } catch (IOException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        }

        //Cria pasta Livros
        Path directoryPdfsLivros = Paths.get(Play.application().configuration().getString("diretorioDePdfsLivros"));

        try {
            Files.createDirectories(directoryPdfsLivros);
            Logger.info("Directory Livros is created " + directoryPdfsLivros);
        } catch (IOException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        }

        //Cria pasta Publicacoes
        Path directoryPdfsPublicacoes = Paths.get(Play.application().configuration().getString("diretorioDePdfsPublicacoes"));

        try {
            Files.createDirectories(directoryPdfsPublicacoes);
            Logger.info("Directory Publicacoes is created " + directoryPdfsPublicacoes);
        } catch (IOException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        }

        //Cria pasta imagens Cursos
        Path directoryImgCursos = Paths.get(Play.application().configuration().getString("diretorioDeFotosCursos"));

        try {
            Files.createDirectories(directoryImgCursos);
            Logger.info("Directory Image Cursos is created " + directoryImgCursos);
        } catch (IOException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        }

        //Cria pasta imagens Publicacoes
        Path directoryImgPublicacoes = Paths.get(Play.application().configuration().getString("diretorioDeFotosPublicacoes"));

        try {
            Files.createDirectories(directoryImgPublicacoes);
            Logger.info("Directory Image Publicacoes is created " + directoryImgPublicacoes);
        } catch (IOException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        }

        //Cria pasta imagens Noticias
        Path directoryImgNoticias = Paths.get(Play.application().configuration().getString("diretorioDeFotosNoticias"));

        try {
            Files.createDirectories(directoryImgNoticias);
            Logger.info("Directory Image Noticias is created " + directoryImgNoticias);
        } catch (IOException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        }

    }

}
