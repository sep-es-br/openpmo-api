package br.gov.es.openpmo.service.permissions.canaccess;

import java.util.List;

import javax.validation.Valid;

public interface ICanAccessService {

        void ensureCanReadResource(
                        Long id,
                        String authorization);

        void ensureCanReadResourceWorkpack(
            Long idWorkpack,
            String authorization);

        void ensureCanAccessManagementOrReadResource(
                        Long idOffice,
                        String authorization);

        void ensureCanEditResource(
                        @Valid Long id,
                        String authorization);

        void ensureIsAdministrator(String authorization);

        void ensureCanAccessSelfResource(
                        Long idPerson,
                        String authorization);

        void ensureCanAccessManagementResource(
                        Long id,
                        String authorization);

        void ensureCanAccessManagementOrSelfResource(
                        List<Long> ids,
                        String authorization);

        void ensureCanReadManagementResource(
                        Long idOffice,
                        String key,
                        String authorization);

        void ensureCanEditResource(
                        List<Long> ids,
                        String authorization);

}
