package br.gov.es.openpmo.service.actors;


import br.gov.es.openpmo.dto.file.AvatarDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.repository.FileRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
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

import static br.gov.es.openpmo.utils.ApplicationMessage.AVATAR_CREATE_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.AVATAR_DELETE_ERROR;
import static br.gov.es.openpmo.utils.ApplicationMessage.FILE_NOT_FOUND;

@Service
public class AvatarService {

  private final PersonService personService;
  private final FileRepository repository;

  @Value("${app.pathImagens}")
  private String basePath;

  public AvatarService(
    final PersonService personService,
    final FileRepository repository
  ) {
    this.personService = personService;
    this.repository = repository;
  }

  private static void ifPersonAlreadyHasAvatarThrowException(final Person person) {
    if(person.hasAvatar()) {
      throw new NegocioException(ApplicationMessage.AVATAR_ALREADY_EXISTS);
    }
  }

  @Transactional(readOnly = true)
  public AvatarDto findById(
    final Long idPerson,
    final UriComponentsBuilder uriComponentsBuilder
  ) {

    final br.gov.es.openpmo.model.actors.File avatar = this.personService
      .findById(idPerson)
      .getAvatar();

    if(avatar == null) {
      return null;
    }

    return new AvatarDto(avatar, uriComponentsBuilder);
  }


  @Transactional
  public AvatarDto update(
    final MultipartFile multipartFile,
    final Long idPerson,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    this.deleteAvatarByIdPerson(idPerson);
    return this.save(multipartFile, idPerson, uriComponentsBuilder);
  }

  @Transactional
  public AvatarDto create(
    final MultipartFile multipartFile,
    final Long idPerson,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    final Person person = this.personService.findById(idPerson);
    ifPersonAlreadyHasAvatarThrowException(person);
    return this.save(multipartFile, idPerson, uriComponentsBuilder);
  }

  private AvatarDto save(
    final MultipartFile multipartFile,
    final Long idPerson,
    final UriComponentsBuilder uriComponentsBuilder
  ) {
    try {
      final Person person = this.personService.findById(idPerson);
      final br.gov.es.openpmo.model.actors.File avatar = this.createAvatar(multipartFile, person);
      this.saveOnDisc(multipartFile.getBytes(), avatar.getUniqueNameKey());
      this.repository.save(avatar);
      return new AvatarDto(avatar, uriComponentsBuilder);
    }
    catch(final IOException e) {
      throw new NegocioException(AVATAR_CREATE_ERROR, e);
    }
  }

  private br.gov.es.openpmo.model.actors.File createAvatar(
    final MultipartFile multipartFile,
    final Person person
  ) {
    final br.gov.es.openpmo.model.actors.File avatar = new br.gov.es.openpmo.model.actors.File();
    avatar.setUniqueNameKey(UUID.randomUUID() + multipartFile.getOriginalFilename());
    avatar.setUserGivenName(multipartFile.getOriginalFilename());
    avatar.setMimeType(multipartFile.getContentType());
    avatar.setPerson(person);
    return avatar;
  }

  private void saveOnDisc(
    final byte[] dados,
    final String nomeArquivo
  ) throws IOException {
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
      throw new NegocioException(AVATAR_DELETE_ERROR, e);
    }
  }

  private String generatePath(final String fileName) {
    return this.basePath.concat(fileName);
  }

  public UrlResource getAvatar(final Long idAvatar) throws IOException {
    final br.gov.es.openpmo.model.actors.File avatar = this.findAvatarById(idAvatar);
    final String path = this.basePath + avatar.getUniqueNameKey();
    final URI uri = Paths.get(path).toUri();
    return new UrlResource(uri);
  }

  private br.gov.es.openpmo.model.actors.File findAvatarById(final Long idAvatar) {
    return this.repository.findById(idAvatar)
      .orElseThrow(() -> new NegocioException(FILE_NOT_FOUND));
  }

}
