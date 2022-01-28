package br.gov.es.openpmo.service.ccbmembers;

import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.relations.IsCCBMember;
import br.gov.es.openpmo.repository.IsCCBMemberRepository;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeleteCCBMemberService implements IDeleteCCBMemberService {

  private final IsCCBMemberRepository ccbMemberRepository;

  @Autowired
  public DeleteCCBMemberService(final IsCCBMemberRepository ccbMemberRepository) {
    this.ccbMemberRepository = ccbMemberRepository;
  }

  @Override
  public void delete(final Long idPerson, final Long idWorkpack) {
    final List<IsCCBMember> ccbMembers = this.getAllByPersonIdAndWorkpackId(idPerson, idWorkpack);

    if (ccbMembers.isEmpty()) {
      throw new NegocioException(ApplicationMessage.CCB_MEMBER_NOT_FOUND);
    }

    this.ccbMemberRepository.deleteAll(ccbMembers);
  }

  private List<IsCCBMember> getAllByPersonIdAndWorkpackId(final Long idPerson, final Long idWorkpack) {
    return this.ccbMemberRepository.findAllByPersonIdAndWorkpackId(idPerson, idWorkpack);
  }

}
