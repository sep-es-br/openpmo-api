package br.gov.es.openpmo.repository;

import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.openpmo.model.CostAccount;
import br.gov.es.openpmo.model.Workpack;

public interface CostAccountRepository extends Neo4jRepository<CostAccount, Long> {
    @Query("MATCH (w:Workpack) "
               + " WHERE ID(w) = $idWorkpack "
               + " RETURN w, [ "
               + " [(w)<-[i:APPLIES_TO]-(c:CostAccount) | [i, c] ], "
               + " [(c)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) | [f1, p1, d1, pm1] ], "
               + " [(p2)-[v1:VALUES]->(o:Organization) | [v1, o] ], "
               + " [(p2)-[v2:VALUES]-(l:Locality) | [v2, l] ], "
               + " [(p2)-[v3:VALUES]-(u:UnitMeasure) | [v3, u] ], "
               + " [(w)-[i:IS_IN*]->(w2:Workpack) | [i, w2] ], "
               + " [(w2)<-[i:APPLIES_TO]-(c2:CostAccount) | [i, c2] ], "
               + " [(c2)<-[f2:FEATURES]-(p2:Property)-[d2:IS_DRIVEN_BY]->(pm2:PropertyModel)  | [f2, p2, d2, pm2] ] "
               + "]")
    Optional<Workpack> findWorkpackWithCosts(@Param("idWorkpack") Long idWorkpack);

    @Query("MATCH (c:CostAccount)-[i:APPLIES_TO]->(w:Workpack) "
               + " WHERE ID(c) = $id "
               + " RETURN c, i, w, [ "
               + " [(c)<-[f1:FEATURES]-(p1:Property)-[d1:IS_DRIVEN_BY]->(pm1:PropertyModel) | [f1, p1, d1, pm1] ], "
               + " [(p1)-[v1:VALUES]->(o:Organization) | [v1, o] ], "
               + " [(p1)-[v2:VALUES]-(l:Locality) | [v2, l] ], "
               + " [(p1)-[v3:VALUES]-(u:UnitMeasure) | [v3, u] ] "
               + "]")
    Optional<CostAccount> findByIdWithPropertyModel(@Param("id") Long id);
}
