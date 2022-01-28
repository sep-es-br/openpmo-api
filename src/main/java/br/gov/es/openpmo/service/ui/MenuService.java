package br.gov.es.openpmo.service.ui;

import br.gov.es.openpmo.dto.menu.MenuOfficeDto;
import br.gov.es.openpmo.dto.menu.PlanMenuDto;
import br.gov.es.openpmo.dto.menu.PortfolioMenuRequest;
import br.gov.es.openpmo.dto.menu.WorkpackMenuDto;
import br.gov.es.openpmo.dto.permission.PermissionDto;
import br.gov.es.openpmo.dto.workpack.PropertyDto;
import br.gov.es.openpmo.dto.workpack.WorkpackDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.details.WorkpackModelDetailDto;
import br.gov.es.openpmo.dto.workpackmodel.params.properties.PropertyModelDto;
import br.gov.es.openpmo.exception.NegocioException;
import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.model.office.Office;
import br.gov.es.openpmo.model.office.plan.Plan;
import br.gov.es.openpmo.model.properties.Property;
import br.gov.es.openpmo.model.relations.BelongsTo;
import br.gov.es.openpmo.model.workpacks.Workpack;
import br.gov.es.openpmo.model.workpacks.models.WorkpackModel;
import br.gov.es.openpmo.service.actors.PersonService;
import br.gov.es.openpmo.service.office.OfficeService;
import br.gov.es.openpmo.service.office.plan.PlanService;
import br.gov.es.openpmo.service.workpack.WorkpackModelService;
import br.gov.es.openpmo.service.workpack.WorkpackPermissionVerifier;
import br.gov.es.openpmo.service.workpack.WorkpackService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static br.gov.es.openpmo.enumerator.Session.PROPERTIES;
import static br.gov.es.openpmo.service.workpack.WorkpackService.getValueProperty;
import static br.gov.es.openpmo.utils.ApplicationMessage.WORKPACK_MODEL_TYPE_MISMATCH;

@Service
public class MenuService {

  private final WorkpackService workpackService;
  private final PlanService planService;
  private final OfficeService officeService;
  private final ModelMapper modelMapper;
  private final PersonService personService;
  private final WorkpackModelService workpackModelService;
  private final WorkpackPermissionVerifier workpackPermissionVerifier;

  @Autowired
  public MenuService(
    final WorkpackService workpackService,
    final PlanService planService,
    final PersonService personService,
    final OfficeService officeService,
    final ModelMapper modelMapper,
    final WorkpackModelService workpackModelService,
    final WorkpackPermissionVerifier workpackPermissionVerifier
  ) {
    this.personService = personService;
    this.workpackService = workpackService;
    this.planService = planService;
    this.officeService = officeService;
    this.modelMapper = modelMapper;
    this.workpackModelService = workpackModelService;
    this.workpackPermissionVerifier = workpackPermissionVerifier;
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

    if(propertyName == null) return Optional.empty();

    return workpack.getProperties().stream()
      .filter(Objects::nonNull)
      .filter(property -> propertyName.getId().equals(property.getId()))
      .findFirst();
  }

  private static boolean isWorkpackWithPermission(final Collection<Long> permittedWorkpacksId, final Workpack workpack) {
    return permittedWorkpacksId.contains(workpack.getId());
  }

  public List<WorkpackMenuDto> findAllPortfolio(final PortfolioMenuRequest request) {
    final List<PermissionDto> permissions = this.fetchOfficePermissions(request);

    final boolean hasAnyPermission = this.hasAnyPermission(request.getIdUser(), permissions);

    if(this.hasOfficePermission(request.getIdUser(), hasAnyPermission, request.getIdOffice())) {
      final List<Plan> plans = this.findAllPlansInOffice(request.getIdOffice());

      final List<WorkpackMenuDto> menus = new ArrayList<>(0);

      plans.forEach(plan -> this.addPlanIfHasPermission(
        request,
        menus,
        hasAnyPermission,
        plan
      ));

      return menus;
    }
    return Collections.emptyList();
  }

