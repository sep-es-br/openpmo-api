package br.gov.es.openpmo.service.files;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.function.Supplier;

public interface FileService {

  void save(
    MultipartFile multipartFile,
    String filename,
    String path
  );

  void save(
    MultipartFile multipartFile,
    String filename,
    Supplier<String> pathSupplier
  );
  
  void save(
    final InputStream inputStream,
    final String filename,
    final String directoryPath
  );

  void remove(String path);

  void createDirectory(String path);

  String generateName(MultipartFile file);
  
  String generateName(String originalFilename);

}
