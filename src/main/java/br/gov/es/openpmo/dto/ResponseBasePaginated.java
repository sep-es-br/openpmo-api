package br.gov.es.openpmo.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.domain.Page;

import java.util.List;

@ApiModel(description = "Model responsável por encapsular todas as responses paginadas da api.")
@JsonPropertyOrder({"success", "data", "pagination"})
public class ResponseBasePaginated<T> {

  @ApiModelProperty(position = 1)
  private boolean success = true;
  @ApiModelProperty(position = 2)
  private List<T> data;
  @ApiModelProperty(position = 3)
  private Pagination pagination;


  public static <T> ResponseBasePaginated<T> of(final Page<T> data) {
    final ResponseBasePaginated<T> pageResponse = new ResponseBasePaginated<>();
    pageResponse.data = data.getContent();
    pageResponse.pagination = Pagination.of(data);
    return pageResponse;
  }

  public Pagination getPagination() {
    return this.pagination;
  }

  public void setPagination(final Pagination pagination) {
    this.pagination = pagination;
  }

  public List<T> getData() {
    return this.data;
  }

  public void setData(final List<T> data) {
    this.data = data;
  }

  public boolean isSuccess() {
    return this.success;
  }

  public void setSuccess(final boolean success) {
    this.success = success;
  }

  public static class Pagination {

    private final int page;

    private final int pageSize;

    private final int totalPages;

    private final long totalRecords;

    public Pagination(final int page, final int pageSize, final int totalPages, final long totalRecords) {
      this.page = page;
      this.pageSize = pageSize;
      this.totalPages = totalPages;
      this.totalRecords = totalRecords;
    }

    public static Pagination of(final Page<?> page) {
      return new Pagination(
        page.getNumber(),
        page.getSize(),
        page.getTotalPages(),
        page.getTotalElements()
      );
    }

    public int getPage() {
      return this.page;
    }

    public int getPageSize() {
      return this.pageSize;
    }

    public int getTotalPages() {
      return this.totalPages;
    }

    public long getTotalRecords() {
      return this.totalRecords;
    }
  }

}
