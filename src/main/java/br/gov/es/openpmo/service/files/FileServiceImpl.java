package br.gov.es.openpmo.service.files;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FileServiceImpl implements FileService {

  private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
  private static final Pattern SPACE_CHAR = Pattern.compile(" ", Pattern.LITERAL);


  @Override
  public void save(
    final MultipartFile file,
    final String filename,
    final String directoryPath
  ) {
    try (final InputStream inputStream = file.getInputStream()) {
      final Path basePath = new File(directoryPath).toPath();
      log.info("Criando o arquivo {} no diretório {}", filename, directoryPath);
      createDirectoryIfNotExists(basePath);
      Files.copy(
        inputStream,
        basePath.resolve(filename)
      );
    }
    catch (final FileAlreadyExistsException e) {
      log.error("O arquivo já existe", e);
      throw new NegocioException(ApplicationMessage.FILE_ALREADY_EXISTS);
    }
    catch (final IOException e) {
      log.error("Ocorreu um erro ao tentar persistir o arquivo no disco", e);
      throw new NegocioException(ApplicationMessage.FILE_ERROR_ON_PERSIST);
    }
  }
  
  @Override
  public void save(
    final InputStream inputStream,
    final String filename,
    final String directoryPath
  ) {      
      try {
    	final Path basePath = new File(directoryPath).toPath();
        log.info("Criando o arquivo {} no diretório {}", filename, directoryPath);
		createDirectoryIfNotExists(basePath);
		Files.copy(
	        inputStream,
	        basePath.resolve(filename)
	      );
      }
      catch (final FileAlreadyExistsException e) {
    	log.error("O arquivo já existe", e);
    	throw new NegocioException(ApplicationMessage.FILE_ALREADY_EXISTS);
      }
      catch (final IOException e) {
    	log.error("Ocorreu um erro ao tentar persistir o arquivo no disco", e);
    	throw new NegocioException(ApplicationMessage.FILE_ERROR_ON_PERSIST);
      }
  }

  @Override
  public void save(
    final MultipartFile multipartFile,
    final String filename,
    final Supplier<String> pathSupplier
  ) {
    this.save(
      multipartFile,
      filename,
      pathSupplier.get()
    );
  }

  @Override
  public void remove(final String path) {
    try {
      log.info("Removendo o arquivo/diretório {}", path);
      Files.deleteIfExists(Paths.get(path));
    }
    catch (final IOException e) {
      log.error("Ocorreu um erro ao tentar remover o arquivo do disco", e);
      throw new NegocioException(ApplicationMessage.FILE_ERROR_ON_DELETE);
    }
  }

  @Override
  public void createDirectory(final String path) {
    try {
      createDirectoryIfNotExists(Paths.get(path ));
    }
    catch (final IOException e) {
      log.error("Ocorreu um erro ao tentar criar o diretório {}", path, e);
      throw new NegocioException(ApplicationMessage.FILE_ERROR_ON_CREATE_DIRECTORY);
    }
  }

  @Override
  public String generateName(final MultipartFile file) {
	return this.generateName(file.getOriginalFilename());
  }

  @Override
  public String generateName(final String originalFilename) {
    final String generatedFilename = UUID.randomUUID() + "-" + originalFilename;
    final String sanitizedGeneratedFilename = SPACE_CHAR.matcher(generatedFilename).replaceAll(Matcher.quoteReplacement("-"));
    log.info("Nome do arquivo gerado com sucesso: {}", sanitizedGeneratedFilename);
    return sanitizedGeneratedFilename;
  }

  private static void createDirectoryIfNotExists(final Path path) throws IOException {
    log.info("Verificando se o diretório {} existe", path);
    if (path.toFile().exists()) {
      log.info("O diretório {} já existe", path);
      return;
    }
    log.info("Criando diretório {}", path);
    Files.createDirectories(path);
  }

}
