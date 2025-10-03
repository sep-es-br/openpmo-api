/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.service.workpack;

import br.gov.es.openpmo.model.workpacks.Instrument;
import br.gov.es.openpmo.repository.InstrumentRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author gean.carneiro
 */
@Service
public class InstrumentService {
    
    private final InstrumentRepository instrumentRepository;
    
    @Autowired
    public InstrumentService(
        InstrumentRepository instrumentRepository
    ) {
        this.instrumentRepository = instrumentRepository;
    }
    
    public Instrument findOrCreate(Instrument instrument) {
        
        return instrumentRepository
                    .findBySigefesCode(instrument.getSigefesCode())
                    .map(Instrument::getId)
                    .flatMap(instrumentRepository::findById)
                    .orElse(instrument);
        
    }
    
    public List<Instrument> findOrCreateAll(List<Instrument> instruments) {
        
        return instruments.stream().map(this::findOrCreate).collect(Collectors.toList());
                
        
    }
    
    
}
