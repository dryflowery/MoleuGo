package com.Moleugo.moleugo.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "animation_count")
public class AnimationCount {

    // animation_count의 모든 값을 Map으로 반환
    public Map<String, Integer> toMap() {
        Map<String, Integer> ret = new HashMap<>();
        ret.put("linkedList", linkedList);
        ret.put("stack", stack);
        ret.put("queue", queue);
        ret.put("deque", deque);
        ret.put("heap", heap);
        ret.put("binarySearch", binarySearch);
        ret.put("bubbleSort", bubbleSort);
        ret.put("selectionSort", selectionSort);
        ret.put("insertionSort", insertionSort);
        ret.put("dfs", dfs);
        ret.put("bfs", bfs);
        ret.put("dijkstra", dijkstra);
        ret.put("bellmanFord", bellmanFord);
        ret.put("floydWarshall", floydWarshall);
        ret.put("convexHull", convexHull);

        return ret;
    }

    @Id
    private String email;

    @Column(name = "linked_list", nullable = false)
    private int linkedList = 0;

    @Column(nullable = false)
    private int stack = 0;

    @Column(nullable = false)
    private int queue = 0;

    @Column(nullable = false)
    private int deque = 0;

    @Column(nullable = false)
    private int heap = 0;

    @Column(name = "binary_search", nullable = false)
    private int binarySearch = 0;

    @Column(name = "bubble_sort", nullable = false)
    private int bubbleSort = 0;

    @Column(name = "selection_sort", nullable = false)
    private int selectionSort = 0;

    @Column(name = "insertion_sort", nullable = false)
    private int insertionSort = 0;

    @Column(nullable = false)
    private int dfs = 0;

    @Column(nullable = false)
    private int bfs = 0;

    @Column(nullable = false)
    private int dijkstra = 0;

    @Column(name = "bellman_ford", nullable = false)
    private int bellmanFord = 0;

    @Column(name = "floyd_warshall", nullable = false)
    private int floydWarshall = 0;

    @Column(name = "convex_hull", nullable = false)
    private int convexHull = 0;
}
