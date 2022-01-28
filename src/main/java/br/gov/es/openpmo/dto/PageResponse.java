package br.gov.es.openpmo.dto;

import org.springframework.data.domain.Slice;

import java.util.List;

public class PageResponse<T> {

  private List<T> data;

  private int page;

  private int pageSize;

  public static <T> PageResponse<T> of(final Slice<T> data) {
    final PageResponse<T> pageResponse = new PageResponse<>();
    pageResponse.data = data.getContent();
    pageResponse.page = data.getNumber();
    pageResponse.pageSize = data.getSize();
    return pageResponse;
  }

  public List<T> getData() {
    return this.data;
  }

  public void setData(final List<T> data) {
    this.data = data;
  }

  public int getPage() {
    return this.page;
  }

  public void setPage(final int page) {
    this.page = page;
  }

  public int getPageSize() {
    return this.pageSize;
  }

  public void setPageSize(final int pageSize) {
    this.pageSize = pageSize;
  }

}
