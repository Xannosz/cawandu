package com.itelg.docker.cawandu.domain.filter;

import lombok.Data;

@Data
public abstract class AbstractFilter implements Filter {
    private String orderBy;
    private Boolean ascending;
    private Integer page;
    private Integer pageSize;

    @Override
    public Boolean isAscending() {
        return ascending;
    }

    @Override
    public void setOrderBy(String orderBy, boolean ascending) {
        this.orderBy = orderBy;
        this.ascending = ascending;
    }

    @Override
    public void setPageSize(int page, int pageSize) {
        setPage(page);
        setPageSize(pageSize);
    }

    @Override
    public Integer getOffset() {
        return getPageSize() * (getPage() - 1);
    }
}