  private void addPlanIfHasPermission(
    final PortfolioMenuRequest portfolioMenuRequest,
    final List<? super WorkpackMenuDto> menus,
    final boolean hasAnyPermission,
    final Plan plan
  ) {
    boolean hasPlanPermission = hasAnyPermission;

    final Long idPlan = plan.getId();

    if(!hasAnyPermission) {
      hasPlanPermission = this.hasPlanPermission(portfolioMenuRequest.getIdUser(), idPlan);
    }

    if(hasPlanPermission || this.planService.hasPermissionPlanWorkpack(idPlan, portfolioMenuRequest.getIdUser())) {
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

  private Set<Long> findAllWorkpacksWithPermissions(final Long idUser, final Long idPlan) {
    return this.workpackService.findAllWorkpacksWithPermissions(
      idPlan,
      idUser
    );
  }

  private boolean hasPlanPermission(final Long idUser, final Long idPlan) {
    return this.planService.hasPermissionPlan(idPlan, idUser);
  }

  public List<MenuOfficeDto> findAllOffice(final Long idUser) {
    this.findPersonById(idUser);

    final List<MenuOfficeDto> menus = new ArrayList<>(0);

    final List<Office> offices = this.officeService.findAll();

    offices.forEach(office -> {

      final List<PermissionDto> permissions = this.fetchOfficePermissions(new PortfolioMenuRequest(office.getId(), idUser));

      final boolean hasPermission = this.hasAnyPermission(idUser, permissions);

      if(this.hasOfficePermission(idUser, hasPermission, office.getId())) {
        final MenuOfficeDto item = this.modelMapper.map(office, MenuOfficeDto.class);
        item.setPlans(new HashSet<>());
        final List<Plan> plans = this.findAllPlansInOffice(office.getId());

        for(final Plan plan : plans) {
          if(hasPermission || this.planService.hasPermissionPlanWorkpack(plan.getId(), idUser)) {
            item.getPlans().add(this.modelMapper.map(plan, PlanMenuDto.class));
          }
        }
        menus.add(item);
      }
    });
    return menus;
  }

  private boolean hasOfficePermission(final Long idUser, final boolean hasPermission, final Long idOffice) {
    return hasPermission || this.officeService.hasPermissionPlanWorkpack(idOffice, idUser);
  }

  private List<PermissionDto> fetchOfficePermissions(final PortfolioMenuRequest request) {
    final Office office = this.findOfficeById(request.getIdOffice());
    final Person person = this.findPersonById(request.getIdUser());
    return this.workpackPermissionVerifier.fetchOfficePermissions(office, person);
  }


  private Office findOfficeById(final Long idOffice) {
    return this.officeService.findById(idOffice);
  }

  private boolean hasAnyPermission(final Long idUser, final Collection<PermissionDto> permissions) {
    final Person person = this.findPersonById(idUser);
    return person.getAdministrator() || (permissions != null && !permissions.isEmpty());
  }

  private Person findPersonById(final Long idUser) {
    return this.personService.findById(idUser);
  }

  private List<Plan> findAllPlansInOffice(final Long idOffice) {
    return this.planService.findAllInOffice(idOffice);
  }

  private Set<WorkpackMenuDto> buildWorkpackStructure(
    final Iterable<? extends Workpack> workpacks,
    final Long idPlan,
    final Long idUser,
    boolean permission,
    final Set<Long> permittedWorkpacksId
  ) {

    final Set<WorkpackMenuDto> menu = new HashSet<>(0);

    for(final Workpack workpack : workpacks) {

      final Set<BelongsTo> workpackBelongsToRelation = workpack.getBelongsTo();
      final boolean isLinked = workpackBelongsToRelation.stream()
        .anyMatch(belongsTo -> idPlan.equals(belongsTo.getIdPlan()) && Boolean.TRUE.equals(belongsTo.getLinked()));

      if(isLinked) {
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
      }
      else {
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
    final WorkpackMenuDto menuItemDto = this.modelMapper.map(workpack, WorkpackMenuDto.class);
    final WorkpackDetailDto detailDto = this.workpackService.getWorkpackDetailDto(workpack);

    final WorkpackModelDetailDto modelDetailDto = detailDto.getModel();

    menuItemDto.setIdPlan(idPlan);
    menuItemDto.setFontIcon(modelDetailDto.getFontIcon());
    menuItemDto.setIdWorkpackModelLinked(linkedModel.getId());

    if(modelDetailDto.hasProperties()) {
      permission = this.addPropertyName(
        permission,
        permittedWorkpackId,
        menu,
        workpack,
        menuItemDto,
        detailDto,
        modelDetailDto
      );
    }
    if(workpack.getChildren() != null) {
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

    for(final Workpack workpack : children) {
      // TODO: comparar propriedade `modelName` do `Workpack`
      final WorkpackModel linkedModelEquivalent = this.findWorkpackModelEquivalent(
        workpack.getWorkpackModelInstance(),
        linkedChildrenModel.getChildren()
      ).orElseThrow(() -> new NegocioException(WORKPACK_MODEL_TYPE_MISMATCH));

      permission = this.addLinkedWorkpack(
        idPlan,
        idUser,
        permission,
        idWorkpackStakeholder,
        menu,
        workpack,
        linkedModelEquivalent
      );
    }
    return menu;
  }

  private Optional<WorkpackModel> findWorkpackModelEquivalent(final WorkpackModel model, final Set<WorkpackModel> linkedModels) {
    for(final WorkpackModel linkedModel : linkedModels) {
      if(model.hasSameType(linkedModel)) {
        return Optional.of(linkedModel);
      }
      if(linkedModel.hasChildren()) {
        return this.findWorkpackModelEquivalent(model, linkedModels);
      }
    }
    return Optional.empty();
  }

  private WorkpackModel fetchLinkedModel(final Workpack workpack, final Long idPlan) {
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
    final WorkpackMenuDto menuItemDto = this.modelMapper.map(workpack, WorkpackMenuDto.class);
    final WorkpackDetailDto detailDto = this.workpackService.getWorkpackDetailDto(workpack);
    final WorkpackModelDetailDto model = detailDto.getModel();

    menuItemDto.setIdPlan(idPlan);
    menuItemDto.setFontIcon(model.getFontIcon());

    if(model.hasProperties()) {
      permission = this.addPropertyName(
        permission,
        permittedWorkpacksId,
        menu,
        workpack,
        menuItemDto,
        detailDto,
        model
      );
    }
    if(workpack.getChildren() != null) {
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
    final WorkpackMenuDto menuItem,
    final WorkpackDetailDto detailDto,
    final WorkpackModelDetailDto model
  ) {
    final Optional<Property> maybeProperty = equivalentPropertyOfWorkpack(workpack, detailDto, model);

    if(maybeProperty.isPresent()) {
      menuItem.setName((String) getValueProperty(maybeProperty.get()));
      if(isWorkpackWithPermission(permittedWorkpacksId, workpack)) {
        permission = true;
      }
      if(permission || this.isChildrenWithPermission(workpack.getChildren(), permittedWorkpacksId)) {
        menu.add(menuItem);
      }
    }
    return permission;
  }

  private boolean isChildrenWithPermission(final Iterable<? extends Workpack> workpacks, final Set<Long> idWorkpackStakeholder) {
    if(workpacks == null) return false;
    for(final Workpack workpack : workpacks) {
      if(isWorkpackWithPermission(idWorkpackStakeholder, workpack)) {
        return true;
      }
      if(workpack.getChildren() != null) {
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
