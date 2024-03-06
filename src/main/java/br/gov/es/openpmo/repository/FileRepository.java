package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.actors.File;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends Neo4jRepository<File, Long> {

  @Query(
    "MATCH (file:File)-[:IS_SOURCE_TEMPLATE_OF]->(report:ReportDesign) " +
    "WHERE id(file)=$idFile AND id(report)=$idReportModel " +
    "RETURN file"
  )
  Optional<File> findFileTemplateByIdAndReportDesign(@Param("idFile") Long idFile, @Param("idReportModel") Long idReportModel);
  
  @Query(
    "MATCH (file:File)-[:IS_SOURCE_TEMPLATE_OF]->(report:ReportDesign) " +
    "WHERE id(report)=$idReportModel AND file.main = TRUE " +
    "RETURN file"
  )
  Optional<File> findFileTemplateReportDesignWhereMainIsTrue(@Param("idReportModel") Long idReportModel);
  
  @Query(
    "MATCH (file:File)<-[:IS_COMPILATION_OF]-(compiledFile:File) " +
    "WHERE id(file)=$idFile " +
    "RETURN compiledFile"
  )
  Optional<File> findCompiledFileByTemplateFileId(@Param("idFile") Long idFile);

}
