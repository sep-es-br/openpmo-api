package br.gov.es.openpmo.service.actors;

import br.gov.es.openpmo.dto.person.favorite.WorkpackFavoritedDetail;
import br.gov.es.openpmo.dto.person.favorite.WorkpackFavoritedRequest;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.relations.IsFavoritedBy;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.repository.IsFavoritedByRepository;
import br.gov.es.openpmo.repository.PersonRepository;
import br.gov.es.openpmo.repository.WorkpackRepository;
import br.gov.es.openpmo.service.authentication.TokenService;
import br.gov.es.openpmo.service.permissions.canaccess.CanAccessData;
import br.gov.es.openpmo.service.workpack.GetWorkpackName;
import br.gov.es.openpmo.utils.ApplicationMessage;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IsFavoritedByService {

  private final TokenService tokenService;

  private final GetWorkpackName getWorkpackName;

  private final WorkpackRepository workpackRepository;

  private final CanAccessData canAccessData;

  private final PersonRepository personRepository;

  private final IsFavoritedByRepository isFavoritedByRepository;

  public IsFavoritedByService(
    final TokenService tokenService,
    final GetWorkpackName getWorkpackName,
    final WorkpackRepository workpackRepository,
    final CanAccessData canAccessData,
    final PersonRepository personRepository,
    final IsFavoritedByRepository isFavoritedByRepository
  ) {
    this.tokenService = tokenService;
    this.getWorkpackName = getWorkpackName;
    this.workpackRepository = workpackRepository;
    this.canAccessData = canAccessData;
    this.personRepository = personRepository;
    this.isFavoritedByRepository = isFavoritedByRepository;
  }

  public Set<WorkpackFavoritedDetail> findAllFavoritesWorkpack(
    final String authorization,
    final Long idPlan
  ) {
    final Long idPerson = this.tokenService.getUserId(authorization);
    final Set<Workpack> workpacks = this.personRepository.findAllFavoriteWorkpackByPersonIdAndPlanId(idPerson, idPlan);
    return workpacks.stream()
      .map(workpack -> this.buildFavWorkpackResponse(workpack, authorization))
      .sorted(Comparator.comparing(WorkpackFavoritedDetail::getName))
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private WorkpackFavoritedDetail buildFavWorkpackResponse(
    final Workpack workpack,
    final String authorization
  ) {
    final Long id = workpack.getId();
    final String icon = workpack.getIcon();
    return new WorkpackFavoritedDetail(
      id,
      this.getWorkpackName.execute(id).getName(),
      icon,
      this.canAccessData.execute(id, authorization).canReadResource()
    );
  }

  public boolean isFavoritedBy(
    final Long idWorkpack,
    final Long idPlan,
    final Long idPerson
  ) {
    return this.checkIfIsFavoritedByExists(
      idPerson,
      idWorkpack,
      idPlan
    );
  }

  private boolean checkIfIsFavoritedByExists(
    final Long idPerson,
    final Long idWorkpack,
    final Long idPlan
  ) {
    return this.findIsFavoritedByIfExists(idPlan, idWorkpack, idPerson).isPresent();
  }

  private Optional<IsFavoritedBy> findIsFavoritedByIfExists(
    final Long idPlan,
    final Long idWorkpack,
    final Long idPerson
  ) {
    return this.isFavoritedByRepository.findIsFavoritedByPersonIdAndWorkpackIdAndPlanId(
      idPerson,
      idWorkpack,
      idPlan
    );
  }

  public void toggleFavorite(
    final Long idWorkpack,
    final WorkpackFavoritedRequest request,
    final String authorization
  ) {
    final Person person = this.getPerson(authorization);
    final Workpack workpack = this.getWorkpack(idWorkpack, request.getIdPlan());
    final Optional<IsFavoritedBy> maybeFavorite = this.findIsFavoritedByIfExists(
      request.getIdPlan(),
      workpack.getId(),
      person.getId()
    );
    if (maybeFavorite.isPresent()) {
      this.unfavorite(maybeFavorite.get());
      return;
    }
    this.favorite(request, person, workpack);
  }

  private Person getPerson(final String authorization) {
    final Long personId = this.tokenService.getUserId(authorization);
    final Optional<Person> personWithFavoriteWorkpacks =
      this.personRepository.findPersonWithFavoriteWorkpacks(personId);
    return personWithFavoriteWorkpacks
      .orElseThrow(() -> new NegocioException(ApplicationMessage.PERSON_NOT_FOUND));
  }

  private Workpack getWorkpack(
    final Long idWorkpack,
    final Long idPlan
  ) {
    return this.workpackRepository.findByIdWorkpackAndIdPlan(idWorkpack, idPlan);
  }

  private void unfavorite(final IsFavoritedBy maybeFavorite) {
    this.isFavoritedByRepository.delete(maybeFavorite);
  }

  private void favorite(
    final WorkpackFavoritedRequest request,
    final Person person,
    final Workpack workpack
  ) {
    final IsFavoritedBy isFavoritedBy = new IsFavoritedBy();
    isFavoritedBy.setPerson(person);
    isFavoritedBy.setWorkpack(workpack);
    isFavoritedBy.setIdPlan(request.getIdPlan());
    this.isFavoritedByRepository.save(isFavoritedBy);
  }

}
