package br.gov.es.openpmo.service.ccbmembers;

@FunctionalInterface
public interface IDeleteCCBMemberService {

  void delete(
    Long idPerson,
    Long idWorkpack
  );

}
