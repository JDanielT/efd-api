package br.com.jbssistemas.efdclient.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {

    @Builder.Default
    private List<T> items = new ArrayList<>();

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int pageSize = 15;

    @Builder.Default
    private int pages = 0;

    @Builder.Default
    private long totalRecords = 0;

}
