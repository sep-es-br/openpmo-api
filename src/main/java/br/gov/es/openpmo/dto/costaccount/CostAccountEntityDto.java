package br.gov.es.openpmo.dto.costaccount;

import br.gov.es.openpmo.model.workpacks.CostAccount;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class CostAccountEntityDto {

    private Long id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;

    private Integer codUo;

    private String unidadeOrcamentaria;

    private Integer codPo;

    private String planoOrcamentario;

    public CostAccountEntityDto() {

    }

    public CostAccountEntityDto(final Long id) {
        this.id = id;
    }

    public CostAccountEntityDto(
            Long id,
            String name,
            Integer codUo,
            String unidadeOrcamentaria,
            Integer codPo,
            String planoOrcamentario
    ) {
        this.id = id;
        this.name = name;
        this.codUo = codUo;
        this.unidadeOrcamentaria = unidadeOrcamentaria;
        this.codPo = codPo;
        this.planoOrcamentario = planoOrcamentario;
    }

    public static CostAccountEntityDto of(final CostAccount costAccount) {
        return new CostAccountEntityDto(costAccount.getId());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCodUo() {
        return codUo;
    }

    public void setCodUo(Integer codUo) {
        this.codUo = codUo;
    }

    public String getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(String unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public Integer getCodPo() {
        return codPo;
    }

    public void setCodPo(Integer codPo) {
        this.codPo = codPo;
    }

    public String getPlanoOrcamentario() {
        return planoOrcamentario;
    }

    public void setPlanoOrcamentario(String planoOrcamentario) {
        this.planoOrcamentario = planoOrcamentario;
    }
}
