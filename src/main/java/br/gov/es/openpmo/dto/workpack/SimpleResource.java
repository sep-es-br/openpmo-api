package br.gov.es.openpmo.dto.workpack;

public class SimpleResource {

  private final Long id;
  private final String name;
  private final String fullName;

  public SimpleResource(final Long id, final String name, final String fullName) {
    this.id = id;
    this.name = name;
    this.fullName = fullName;
  }

  public static SimpleResource of(final Long id, final String name, final String fullName) {
    return new SimpleResource(id, name, fullName);
  }

  public Long getId() {
    return this.id;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.fullName;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;

    final SimpleResource that = (SimpleResource) o;

    return this.id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }
}
