package br.gov.es.openpmo.apis.edocs;

import br.gov.es.openpmo.apis.edocs.response.ProcessHistoryResponse;
import br.gov.es.openpmo.apis.edocs.response.ProcessResponse;

import java.io.IOException;
import java.util.List;

public interface EDocsApi {

    ProcessResponse findProcessByProtocol(String protocol, Long idPerson);

    List<ProcessHistoryResponse> findProcessHistoryById(String id, Long idPerson);

    boolean isProcessPriority(String processId, Long personId) throws IOException;

}
