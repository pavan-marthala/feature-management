package org.feature.management.shared.utils;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class SortHelperTest {

    @Test
    void buildSort_NullParam_ReturnsUnsorted() {
        Sort sort = SortHelper.buildSort(null);
        assertThat(sort.isUnsorted()).isTrue();
    }

    @Test
    void buildSort_EmptyParam_ReturnsUnsorted() {
        Sort sort = SortHelper.buildSort("");
        assertThat(sort.isUnsorted()).isTrue();
    }

    @Test
    void buildSort_BlankParam_ReturnsUnsorted() {
        Sort sort = SortHelper.buildSort("   ");
        assertThat(sort.isUnsorted()).isTrue();
    }

    @Test
    void buildSort_SingleProperty_ReturnsAscByProperty() {
        Sort sort = SortHelper.buildSort("name");
        assertThat(sort.getOrderFor("name")).isNotNull();
        assertThat(sort.getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void buildSort_PropertyAndDirectionAsc_ReturnsAscByProperty() {
        Sort sort = SortHelper.buildSort("name,asc");
        assertThat(sort.getOrderFor("name")).isNotNull();
        assertThat(sort.getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void buildSort_PropertyAndDirectionDesc_ReturnsDescByProperty() {
        Sort sort = SortHelper.buildSort("name,desc");
        assertThat(sort.getOrderFor("name")).isNotNull();
        assertThat(sort.getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void buildSort_WithWhitespaceAroundPropertyAndDirection_ReturnsCorrectSort() {
        Sort sort = SortHelper.buildSort("  name  ,  desc  ");
        assertThat(sort.getOrderFor("name")).isNotNull();
        assertThat(sort.getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.DESC);
    }
}
