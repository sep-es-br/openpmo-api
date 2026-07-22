package br.gov.es.openpmo.model.agreements;

import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Contract extends Agreement {

    public Contract() {
    }

    public Contract(final Long processId, final String object, final Workpack workpack) {
        super(processId, object, workpack);
    }

    @Override
    public AgreementType getType() { return AgreementType.CONTRACT; }
}
