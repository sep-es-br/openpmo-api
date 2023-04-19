package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.menu.MenuOfficeDto;
import br.gov.es.openpmo.dto.menu.PlanMenuDto;
import br.gov.es.openpmo.dto.menu.PortfolioMenuRequest;
import br.gov.es.openpmo.dto.menu.WorkpackMenuDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import br.gov.es.openpmo.dto.workpack.WorkpackName;
import br.gov.es.openpmo.dto.workpackmodel.details.WorkpackModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.filter.SortByDirectionEnum;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.office.plan.PlanService;
import br.gov.es.openpmo.service.properties.GetSorterProperty;
import br.gov.es.openpmo.service.workpack.PropertyComparator;
import br.gov.es.openpmo.service.workpack.WorkpackModelService;
import br.gov.es.openpmo.service.workpack.WorkpackPermissionVerifier;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static br.gov.es.openpmo.enumerator.Session.PROPERTIES;

@Service
public class MenuService {

  private final WorkpackService workpackService;
  private final PlanService planService;
  private final OfficeService officeService;
  private final PersonService personService;
  private final WorkpackModelService workpackModelService;
  private final WorkpackPermissionVerifier workpackPermissionVerifier;
  private final GetSorterProperty getSorterProperty;

  @Autowired
  public MenuService(
    final WorkpackService workpackService,
    final PlanService planService,
    final PersonService personService,
    final OfficeService officeService,
    final WorkpackModelService workpackModelService,
    final WorkpackPermissionVerifier workpackPermissionVerifier,
    final GetSorterProperty getSorterProperty
  ) {
    this.personService = personService;
    this.workpackService = workpackService;
    this.planService = planService;
    this.officeService = officeService;
    this.workpackModelService = workpackModelService;
    this.workpackPermissionVerifier = workpackPermissionVerifier;
    this.getSorterProperty = getSorterProperty;
  }

  private static Optional<Property> equivalentPropertyOfWorkpack(
    final Workpack workpack,
    final WorkpackDetailDto detailDto,
    final WorkpackModelDetailDto model
  ) {
    final PropertyModelDto propertyModelName = model.getProperties()
      .stream()
      .filter(property -> property.getSession() == PROPERTIES && "name".equals(property.getName()))
      .findFirst()
      .orElse(null);

    final PropertyDto propertyName = detailDto.getProperties().stream()
      .filter(pn -> propertyModelName != null && pn.getIdPropertyModel().equals(propertyModelName.getId()))
      .findFirst()
      .orElse(null);

    if (propertyName == null) return Optional.empty();

    return workpack.getProperties().stream()
      .filter(Objects::nonNull)
      .filter(property -> propertyName.getId().equals(property.getId()))
      .findFirst();
  }

  private static boolean isWorkpackWithPermission(
    final Collection<Long> permittedWorkpacksId,
    final Workpack workpack
  ) {
    return permittedWorkpacksId.contains(workpack.getId());
  }

  public Set<WorkpackMenuDto> findAllPortfolio(final PortfolioMenuRequest request) {
    final List<PermissionDto> permissions = this.fetchOfficePermissions(request);

    final boolean hasAnyPermission = this.hasAnyPermission(request.getIdUser(), permissions);

    if (!this.hasOfficePermission(request.getIdUser(), hasAnyPermission, request.getIdOffice())) {return Collections.emptySet();}

    final List<Plan> plans = this.findAllPlansInOffice(request.getIdOffice()).parallelStream()
      .filter(a -> request.getIdPlan() == null || request.getIdPlan().equals(a.getId()))
      .collect(Collectors.toList());

    final List<WorkpackMenuDto> menus = new ArrayList<>(0);

    plans.parallelStream().forEach(plan -> this.addPlanIfHasPermission(
      request,
      menus,
      hasAnyPermission,
      plan
    ));
    return this.sortMenus(menus);
  }

