package br.gov.es.openpmo.service.organization;

import br.gov.es.openpmo.model.actors.Organization;
import br.gov.es.openpmo.model.actors.OrganizationEnum;
import br.gov.es.openpmo.repository.OrganizationRepository;
import br.gov.es.pmo.organization_parser.pmo_base.model.IOrganizationParser;
import br.gov.es.pmo.organization_parser.pmo_base.model.OrganizationDto;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrganizationSyncService {

    private final OrganizationRepository repository;
    private final ObjectProvider<IOrganizationParser<String>> organizationParser;
    private final OrganizationTokenService organizationTokenService;

    @Autowired
    public OrganizationSyncService(
            final OrganizationRepository repository,
            final ObjectProvider<IOrganizationParser<String>> organizationParser,
            final OrganizationTokenService organizationTokenService
    ) {
        this.repository = repository;
        this.organizationParser = organizationParser;
        this.organizationTokenService = organizationTokenService;
    }

    @Scheduled(cron = "${app.scheduler.everyday-at-8am}")
    public void syncOrganizations() {

        try {

            System.out.println(
                    "Iniciando sincronização de organizações..."
            );

            IOrganizationParser<String> parser =
                    organizationParser.getIfAvailable();

            if (parser == null) {
                System.out.println(
                        "Nenhum parser disponível."
                );
                return;
            }

            String token =
                    organizationTokenService.fetchSystemToken();

            List<OrganizationDto> orgDtos =
                    parser.getOrganizations(token);

            System.out.println(
                    "Total parser: " + orgDtos.size()
            );

            if (orgDtos.isEmpty()) {
                return;
            }

            List<String> guids =
                    orgDtos.stream()
                            .map(OrganizationDto::getGuid)
                            .collect(Collectors.toList());

            List<Organization> existingOrganizations =
                    repository.findByGuidIn(guids);

            Map<String, Organization> organizationMap =
                    existingOrganizations.stream()
                            .collect(Collectors.toMap(
                                    Organization::getGuid,
                                    org -> org
                            ));

            List<Organization> organizationsToSave =
                    new ArrayList<>();

            for (OrganizationDto dto : orgDtos) {

                Organization organization =
                        organizationMap.get(dto.getGuid());

                if (organization == null) {
                    organization = new Organization();
                    organization.setGuid(dto.getGuid());
                }

                OrganizationEnum organizationEnum = OrganizationEnum.valueOf(dto.getSector());
                organization.setSector(organizationEnum);
                organization.setName(dto.getName());
                organization.setFullName(dto.getFullName());
                organization.setIntegration(dto.getIntegration());
                organization.setSuffix(dto.getSuffix());

                organizationsToSave.add(organization);
            }

            repository.saveAll(organizationsToSave);

            System.out.println(
                    "Salvos/Atualizados: "
                            + organizationsToSave.size()
            );

        } catch (Exception e) {

            System.err.println(
                    "Erro ao sincronizar organizações"
            );

            e.printStackTrace();
        }
    }
}
