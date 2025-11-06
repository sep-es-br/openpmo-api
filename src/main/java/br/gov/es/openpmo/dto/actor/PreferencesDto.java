/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.dto.actor;

import br.gov.es.openpmo.model.actors.Person;
import br.gov.es.openpmo.utils.DisplayModeEnum;

/**
 *
 * @author gean.carneiro
 */
public class PreferencesDto {
    
    private Long officeId;
    private Long workpackId;
    private Long idWorkpackModelLinked;
    private Integer pageSize;
    private Boolean fixedMenu;
    private DisplayModeEnum displayMode;

    public Long getOfficeId() {
        return officeId;
    }

    public void setOfficeId(Long officeId) {
        this.officeId = officeId;
    }

    public Long getWorkpackId() {
        return workpackId;
    }

    public void setWorkpackId(Long workpackId) {
        this.workpackId = workpackId;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Boolean getFixedMenu() {
        return fixedMenu;
    }

    public void setFixedMenu(Boolean fixedMenu) {
        this.fixedMenu = fixedMenu;
    }

    public DisplayModeEnum getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(DisplayModeEnum displayMode) {
        this.displayMode = displayMode;
    }

    public Long getIdWorkpackModelLinked() {
        return idWorkpackModelLinked;
    }

    public void setIdWorkpackModelLinked(Long idWorkpackModelLinked) {
        this.idWorkpackModelLinked = idWorkpackModelLinked;
    }
    
    public static PreferencesDto of(Person person){
        if(person == null) return null;
        
        PreferencesDto preferences = new PreferencesDto();
        
        preferences.setOfficeId(person.getIdOffice());
        preferences.setWorkpackId(person.getIdWorkpack());
        preferences.setIdWorkpackModelLinked(person.getIdWorkpackModelLinked());
        preferences.setPageSize(person.getPageSize());
        preferences.setFixedMenu(person.getFixedMenu());
        preferences.setDisplayMode(person.getDisplayMode());
        
        return preferences;
    }
    
    
}
