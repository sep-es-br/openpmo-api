package br.gov.es.openpmo.service.actors;


import br.gov.es.openpmo.dto.file.AvatarDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.repository.FileRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static br.gov.es.openpmo.utils.ApplicationMessage.FILE_NOT_FOUND;

@Service
public class AvatarService {

  private final PersonService personService;
  private final FileRepository repository;
  private final Logger logger;

  @Value("${app.pathImagens}")
  private String basePath;

  public AvatarService(
    final PersonService personService,
    final FileRepository repository,
    final Logger logger
  ) {
    this.personService = personService;
    this.repository = repository;
    this.logger = logger;
  }

  @Transactional(readOnly = true)
  public AvatarDto findById(final Long idPerson, final UriComponentsBuilder uriComponentsBuilder) {

    final br.gov.es.openpmo.model.actors.File avatar = this.personService
      .findById(idPerson)
      .getAvatar();

    if(avatar == null) {
      return null;
    }

    return new AvatarDto(avatar, uriComponentsBuilder);
  }

  public br.gov.es.openpmo.model.actors.File find(final Long id) {
    return this.repository.findById(id)
      .orElseThrow(() -> new NegocioException(FILE_NOT_FOUND));
  }

  public AvatarDto update(final MultipartFile multipartFile, final Long idPerson, final UriComponentsBuilder uriComponentsBuilder) throws IOException {
    this.deleteAvatarByIdPerson(idPerson);
    return this.save(multipartFile, idPerson, uriComponentsBuilder);
  }

  @Transactional
  public AvatarDto save(final MultipartFile multipartFile, final Long idPerson, final UriComponentsBuilder uriComponentsBuilder) throws IOException {
    final Person person = this.personService.findById(idPerson);

    final br.gov.es.openpmo.model.actors.File avatar = this.createAvatar(multipartFile, person);

    this.saveOnDisc(multipartFile.getBytes(), avatar.getUniqueNameKey());

    this.repository.save(avatar);

    return new AvatarDto(avatar, uriComponentsBuilder);
  }

  private br.gov.es.openpmo.model.actors.File createAvatar(final MultipartFile multipartFile, final Person person) {
    final br.gov.es.openpmo.model.actors.File avatar = new br.gov.es.openpmo.model.actors.File();
    avatar.setUniqueNameKey(UUID.randomUUID() + multipartFile.getOriginalFilename());
    avatar.setUserGivenName(multipartFile.getOriginalFilename());
    avatar.setMimeType(multipartFile.getContentType());
    avatar.setPerson(person);
    return avatar;
  }

  private void saveOnDisc(final byte[] dados, final String nomeArquivo) throws IOException {
    final String caminhoArquivo = this.generatePath(nomeArquivo);
    final File file = Files.createFile(Paths.get(caminhoArquivo)).toFile();

    try(final FileOutputStream out = new FileOutputStream(file)) {
      out.write(dados);
      out.flush();
    }
  }

  public void deleteAvatarByIdPerson(final Long idPerson) {
    final Person person = this.personService.findById(idPerson);

    final br.gov.es.openpmo.model.actors.File oldAvatar = person.getAvatar();

    if(oldAvatar == null) {
      throw new NegocioException(FILE_NOT_FOUND);
    }

    this.delete(oldAvatar.getId());
  }

  public void delete(final Long idAvatar) {
    final br.gov.es.openpmo.model.actors.File fileToDelete = this.repository
      .findById(idAvatar)
      .orElseThrow(() -> new NegocioException(FILE_NOT_FOUND));

    this.eraseFromDisk(fileToDelete);

    this.repository.delete(fileToDelete);
  }

  private void eraseFromDisk(final br.gov.es.openpmo.model.actors.File fileToDelete) {
    try {
      final String caminhoArquivo = this.generatePath(fileToDelete.getUniqueNameKey());
      Files.delete(Paths.get(caminhoArquivo));
    }
    catch(final IOException e) {
      this.logger.error("File not removed", e);
    }
  }

  private String generatePath(final String fileName) {
    return this.basePath.concat(fileName);
  }

  public UrlResource getAvatar(final Long idAvatar) throws IOException {
    final br.gov.es.openpmo.model.actors.File avatar = this.repository.findById(idAvatar)
      .orElseThrow(() -> new NegocioException(FILE_NOT_FOUND));

    final String path = this.basePath + avatar.getUniqueNameKey();
    final URI uri = Paths.get(path).toUri();
    return new UrlResource(uri);
  }
}
