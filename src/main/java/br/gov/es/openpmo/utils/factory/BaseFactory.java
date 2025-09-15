/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.openpmo.utils.factory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author gean.carneiro
 * @param <T> classe do model
 * @param <U> classe do dto
 */
public abstract class BaseFactory<T, U> {
    
    private final ConcurrentHashMap<Long, T> cacheModel = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, U> cacheDto = new ConcurrentHashMap<>();
    private final Class<T> modelClass;
    private final Class<U> dtoClass;
    
    protected abstract T createModel(U dto);
    protected abstract Long getModelId(T model);
    protected abstract U createDto(T model);
    protected abstract Long getDtoId(U dto);
    
    protected BaseFactory(Class<T> modelClass, Class<U> dtoClass) {
        this.modelClass = modelClass;
        this.dtoClass = dtoClass;
    }
    
    public void clear() {
        this.cacheDto.clear();
        this.cacheModel.clear();
    }
    
    public T fromDto(U dto){
        if (dto == null) return null;
        T model = this.cacheModel.computeIfAbsent(getDtoId(dto), (id) -> this.createModel(dto));
        
        return model;
    }
        
    public Set<T> fromDto(Set<U> dto) {
        
        return mapSet(dto, this::fromDto);
    }
        
    public List<T> fromDto(List<U> dto) {
        
        return mapList(dto, this::fromDto);
    }
    
    public U fromModel(T model) {
        
        if(model == null) return null;
        
        U dto = this.cacheDto.computeIfAbsent(getModelId(model), (id) -> createDto(model));
        
        
        return dto;
    }
    
    public Set<U> fromModel(Set<T> model) {
        
        return mapSet(model, this::fromModel);
                
    }
    
    public List<U> fromModel(List<T> model) {
        
        return mapList(model, this::fromModel);
                
    }
    
    private <X, Y> Set<X> mapSet(Set<Y> source, Function<Y, X> mapper){
        if(source == null) return null;
        return source.stream().map(mapper).collect(Collectors.toSet());
    }
    
    private <X, Y> List<X> mapList(List<Y> source, Function<Y, X> mapper){
        if(source == null) return null;
        return source.stream().map(mapper).collect(Collectors.toList());
    }
    
    private T newModel() {
        try {
            return this.modelClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException ex){
            throw new RuntimeException("Classe model " + this.modelClass.getSimpleName() + " não possui construtor sem argumentos");
        } catch (Exception ex) {
            throw new RuntimeException(ex.getLocalizedMessage(), ex);
        }
    }
    
    private U newDto() {
        try {
            return this.dtoClass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException ex){
            throw new RuntimeException("Classe dto " + this.dtoClass.getSimpleName() + " não possui construtor sem argumentos");
        } catch (Exception ex) {
            throw new RuntimeException(ex.getLocalizedMessage(), ex);
        }
    }
    
}