  private Set<WorkpackMenuDto> sortMenus(final Collection<WorkpackMenuDto> menus) {
    for (final WorkpackMenuDto menu : menus) {
      if (!menu.isEmpty()) {
        menu.setChildren(this.sortMenus(menu.getChildren()));
      }
    }

    final Map<Long, SortByDirectionEnum> modelGroupedBySort = menus.stream()
      .collect(Collectors.toMap(
        WorkpackMenuDto::getIdWorkpackModel,
        menu -> menu.getSorter().getDirection(),
        (a, b) -> a
      ));

    return menus.stream()
      .sorted(
        Comparator.comparing(WorkpackMenuDto::getIdWorkpackModelLinked, Comparator.nullsLast(Comparator.naturalOrder()))
          .thenComparing(WorkpackMenuDto::getIdWorkpackModel, Comparator.nullsLast(Comparator.naturalOrder()))
          .thenComparing(
            (menuA, menuB) -> {
              if (menuA == null && menuB == null) return 0;
              if (menuB == null) return 1;
              if (menuA == null) return -1;
              if (menuA.sameModel(menuB)) {
                final SortByDirectionEnum sort = modelGroupedBySort.getOrDefault(menuA.getIdWorkpackModel(), SortByDirectionEnum.ASC);
                return PropertyComparator.compare(menuA.getSorter().getValue(), menuB.getSorter().getValue()) * sort.getOrder();
              }
              return PropertyComparator.compare(menuA.getSorter().getValue(), menuB.getSorter().getValue());
            }
          )
      )
      .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private void addPlanIfHasPermission(
    final PortfolioMenuRequest portfolioMenuRequest,
    final List<? super WorkpackMenuDto> menus,
    final boolean hasAnyPermission,
    final Plan plan
  ) {
    boolean hasPlanPermission = hasAnyPermission;

    final Long idPlan = plan.getId();

    if (!hasAnyPermission) {
      hasPlanPermission = this.hasPlanPermission(portfolioMenuRequest.getIdUser(), idPlan);
    }

    if (hasPlanPermission || this.planService.hasPermissionPlanWorkpack(idPlan, portfolioMenuRequest.getIdUser())) {
      this.addPlanStructure(new AddPlanCommand(
        portfolioMenuRequest.getIdUser(),
        idPlan,
        menus,
        hasPlanPermission
      ));
    }
  }

  private void addPlanStructure(final AddPlanCommand command) {
    final Set<Workpack> workpacks = this.workpackService.findAllByIdPlan(command.getIdPlan());

    final Set<Long> permittedWorkpacksId = this.findAllWorkpacksWithPermissions(
      command.getIdUser(),
      command.getIdPlan()
    );

    command.getMenus().addAll(this.buildWorkpackStructure(
      workpacks,
      command.getIdPlan(),
      command.getIdUser(),
      command.isHasPlanPermission(),
      permittedWorkpacksId
    ));
  }

  private Set<Long> findAllWorkpacksWithPermissions(
    final Long idUser,
    final Long idPlan
  ) {
    return this.workpackService.findAllWorkpacksWithPermissions(
      idPlan,
      idUser
    );
  }

  private boolean hasPlanPermission(
    final Long idUser,
    final Long idPlan
  ) {
    return this.planService.hasPermissionPlan(idPlan, idUser);
  }

  public List<MenuOfficeDto> findAllOffice(final Long idUser) {
    this.findPersonById(idUser);

    final List<MenuOfficeDto> menus = new ArrayList<>(0);

    final List<Office> offices = this.officeService.findAll();

    offices.parallelStream().forEach(office -> {
      final List<PermissionDto> permissions = this.fetchOfficePermissions(new PortfolioMenuRequest(office.getId(), idUser));

      final boolean hasPermission = this.hasAnyPermission(idUser, permissions);

      if (this.hasOfficePermission(idUser, hasPermission, office.getId())) {
        final MenuOfficeDto item = MenuOfficeDto.of(office);
        item.setPlans(new HashSet<>());
        final List<Plan> plans = this.findAllPlansInOffice(office.getId());

        for (final Plan plan : plans) {
          if (hasPermission || this.planService.hasPermissionPlanWorkpack(plan.getId(), idUser)) {
            item.getPlans().add(PlanMenuDto.of(plan));
          }
        }
        menus.add(item);
      }
    });
    return menus;
  }

  private Person findPersonById(final Long idUser) {
    return this.personService.findById(idUser);
  }

  private List<PermissionDto> fetchOfficePermissions(final PortfolioMenuRequest request) {
    final Office office = this.findOfficeById(request.getIdOffice());
    final Person person = this.findPersonById(request.getIdUser());
    return this.workpackPermissionVerifier.fetchOfficePermissions(office, person);
  }

  private boolean hasAnyPermission(
    final Long idUser,
    final Collection<PermissionDto> permissions
  ) {
    final Person person = this.findPersonById(idUser);
    return person.getAdministrator() || (permissions != null && !permissions.isEmpty());
  }

  private boolean hasOfficePermission(
    final Long idUser,
    final boolean hasPermission,
    final Long idOffice
  ) {
    return hasPermission || this.officeService.hasPermissionPlanWorkpack(idOffice, idUser);
  }

  private List<Plan> findAllPlansInOffice(final Long idOffice) {
    return this.planService.findAllInOffice(idOffice);
  }

  private Office findOfficeById(final Long idOffice) {
    return this.officeService.findById(idOffice);
  }

  private Set<WorkpackMenuDto> buildWorkpackStructure(
    final Iterable<? extends Workpack> workpacks,
    final Long idPlan,
    final Long idUser,
    boolean permission,
    final Set<Long> permittedWorkpacksId
  ) {

    final Set<WorkpackMenuDto> menu = new HashSet<>(0);

    for (final Workpack workpack : workpacks) {

      final Set<BelongsTo> workpackBelongsToRelation = workpack.getBelongsTo();

      final boolean isLinked = workpackBelongsToRelation.stream()
        .anyMatch(belongsTo -> idPlan.equals(belongsTo.getIdPlan()) && Boolean.TRUE.equals(belongsTo.getLinked()));

      if (isLinked) {
        final WorkpackModel linkedModel = this.fetchLinkedModel(workpack, idPlan);
        this.addLinkedWorkpack(
          idPlan,
          idUser,
          permission,
          permittedWorkpacksId,
          menu,
          workpack,
          linkedModel
        );
      } else {
        permission = this.addWorkpack(
          idPlan,
          idUser,
          permission,
          permittedWorkpacksId,
          menu,
          workpack
        );
      }
    }
    return menu;
  }

  private boolean addLinkedWorkpack(
    final Long idPlan,
    final Long idUser,
    boolean permission,
    final Set<Long> permittedWorkpackId,
    final Collection<? super WorkpackMenuDto> menu,
    final Workpack workpack,
    final WorkpackModel linkedModel
  ) {
    final WorkpackMenuDto menuItemDto = WorkpackMenuDto.of(workpack, idPlan, this.getSorterProperty.execute(workpack.getId(), idUser));
    menuItemDto.setIdWorkpackModelLinked(linkedModel.getId());

    if (workpack.hasPropertyModel()) {
      permission = this.addPropertyName(
        permission,
        permittedWorkpackId,
        menu,
        workpack,
        menuItemDto
      );
    }
    if (workpack.getChildren() != null) {
      menuItemDto.setChildren(this.buildWorkpackLinkedStructure(
        workpack.getChildren(),
        idPlan,
        idUser,
        permission,
        permittedWorkpackId,
        linkedModel
      ));
    }

    return permission;
  }

  private Set<WorkpackMenuDto> buildWorkpackLinkedStructure(
    final Iterable<? extends Workpack> children,
    final Long idPlan,
    final Long idUser,
    boolean permission,
    final Set<Long> idWorkpackStakeholder,
    final WorkpackModel linkedChildrenModel
  ) {
    final Set<WorkpackMenuDto> menu = new HashSet<>(0);

    for (final Workpack workpack : children) {
      final Optional<WorkpackModel> maybeLinkedModelEquivalent =
        this.findWorkpackModelEquivalent(workpack.getWorkpackModelInstance(), linkedChildrenModel.getChildren());

      if (!maybeLinkedModelEquivalent.isPresent()) continue;

      permission = this.addLinkedWorkpack(
        idPlan,
        idUser,
        permission,
        idWorkpackStakeholder,
        menu,
        workpack,
        maybeLinkedModelEquivalent.get()
      );
    }
    return menu;
  }

  private Optional<WorkpackModel> findWorkpackModelEquivalent(
    final WorkpackModel model,
    final Set<WorkpackModel> linkedModels
  ) {
    if (linkedModels == null || linkedModels.isEmpty()) {
      return Optional.empty();
    }

    final Optional<WorkpackModel> workpackModel = linkedModels.stream()
      .filter(model::hasSameName)
      .findFirst();

    if (workpackModel.isPresent()) {
      return workpackModel;
    }

    final Set<WorkpackModel> children = new HashSet<>();

    for (final WorkpackModel linkedModel : linkedModels) {
      children.addAll(linkedModel.getChildren());
    }

    return this.findWorkpackModelEquivalent(model, children);
  }

  private WorkpackModel fetchLinkedModel(
    final Workpack workpack,
    final Long idPlan
  ) {
    return this.workpackService.findWorkpackModelLinked(
      workpack.getId(),
      idPlan
    );
  }

  private boolean addWorkpack(
    final Long idPlan,
    final Long idUser,
    boolean permission,
    final Set<Long> permittedWorkpacksId,
    final Collection<? super WorkpackMenuDto> menu,
    final Workpack workpack
  ) {
    final WorkpackMenuDto menuItemDto = WorkpackMenuDto.of(
      workpack,
      idPlan,
      this.getSorterProperty.execute(workpack.getId(), idUser)
    );

    if (workpack.hasPropertyModel()) {
      permission = this.addPropertyName(
        permission,
        permittedWorkpacksId,
        menu,
        workpack,
        menuItemDto
      );
    }
    if (workpack.getChildren() != null) {
      menuItemDto.setChildren(this.buildWorkpackStructure(
        workpack.getChildren(),
        idPlan,
        idUser,
        permission,
        permittedWorkpacksId
      ));
    }
    return permission;
  }

  private boolean addPropertyName(
    boolean permission,
    final Set<Long> permittedWorkpacksId,
    final Collection<? super WorkpackMenuDto> menu,
    final Workpack workpack,
    final WorkpackMenuDto menuItem
  ) {
    final Optional<String> maybeWorkpackNameData = this.workpackService.findWorkpackNameAndFullname(workpack.getId())
      .map(WorkpackName::getName);

    if (maybeWorkpackNameData.isPresent()) {
      menuItem.setName(maybeWorkpackNameData.get());
      if (isWorkpackWithPermission(permittedWorkpacksId, workpack)) {
        permission = true;
      }
      if (permission || this.isChildrenWithPermission(workpack.getChildren(), permittedWorkpacksId)) {
        menu.add(menuItem);
      }
    }
    return permission;
  }

  private boolean isChildrenWithPermission(
    final Iterable<? extends Workpack> workpacks,
    final Set<Long> idWorkpackStakeholder
  ) {
    if (workpacks == null) return false;
    for (final Workpack workpack : workpacks) {
      if (isWorkpackWithPermission(idWorkpackStakeholder, workpack)) {
        return true;
      }
      if (workpack.getChildren() != null) {
        return this.isChildrenWithPermission(workpack.getChildren(), idWorkpackStakeholder);
      }
    }
    return false;
  }

  private static final class AddPlanCommand {

    private final Long idUser;
    private final Long idPlan;
    private final Collection<? super WorkpackMenuDto> menus;
    private final boolean hasPlanPermission;

    AddPlanCommand(
      final Long idUser,
      final Long idPlan,
      final Collection<? super WorkpackMenuDto> menus,
      final boolean hasPlanPermission
    ) {
      this.idUser = idUser;
      this.idPlan = idPlan;
      this.menus = menus;
      this.hasPlanPermission = hasPlanPermission;
    }

    public Long getIdUser() {
      return this.idUser;
    }

    public Long getIdPlan() {
      return this.idPlan;
    }

    public Collection<? super WorkpackMenuDto> getMenus() {
      return this.menus;
    }

    public boolean isHasPlanPermission() {
      return this.hasPlanPermission;
    }

  }

}
