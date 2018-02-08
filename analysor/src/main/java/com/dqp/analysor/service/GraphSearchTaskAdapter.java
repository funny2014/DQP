package com.dqp.analysor.service;

import com.dqp.analysor.domain.Links;
import com.dqp.analysor.domain.Nodes;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Setter
@Getter
public class GraphSearchTaskAdapter {
    private Set<Nodes> nodesSet = new HashSet<Nodes>(2000);
    private Set<Links> linksSet = new HashSet<Links>(2000);
    private Set<Nodes> returnNodesSet = new HashSet<Nodes>(2000);
    private Set<Links> returnLinksSet = new HashSet<Links>(2000);
    private Set<Links> firstLevelLinksSet = new HashSet<Links>(2000);
    private Nodes currentNode;
    private Links currentLink;
    private Nodes targetNode;
    private int maxDeepLevel = 4;
    private int currentDeepLevel = 0;
    private boolean isChildSearching = false;
    List<Nodes> visited = new ArrayList<Nodes>();


    /**
     * default is parenting searching and deep level is 4
     */
    public GraphSearchTaskAdapter(Set<Nodes> nodesSet, Set<Links> linksSet, int maxDeepLevel, boolean isChildSearching) {
        this.nodesSet = nodesSet;
        this.linksSet = linksSet;
        this.maxDeepLevel = maxDeepLevel;
        this.isChildSearching = isChildSearching;
    }

    /**
     * 广度优先带深度级别图搜索
     * @param startNodeName
     */
    public void widthFirstSeachWithDeepLevel(String startNodeName) {
        Nodes node = getNodesByName(startNodeName);
        if(null == node){
            return;
        }
        returnNodesSet.add(new Nodes(0, node.getName(), node.getValue()));
        assumeDistanceLoop(this.maxDeepLevel, node);


    }

    /**
     * get all the next Level Nodes when doing @methods asumeDistanceLoop()
     */
    private Set<Nodes> getNextLevelNodes(Nodes node, boolean isStoreNode) {
        Set<Nodes> currentLevelNodeSet = new HashSet<>();

        if(null==node){

            log.error("getNextLevelNodes is found error the input node is null.");
        }else {

            if (isStoreNode) {
                returnNodesSet.add(node);
            }
            for (Links link : linksSet) {

                if (isChildSearching) {
                    if (node.getName().equalsIgnoreCase(link.getSource())) {
                        currentLevelNodeSet.add(getNodesByName(link.getTarget()));
                        returnLinksSet.add(link);
                    }

                } else {
                    if (node.getName().equalsIgnoreCase(link.getTarget())) {
                        currentLevelNodeSet.add(getNodesByName(link.getSource()));
                        returnLinksSet.add(link);
                    }
                }


            }
        }
        return currentLevelNodeSet;
    }


    /**
     * Here is a trick
     * 1: when finding the shortest distance between nodes in the non-Direction graph you can using {matrix:floyd} things but...you know..
     * 2: here we can assume that we can already know the distance .then -> loop -> find
     */
    public void assumeDistanceLoop(int assumeDistance, Nodes startNode      /*, Nodes endNode*/) {//>2

        /**
         * storage all the nodes by different level
         * in order
         */
        ArrayList<Set<Nodes>> levelNodesStore = new ArrayList<>(assumeDistance);

        for (int i = 0; i < assumeDistance; i++) {
            /**
             * to store current node set
             */
            Set<Nodes> currentLevelNodeSet = new HashSet<>();

            /**
             * first level do startNode things
             */
            if (i == 0) {
                currentLevelNodeSet.addAll(getNextLevelNodes(startNode, false));
            }
            /**
             * here is not first level then loop last level nodes
             * finding next nodes of last level nodes
             */
            else {
                Set<Nodes> lastLevelNodeStore = levelNodesStore.get(i - 1);
                for (Nodes tmpNode :
                        lastLevelNodeStore) {
//                    if (tmpNode == null || returnLinksSet.size() == 150) {
//                        System.out.print("111");
//                    }
                    currentLevelNodeSet.addAll(getNextLevelNodes(tmpNode, true));
                }
            }
            /**
             * adding to levelNodesStore
             */
            levelNodesStore.add(currentLevelNodeSet);
        }

    }


//    /**
//     * return the shortest distance between nodes
//     *
//     * @param startNode
//     * @param endNode
//     * @return
//     */
//    public int getDistance(Nodes startNode, Nodes endNode) {
//
//
//        for (int i = 1; i < 100; i++) {
//            if (assumeDistanceLoop(i, startNode/* endNode*/)) {
//                return i;
//            }
//        }
//
//
//        return 100;
//    }

    public Nodes getNodesByName(String name) {
        Nodes node = null;
        for (Nodes tnode :
                nodesSet) {
            if (tnode.getName().equalsIgnoreCase(name)) {
                node = tnode;
                break;
            }
        }
        return node;
    }

//    public List<Links> getAllLinks(Nodes nodes) {
//        List<Links> linksList = new ArrayList<>();
//        for (Links link : linksSet) {
//            if (link.getSource().equals(nodes.getName()) || link.getTarget().equals(nodes.getName())) {
//                linksList.add(link);
//            }
//        }
//        return linksList;
//    }

}
