/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.gov.es.openpmo.repository;

import br.gov.es.openpmo.model.workpacks.Instrument;
import java.util.Optional;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author gean.carneiro
 */
@Repository
public interface InstrumentRepository extends Neo4jRepository<Instrument, Long> {
    
//    @Query("MATCH (instrument:Instrument)\n" +
//            "WHERE instrument.SIGEFESCode = $sigefesCode\n" +
//            "RETURN instrument\n" +
//            "LIMIT 1")
    public Optional<Instrument> findBySigefesCode( String sigefesCode);
    
}
