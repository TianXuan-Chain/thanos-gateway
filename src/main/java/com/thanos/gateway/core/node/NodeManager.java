package com.thanos.gateway.core.node;

import com.google.gson.Gson;
import com.thanos.api.proto.broadcast.NodesBroadcastDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NodeManager {
    private static final Logger LOG = LoggerFactory.getLogger("node");

    //自身节点配置
    private Node mySelf;
    //集群节点配置
    private Map<String, Node> cluster = new ConcurrentHashMap<>();
    private Node pushNode;

    //构造N叉树，N默认为3
    private final static Integer N = 3;
    private volatile List<Node> nForkList = new ArrayList<>();
    private Map<String, Integer> nForkMap = new ConcurrentHashMap<>();

    //随机节点生成器
    private final static Random random = new Random();

    private final static Gson gson = new Gson();

    public NodeManager(String nodeStr, List<String> clusterList, String pushStr) {
        if (StringUtils.isNotBlank(nodeStr)) {
            String[] strs = nodeStr.split(":");
            Node node = new Node();
            node.setId(strs[0]);
            node.setIp(strs[1]);
            node.setPort(Integer.valueOf(strs[2]));
            mySelf = node;
            cluster.put(node.getId(), node);
        }
        if (CollectionUtils.isNotEmpty(clusterList)) {
            for (String str : clusterList) {
                String[] info = str.split(":");
                Node node = new Node();
                node.setId(info[0]);
                node.setIp(info[1]);
                node.setPort(Integer.valueOf(info[2]));
                cluster.put(node.getId(), node);
            }
        }
        if (StringUtils.isNotBlank(pushStr)) {
            String[] strs = pushStr.split(":");
            Node node = new Node();
            node.setIp(strs[0]);
            node.setPort(Integer.valueOf(strs[1]));
            pushNode = node;
        }
        //更新N叉树
        makeNforkTree();
    }

    public Node getMySelf() {
        return mySelf;
    }

    public List<Node> getCluster() {
        return new ArrayList<>(cluster.values());
    }

    public List<Node> addNode(List<NodesBroadcastDTO.NodeBroadcastDTO> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            return Collections.emptyList();
        }

        List<Node> newNodes = new ArrayList<>();

        for (NodesBroadcastDTO.NodeBroadcastDTO node : nodes) {
            if (!cluster.containsKey(node.getId())) {
                Node n = new Node();
                n.setId(node.getId());
                n.setIp(new String(node.getIp().getBytes()));
                n.setPort(node.getPort());
                cluster.put(node.getId(), n);
                newNodes.add(n);
            }

        }

        if (newNodes.size() != 0) {
            //clearNforkTree();
            makeNforkTree();
        }
        return newNodes;
    }

    public void removeNode(long id) {
        Node d = cluster.get(id);
        if (d != null) {
            cluster.remove(id);
            //clearNforkTree();
            makeNforkTree();
        }
    }

    public void makeNforkTree() {
        List<Node> nodes = new ArrayList<>(cluster.values());
        Collections.sort(nodes, (o1, o2) -> o1.getId().compareTo(o2.getId()) < 0  ? -1 : 1);

        nForkList = nodes;
        nForkMap.clear();
        for (int i = 0; i < nodes.size(); i++) {
            nForkMap.put(nForkList.get(i).getId(), i + 1);
        }

        LOG.debug("NodeManager makeNforkTree, data:{}", gson.toJson(nForkList));
    }


    public List<Node> getSubNode(String nodeId) {
        int idx = nForkMap.get(nodeId);
        List<Integer> subs = new ArrayList<>();
        int bw = idx * N;
        if (2 != N) {
            int ix = 1;
            for (int i = 0; i < N - 2; i++) {
                int su = bw - ix;
                subs.add(su);
                ix++;
            }
        }
        subs.add(bw);
        subs.add(bw + 1);

        List<Node> snapshot = nForkList;
        List<Node> result = new ArrayList<>();
        for (Integer c : subs) {
            if (c > snapshot.size()) {
                continue;
            }
            Node e = snapshot.get(c - 1);
            if (e == null) {
                continue;
            }
            result.add(e);
        }
        return result;
    }

    public Node getRoot() {
        List<Node> snapshot = nForkList;
        return snapshot.get(0);
    }

    public List<Node> getRandoms() {
        List<Node> snapshot = nForkList;
        if (snapshot.size() < 10) {
            return new ArrayList<>();
        }
        Double d = ((double) snapshot.size() * 0.1d);
        int a = d.intValue() > 0 ? d.intValue() : 1;
        List<Node> result = new ArrayList<>();
        for (int i = 0; i < a; i++) {
            int n = random.nextInt(snapshot.size());
            result.add(snapshot.get(n));
        }
        return result;
    }

    public Node getPushNode() {
        return pushNode;
    }

    public void setPushNode(Node pushNode) {
        this.pushNode = pushNode;
    }

}
