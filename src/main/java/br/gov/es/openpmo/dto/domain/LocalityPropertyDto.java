package br.gov.es.openpmo.dto.domain;

import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class LocalityPropertyDto implements Comparable<LocalityPropertyDto> {

  private Long id;
  private String name;
  private String fullName;
  private Set<LocalityPropertyDto> children;

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(final String fullName) {
    this.fullName = fullName;
  }

  public Set<LocalityPropertyDto> getChildren() {
    return this.children;
  }

  public void setChildren(final Set<LocalityPropertyDto> children) {
    this.children = children;
  }

  @Override
  public int compareTo(final LocalityPropertyDto other) {
    if (Objects.isNull(other.name)) return 1;
    if (Objects.isNull(this.name)) return -1;
    final String otherNameCleaned = other.name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);
    final String selfNameCleaned = this.name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);
    return selfNameCleaned.compareToIgnoreCase(otherNameCleaned);
  }

  public void sort() {
    if (CollectionUtils.isEmpty(this.children)) return;
    this.children = this.children.stream()
      .sorted()
      .collect(Collectors.toCollection(LinkedHashSet::new));
    this.children.forEach(LocalityPropertyDto::sort);
  }
}
