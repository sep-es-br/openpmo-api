package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.menu.MenuOfficeDto;
import br.gov.es.openpmo.dto.menu.PlanMenuDto;
import br.gov.es.openpmo.dto.menu.PortfolioMenuRequest;
import br.gov.es.openpmo.dto.menu.WorkpackMenuDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.filter.SortByDirectionEnum;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.office.plan.PlanService;
import br.gov.es.openpmo.service.properties.GetSorterProperty;
import br.gov.es.openpmo.service.workpack.PropertyComparator;
import br.gov.es.openpmo.service.workpack.WorkpackPermissionVerifier;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MenuService {

  private final WorkpackService workpackService;

  private final PlanService planService;

  private final OfficeService officeService;

  private final PersonService personService;

  private final WorkpackPermissionVerifier workpackPermissionVerifier;

  private final GetSorterProperty getSorterProperty;

  @Autowired
  public MenuService(
    final WorkpackService workpackService,
    final PlanService planService,
    final PersonService personService,
    final OfficeService officeService,
    final WorkpackPermissionVerifier workpackPermissionVerifier,
    final GetSorterProperty getSorterProperty
  ) {
    this.personService = personService;
    this.workpackService = workpackService;
    this.planService = planService;
    this.officeService = officeService;
    this.workpackPermissionVerifier = workpackPermissionVerifier;
    this.getSorterProperty = getSorterProperty;
  }

  public Set<WorkpackMenuDto> findAllPortfolio(final PortfolioMenuRequest request) {
    final List<PermissionDto> permissions = this.fetchOfficePermissions(request);

    final boolean hasAnyPermission = this.hasAnyPermission(request.getIdUser(), permissions);

    if (!this.hasOfficePermission(request.getIdUser(), hasAnyPermission, request.getIdOffice())) {
      return Collections.emptySet();
    }

    final List<Plan> plans = this.findAllPlansInOffice(request.getIdOffice()).stream()
      .filter(a -> request.getIdPlan() == null || request.getIdPlan().equals(a.getId()))
      .collect(Collectors.toList());

    final List<WorkpackMenuDto> menus = Collections.synchronizedList(new ArrayList<>(0));

    plans.forEach(plan -> this.addPlanIfHasPermission(request, menus, hasAnyPermission, plan));

    return this.sortMenus(menus);
  }

  public List<MenuOfficeDto> findAllOffice(final Long idUser) {
    this.findPersonById(idUser);

    final List<MenuOfficeDto> menus = new ArrayList<>(0);

    final List<Office> offices = this.officeService.findAll();

    offices.stream().forEach(office -> {
      final List<PermissionDto> permissions = this.fetchOfficePermissions(new PortfolioMenuRequest(office.getId(), idUser));

      final boolean hasPermission = this.hasAnyPermission(idUser, permissions);

      if (this.hasOfficePermission(idUser, hasPermission, office.getId())) {
        final MenuOfficeDto item = MenuOfficeDto.of(office);
        item.setPlans(new ArrayList<>());
        final List<Plan> plans = this.findAllPlansInOffice(office.getId());

        final List<PlanMenuDto> planMenuDtos = item.getPlans();
        for (final Plan plan : plans) {
          if (hasPermission || this.planService.hasPermissionPlanWorkpack(plan.getId(), idUser)) {
            planMenuDtos.add(PlanMenuDto.of(plan));
          }
        }
        final List<PlanMenuDto> sortedPlans = planMenuDtos.stream()
          .distinct()
          .sorted(Comparator.comparing(PlanMenuDto::getStart).reversed())
          .collect(Collectors.toList());
        item.setPlans(sortedPlans);
        menus.add(item);
      }
    });

    menus.sort(Comparator.comparing(MenuOfficeDto::getName));
    return menus;
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

    final boolean hasPlanPermission = command.isHasPlanPermission();
    if (!hasPlanPermission) {
      final Set<Long> permittedWorkpacksId = this.findAllWorkpacksWithPermissions(command.getIdUser(), command.getIdPlan());
      command.getMenus().addAll(this.buildWorkpackStructure(
        workpacks,
        command.getIdPlan(),
        command.getIdUser(),
        false,
        permittedWorkpacksId
      ));
      return;
    }

    command.getMenus().addAll(this.buildWorkpackStructure(
      workpacks,
      command.getIdPlan(),
      command.getIdUser(),
      true,
      new HashSet<>()
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
    final Collection<? extends Workpack> workpacks,
    final Long idPlan,
    final Long idUser,
    final boolean permission,
    final Set<Long> permittedWorkpacksId
  ) {

    final Set<WorkpackMenuDto> generalMenuItem = Collections.synchronizedSet(new HashSet<>(0));

    workpacks.stream().forEach(workpack -> {
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
          generalMenuItem,
          workpack,
          linkedModel
        );
      } else {
        this.addWorkpack(
          idPlan,
          idUser,
          permission,
          permittedWorkpacksId,
          generalMenuItem,
          workpack
        );
      }
    });

    return generalMenuItem;
  }

  private void addLinkedWorkpack(
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
      permission = this.addWorkpackIfPermited(
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
  }

  private Set<WorkpackMenuDto> buildWorkpackLinkedStructure(
    final Iterable<? extends Workpack> children,
    final Long idPlan,
    final Long idUser,
    final boolean permission,
    final Set<Long> idWorkpackStakeholder,
    final WorkpackModel linkedChildrenModel
  ) {
    final Set<WorkpackMenuDto> menu = new HashSet<>(0);

    for (final Workpack workpack : children) {
      final Optional<WorkpackModel> maybeLinkedModelEquivalent =
        this.findWorkpackModelEquivalent(workpack.getWorkpackModelInstance(), linkedChildrenModel.getChildren());

      if (!maybeLinkedModelEquivalent.isPresent()) continue;

      this.addLinkedWorkpack(
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
    final Collection<WorkpackModel> linkedModels
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

    final Collection<WorkpackModel> children = new HashSet<>();

    for (final WorkpackModel linkedModel : linkedModels) {
      children.addAll(linkedModel.getChildren());
    }

    return this.findWorkpackModelEquivalent(model, children);
  }

  private WorkpackModel fetchLinkedModel(
    final Workpack workpack,
    final Long idPlan
  ) {
    return this.workpackService.findWorkpackModelLinked(workpack.getId(), idPlan);
  }

  private void addWorkpack(
    final Long idPlan,
    final Long idUser,
    boolean permission,
    final Set<Long> permittedWorkpacksId,
    final Collection<? super WorkpackMenuDto> generalMenuItem,
    final Workpack workpack
  ) {
    final WorkpackMenuDto currentMenuItem = WorkpackMenuDto.of(
      workpack,
      idPlan,
      this.getSorterProperty.execute(workpack.getId(), idUser)
    );

    permission = this.addWorkpackIfPermited(
      permission,
      permittedWorkpacksId,
      generalMenuItem,
      workpack,
      currentMenuItem
    );
    if (workpack.getChildren() != null) {
      currentMenuItem.setChildren(this.buildWorkpackStructure(
        workpack.getChildren(),
        idPlan,
        idUser,
        permission,
        permittedWorkpacksId
      ));
    }
  }

  private boolean addWorkpackIfPermited(
    boolean permission,
    final Set<Long> permittedWorkpacksId,
    final Collection<? super WorkpackMenuDto> generalMenu,
    final Workpack workpack,
    final WorkpackMenuDto currentMenuItem
  ) {
    if (isWorkpackWithPermission(permittedWorkpacksId, workpack)) {
      permission = true;
    }
    if (permission || this.isChildrenWithPermission(workpack.getChildren(), permittedWorkpacksId)) {
      generalMenu.add(currentMenuItem);
    }
    return permission;
  }

  private boolean isChildrenWithPermission(
    final Iterable<? extends Workpack> workpacks,
    final Set<Long> idWorkpackStakeholder
  ) {
    if (idWorkpackStakeholder.isEmpty()) return false;
    if (workpacks == null) return false;
    for (final Workpack workpack : workpacks) {
      if (isWorkpackWithPermission(idWorkpackStakeholder, workpack)) {
        return true;
      }
      if (workpack.getChildren() != null) {
        if (this.isChildrenWithPermission(workpack.getChildren(), idWorkpackStakeholder)) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean isWorkpackWithPermission(
    final Collection<Long> permittedWorkpacksId,
    final Workpack workpack
  ) {
    return permittedWorkpacksId.contains(workpack.getId());
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
