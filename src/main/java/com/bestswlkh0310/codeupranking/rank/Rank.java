package com.bestswlkh0310.codeupranking.rank;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Rank {
    private String name;
    private String rank;
    private Map<String, Integer> detail;
    private List<List<Object>> solve;
}
