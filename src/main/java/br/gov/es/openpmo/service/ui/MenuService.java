package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.menu.MenuOfficeDto;
import br.gov.es.openpmo.dto.menu.PlanMenuDto;
import br.gov.es.openpmo.dto.menu.PlanWorkpackDto;
import br.gov.es.openpmo.dto.menu.PortfolioMenuRequest;
import br.gov.es.openpmo.dto.menu.WorkpackMenuDto;
import br.gov.es.openpmo.dto.menu.WorkpackMenuResultDto;
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
import br.gov.es.openpmo.utils.ApplicationCacheUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

  private final ApplicationCacheUtil applicationCacheUtil;

  private final Collator collator;

  @Autowired
  public MenuService(
    final WorkpackService workpackService,
    final PlanService planService,
    final PersonService personService,
    final OfficeService officeService,
    final WorkpackPermissionVerifier workpackPermissionVerifier,
    final GetSorterProperty getSorterProperty,
    final ApplicationCacheUtil applicationCacheUtil
  ) {
    this.personService = personService;
    this.workpackService = workpackService;
    this.planService = planService;
    this.officeService = officeService;
    this.workpackPermissionVerifier = workpackPermissionVerifier;
    this.getSorterProperty = getSorterProperty;
    this.applicationCacheUtil = applicationCacheUtil;
    this.collator = Collator.getInstance();
    this.collator.setStrength(Collator.PRIMARY);
  }

  public Set<WorkpackMenuResultDto> findAllPortfolioCached(final PortfolioMenuRequest request) {
    Long idOffice = request.getIdOffice();
    Long idPerson = request.getIdUser();
    final Office office = this.officeService.findByIdThin(idOffice);
    final Person person = this.personService.findByIdThinElseThrow(idPerson);

    final List<PermissionDto> permissions = this.workpackPermissionVerifier.fetchOfficePermissions(office, person);
    final List<Long> plansWithPermission = this.planService.findAllUserHasPermission(idOffice, idPerson);
    final List<PlanWorkpackDto> permissionWorkpack = this.workpackService
          .findAllMappedByPlanWithPermission(idOffice, idPerson);

    boolean admin = person.getAdministrator();
    boolean officePermission = !permissions.isEmpty();
    boolean planPermission = !plansWithPermission.isEmpty();
    boolean workpackPermission = !permissionWorkpack.isEmpty();

    if (!admin && !officePermission && !planPermission && !workpackPermission) {
      return Collections.emptySet();
    }

    List<Long> plans = new ArrayList<>(0);
    if (request.getIdPlan() != null) {
      plans.add(request.getIdPlan());
    }
    if (plans.isEmpty()) {
      plans = this.planService.findAllIdsInOfficeOrderByStartDesc(idOffice);
    }

    if (admin || officePermission) {
      return findAllPortifolio(plans);
    }

    return findAllPortifolio(plansWithPermission, permissionWorkpack);
  }

  private Set<WorkpackMenuResultDto> findAllPortifolio(List<Long> plans) {
    Set<WorkpackMenuResultDto> result = new LinkedHashSet<>(0);
    for (Long plan : plans) {
      result.addAll(this.getListWorkpackMenuResultDtoFull(plan));
    }
    return sortMenusChildren(result);
  }

  private Set<WorkpackMenuResultDto> sortMenusChildren(final Collection<WorkpackMenuResultDto> menus) {
    for (final WorkpackMenuResultDto menu : menus) {
      if (!menu.getChildren().isEmpty()) {
        menu.setChildren(this.sortMenusChildren(menu.getChildren()));
      }
    }

    Map<ModelPosition, List<WorkpackMenuResultDto>> mapMenu = getMapMenuPosition(menus);
    List<ModelPosition> models = new ArrayList<>(mapMenu.keySet());
    models.sort(Comparator.comparing(ModelPosition::getPosition, Comparator.nullsLast(Comparator.naturalOrder())));
    Set<WorkpackMenuResultDto> result = new LinkedHashSet<>(0);
    for (ModelPosition model : models) {
      List<WorkpackMenuResultDto> menuModel = mapMenu.get(model);
      if (menuModel.stream().anyMatch(m -> m.getSort() instanceof String)) {
        result.addAll(menuModel.stream()
              .sorted(Comparator.comparing(WorkpackMenuResultDto::getIdWorkpackModel, Comparator.nullsLast(Comparator.naturalOrder()))
                                .thenComparing((a, b) -> this.collator.compare(a.getSort(), b.getSort())))
              .collect(Collectors.toCollection(LinkedHashSet::new)));
        continue;
      }
      result.addAll(menuModel.stream()
                             .sorted(Comparator.comparing(WorkpackMenuResultDto::getIdWorkpackModel, Comparator.nullsLast(Comparator.naturalOrder()))
                                               .thenComparing((a,b) -> this.compareTo(a.getSort(), b.getSort())))
                             .collect(Collectors.toCollection(LinkedHashSet::new)));
    }
    return result;
  }

  private Map<ModelPosition, List<WorkpackMenuResultDto>> getMapMenuPosition(Collection<WorkpackMenuResultDto> menus) {
    final Map<ModelPosition, List<WorkpackMenuResultDto>> map = new HashMap<>(0);
    if (CollectionUtils.isNotEmpty(menus)) {
      menus.forEach(m -> {
        ModelPosition key = new ModelPosition(m.getIdWorkpackModel(), m.getPosition());
        map.computeIfAbsent(key, k -> new ArrayList<>(0));
        map.get(key).add(m);
      });
    }
    return map;
  }

  private int compareTo(Comparable a, Comparable b) {
    if ( a == null )
      return b == null ? 0 : 1;

    if ( b == null )
      return 1;

    return a.compareTo(b);
  }

  private List<WorkpackMenuResultDto> getListWorkpackMenuResultDtoFull(Long idPlan) {
    final List<WorkpackMenuResultDto> listWorkpackMenus = applicationCacheUtil.getListWorkpackMenuResultDto(idPlan);
    for (WorkpackMenuResultDto planMenu : listWorkpackMenus) {
      if (planMenu.getIdParent() != null) {
        listWorkpackMenus.stream().filter(w -> planMenu.getIdParent().equals(w.getId())).findFirst().ifPresent(
            parent -> parent.getChildren().add(planMenu));
      }
    }
    return listWorkpackMenus.stream().filter(w -> w.getIdParent() == null).collect(Collectors.toList());
  }

  private Set<WorkpackMenuResultDto> findAllPortifolio(final List<Long> plansWithPermission
      , final List<PlanWorkpackDto> permissionWorkpack) {
    Set<WorkpackMenuResultDto> result = new LinkedHashSet<>(0);
    if (!plansWithPermission.isEmpty()) {
      for (Long plan : plansWithPermission) {
        result.addAll(this.getListWorkpackMenuResultDtoFull(plan));
      }
    }
    List<PlanWorkpackDto> workpackDtos = permissionWorkpack.stream()
            .filter(wm -> plansWithPermission.stream().noneMatch(id -> wm.getIdPlan().equals(id)))
            .collect(Collectors.toList());

    for (PlanWorkpackDto planWorkpack : workpackDtos) {
      List<WorkpackMenuResultDto> listWorkpackMenus = applicationCacheUtil.getListWorkpackMenuResultDto(planWorkpack.getIdPlan());
      for (Long idWorkpack : planWorkpack.getWorkpacks()) {
        WorkpackMenuResultDto workpack = this.getWorkpackMenuResultDto(idWorkpack, listWorkpackMenus);
        result.add(workpack);
      }
    }
    return sortMenusChildren(result);
  }

  private WorkpackMenuResultDto getWorkpackMenuResultDto(Long idWorkpack, final List<WorkpackMenuResultDto> listWorkpackMenus) {
    WorkpackMenuResultDto workpack = listWorkpackMenus.stream().filter(w -> w.getId().equals(idWorkpack))
                                                      .findFirst().orElse(null);
    if (workpack != null) {
      workpack.setChildren(this.getChildren(workpack.getId(), listWorkpackMenus));
      if (workpack.getIdParent() != null) {
        return this.getParent(workpack, listWorkpackMenus);
      }
    }
    return workpack;
  }

  private WorkpackMenuResultDto getParent(WorkpackMenuResultDto workpack, List<WorkpackMenuResultDto> listWorkpackMenus) {
    WorkpackMenuResultDto parent = listWorkpackMenus.stream().filter(w -> w.getId().equals(workpack.getIdParent()))
                                                    .findFirst().orElse(null);
    if (parent != null) {
      parent.getChildren().add(workpack);
      if (parent.getIdParent() != null) {
        return this.getParent(parent, listWorkpackMenus);
      }
      return parent;
    }
    return workpack;
  }

  private Set<WorkpackMenuResultDto> getChildren(Long idWorkpack, final List<WorkpackMenuResultDto> listWorkpackMenus) {
    Set<WorkpackMenuResultDto> children = new LinkedHashSet<>(0);
    children.addAll(listWorkpackMenus.stream().filter(w -> idWorkpack.equals(w.getIdParent())).collect(
        Collectors.toSet()));
    if (!children.isEmpty()) {
      for (WorkpackMenuResultDto child : children) {
        child.setChildren(this.getChildren(child.getId(), listWorkpackMenus));
      }
    }
    return children;
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

    offices.forEach(office -> {
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

    workpacks.forEach(workpack -> {
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

  class ModelPosition {

    private Long idModel;
    private Long position;

    public ModelPosition(Long idModel, Long position) {
      this.idModel = idModel;
      this.position = position;
    }

    public Long getIdModel() {
      return idModel;
    }

    public void setIdModel(Long idModel) {
      this.idModel = idModel;
    }

    public Long getPosition() {
      return position;
    }

    public void setPosition(Long position) {
      this.position = position;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      ModelPosition that = (ModelPosition) o;
      return Objects.equals(idModel, that.idModel);
    }

    @Override
    public int hashCode() {
      return Objects.hash(idModel);
    }
  }

}
