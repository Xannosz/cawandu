package com.itelg.docker.cawandu.domain.filter;

public interface Filter {
    String getOrderBy();

    Boolean isAscending();

    void setOrderBy(String orderBy, boolean ascending);

    Integer getPage();

    Integer getPageSize();

    void setPageSize(int page, int pageSize);

    Integer getOffset();
}