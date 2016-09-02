import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static play.mvc.Results.notFound;

public class Global extends GlobalSettings {

    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader request) {
        return F.Promise.<Result> pure(notFound(views.html.mensagens.erro.naoEncontrada.render(request.uri())));
    }

    @Override
    public void beforeStart(Application app) {

        // Diretorios
        try {
            Path directoryPdfsArtigos = Paths.get(Play.application().configuration().getString("diretorioDePdfsArtigos"));
            Path directoryPdfsLivros = Paths.get(Play.application().configuration().getString("diretorioDePdfsLivros"));
            Path directoryPdfsPublicacoes = Paths.get(Play.application().configuration().getString("diretorioDePdfsPublicacoes"));
            Path directoryImgCursos = Paths.get(Play.application().configuration().getString("diretorioDeFotosCursos"));
            Path directoryImgPublicacoes = Paths.get(Play.application().configuration().getString("diretorioDeFotosPublicacoes"));
            Path directoryImgNoticias = Paths.get(Play.application().configuration().getString("diretorioDeFotosNoticias"));

            Files.createDirectories(directoryPdfsArtigos);
            Logger.info("Directory artigos is created " + directoryPdfsArtigos);
            Files.createDirectories(directoryPdfsLivros);
            Logger.info("Directory livros is created " + directoryPdfsLivros);
            Files.createDirectories(directoryPdfsPublicacoes);
            Logger.info("Directory publicacoes is created " + directoryPdfsPublicacoes);
            Files.createDirectories(directoryImgCursos);
            Logger.info("Directory Image cursos is created " + directoryImgCursos);
            Files.createDirectories(directoryImgPublicacoes);
            Logger.info("Directory Image publicacoes is created " + directoryImgPublicacoes);
            Files.createDirectories(directoryImgNoticias);
            Logger.info("Directory Image noticias is created " + directoryImgNoticias);
        } catch (IOException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        } catch (Exception e) {
           Logger.error(e.toString());
        }

        // Arquivos
        String s = "Biblioteca Hello World Create File! ";
        byte data[] = s.getBytes();

            Path fileArtigo = Paths.get(Play.application().configuration().getString("diretorioDePdfsArtigos") + "/testFilePdfArtigo.txt");
            Path fileLivro = Paths.get(Play.application().configuration().getString("diretorioDePdfsLivros") + "/testFilePdfLivro.txt");
            Path filePublicacao = Paths.get(Play.application().configuration().getString("diretorioDePdfsPublicacoes") + "/testFilePdfPublicacoes.txt");
            Path fileImgCurso = Paths.get(Play.application().configuration().getString("diretorioDeFotosCursos") + "/testFileImgCurso.txt");
            Path fileImgPublicacao = Paths.get(Play.application().configuration().getString("diretorioDeFotosPublicacoes") + "/testFileImgPublicacao.txt");
            Path fileImgNoticia = Paths.get(Play.application().configuration().getString("diretorioDeFotosNoticias") + "/testFileImgNoticia.txt");

        try (OutputStream outArtigo = new BufferedOutputStream(
                Files.newOutputStream(fileArtigo, CREATE, APPEND))) {
            outArtigo.write(data, 0, data.length);
            Logger.info("File testFilePdfArtigo.txt is created");
        } catch (IOException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        } catch (Exception e) {
            Logger.error(e.toString());
        }

        try (OutputStream outLivro = new BufferedOutputStream(
                Files.newOutputStream(fileLivro, CREATE, APPEND))) {
            outLivro.write(data, 0, data.length);
            Logger.info("File testFilePdfLivro.txt is created");
        } catch (IOException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        }   catch (Exception e) {
            Logger.error(e.toString());
        }

        try (OutputStream outPublicacao = new BufferedOutputStream(
                Files.newOutputStream(filePublicacao, CREATE, APPEND))) {
            outPublicacao.write(data, 0, data.length);
            Logger.info("File testFilePdfPublicacoes.txt is created");
        } catch (IOException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        }   catch (Exception e) {
            Logger.error(e.toString());
        }

        try (OutputStream outImgCurso = new BufferedOutputStream(
                Files.newOutputStream(fileImgCurso, CREATE, APPEND))) {
            outImgCurso.write(data, 0, data.length);
            Logger.info("File testFileImgCurso.txt is created");
        } catch (IOException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        }   catch (Exception e) {
            Logger.error(e.toString());
        }

        try (OutputStream outImgPublicacao = new BufferedOutputStream(
                Files.newOutputStream(fileImgPublicacao, CREATE, APPEND))) {
            outImgPublicacao.write(data, 0, data.length);
            Logger.info("File testFileImgPublicacao.txt is created");
        } catch (IOException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        }   catch (Exception e) {
            Logger.error(e.toString());
        }

        try (OutputStream outImgNoticia = new BufferedOutputStream(
                Files.newOutputStream(fileImgNoticia, CREATE, APPEND))) {
            outImgNoticia.write(data, 0, data.length);
            Logger.info("File testFileImgNoticia.txt is created");
        } catch (IOException e) {
            Logger.error(e.toString());
            e.printStackTrace();
        }   catch (Exception e) {
            Logger.error(e.toString());
        }

    }

}
