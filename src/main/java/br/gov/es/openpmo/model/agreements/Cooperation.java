package br.gov.es.openpmo.model.agreements;

import br.gov.es.openpmo.model.workpacks.Workpack;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Cooperation extends Agreement {

    public Cooperation() {
    }

    public Cooperation(final String processNumber, final String object, final Workpack workpack) {
        super(processNumber, object, workpack);
    }

    @Override
    public AgreementType getType() { return AgreementType.COOPERATION; }
}
